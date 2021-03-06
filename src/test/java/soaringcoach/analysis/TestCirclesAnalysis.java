/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   SoaringCoach is a tool for analysing IGC files produced by modern FAI
 *   flight recorder devices, and providing the pilot with useful feedback
 *   on how effectively they are flying.    
 *   Copyright (C) 2017 Johan Pretorius
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   The author can be contacted via email at pretoriusjf@gmail.com, or 
 *   by paper mail by addressing as follows: 
 *      Johan Pretorius 
 *      PO Box 990 
 *      Durbanville 
 *      Cape Town 
 *      7551
 *      South Africa
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package soaringcoach.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import soaringcoach.Circle;
import soaringcoach.Flight;
import soaringcoach.FlightAnalyser.FlightMode;
import soaringcoach.FlightAnalyserTestFacade;
import soaringcoach.FlightTestFacade;
import soaringcoach.Thermal;

public class TestCirclesAnalysis {

	
	/**
	 * This set of fixes includes: <br>
	 * - an S-turn (i.e. half a circle left, immediately followed by half a
	 * circle right)<br>
	 * - four separate full circles strung together, all in the same turn direction
	 * <br>
	 * - part of a circle at the end of the series<br>
	 * Having several circles immediately following each other, also tests the
	 * modulus arithmetic around 360/0 degrees.
	 * @throws Exception 
	 */
	@Test
	public void testCirclingAnalysisPositive() throws Exception {
		ArrayList<GNSSPoint> igc_points = FlightAnalyserTestFacade.loadFromFile(
				"src/test/resources/circling_detection.igc").igc_points;
		
		Flight f = new FlightTestFacade(igc_points);
		
		CirclesAnalysis ca = new CirclesAnalysis();
		f = ca.performAnalysis(f);
		
		//Check # of turns
		assertEquals("incorrect number of circles detected", 4, f.circles.size());
		
		//Check individual turn durations
		Circle t1 = f.circles.get(0);
		assertEquals("first circle duration", 32, t1.duration);
		
		Circle t2 = f.circles.get(1);
		assertEquals("second circle duration", 24, t2.duration);
		
		Circle t3 = f.circles.get(2);
		assertEquals("third circle duration", 32, t3.duration);
		
		Circle t4 = f.circles.get(3);
		assertEquals("fourth circle duration", 28, t4.duration);
	}

	/**
	 * Test with a set of points that make up an S turn, without ever quite completing a circle 
	 * @throws Exception
	 */
	@Test
	public void testCirclingDetectionDiscard() throws Exception {
		ArrayList<GNSSPoint> points = new ArrayList<>();
		
		points = FlightAnalyserTestFacade.loadFromFile(
				"src/test/resources/testCirclingDetectionDiscard.igc").igc_points;
		
		Flight f = new FlightTestFacade(points); 
		
		CirclesAnalysis ca = new CirclesAnalysis();
		f = ca.performAnalysis(f);
		
		assertEquals("number of turns", 0, f.circles.size());
	}

	@Test
	public void testHasBeenRun() throws AnalysisException {
		CirclesAnalysis ca = new CirclesAnalysis();
		Flight f = new FlightTestFacade(new ArrayList<GNSSPoint>());
		f.is_circles_analysis_complete = false;
		assertFalse(ca.hasBeenRun(f));
		f.is_circles_analysis_complete = true;
		assertTrue(ca.hasBeenRun(f));
	}
	

	@Test
	/**
	 * Accurately determine circle start lat/lon/heading/time tuple for a set of circles with no wind
	 * 
	 * @throws FileNotFoundException
	 */
	public void testDetermineCircleStartNoWind() throws Exception {
		ArrayList<GNSSPoint> igc_points = FlightAnalyserTestFacade.loadFromFile(
				"src/test/resources/DetermineCircleStartNoWind.igc").igc_points;
		
		Flight f = new FlightTestFacade(igc_points);
		
		double[] circle_start_lat_expected = {50.76617, 50.76655};
		
		double[] circle_start_lon_expected = {3.877017, 3.877617};
		
		double[] circle_start_heading_expected = {90, 90};
		
		String[] circle_start_timestamp_expected = {"10:43:06", "10:43:29"};
		
		CirclesAnalysis ca = new CirclesAnalysis();
		ca.performAnalysis(f);
		ArrayList<Circle> circles = f.circles;
		
		assertEquals("Number of circles", 2, circles.size());
		
		int i = 0;
		for (Circle circle : circles) {
			assertEquals(
					"Circle at index [" + i + "], timestamp [" + circle.getTimestamp() + "]", 
					circle_start_lat_expected[i], 
					circle.getCircleStartLatitude(), 
					0.0001);
			
			assertEquals(
					"Circle at index [" + i + "], timestamp [" + circle.getTimestamp() + "]", 
					circle_start_lon_expected[i],
					circle.getCircleStartLongitude(),
					0.0001);
			
			assertEquals(
					"Circle at index [" + i + "], timestamp [" + circle.getTimestamp() + "]", 
					circle_start_heading_expected[i],
					circle.circle_start_course,
					0.1);
			
			assertEquals(
					"Circle at index [" + i + "], timestamp [" + circle.getTimestamp() + "]", 
					circle_start_timestamp_expected[i],
					circle.getTimestamp());
			
			i += 1;
		}
	}

	@Test
	/**
	 * Accurately determine circle start lat/lon/heading/time tuple for a set of 
	 * circles with howling gale
	 * 
	 */
	public void testDetermineCircleStartHowlingGale() throws Exception {
		ArrayList<GNSSPoint> igc_points = FlightAnalyserTestFacade.loadFromFile(
				"src/test/resources/DetermineCircleStartHowlingGale.igc").igc_points;
		
		Flight f = new FlightTestFacade(igc_points);
		
		double[] circle_start_lat_expected = { 50.76625, 50.76707, 50.7667166666667, 50.7669 };

		double[] circle_start_lon_expected = { 3.88011, 3.88427, 3.886, 3.88817 };

		double[] circle_start_heading_expected = { 88.1, 88.1, 88.1, 88.1 };

		String[] circle_start_timestamp_expected = { "10:40:02", "10:40:17", "10:40:27", "10:40:37" };

		CirclesAnalysis ca = new CirclesAnalysis();
		ca.performAnalysis(f);
		ArrayList<Circle> circles = f.circles;
		
		assertEquals("Number of circles", 4, circles.size());
		
		int i = 0;
		for (Circle circle : circles) {
			assertEquals("Circle at index " + i, 
					circle_start_lat_expected[i], 
					circle.getCircleStartLatitude(), 
					0.0001);
			
			assertEquals("Circle at index " + i, 
					circle_start_lon_expected[i],
					circle.getCircleStartLongitude(),
					0.0001);
			
			assertEquals("Circle at index " + i, 
					circle_start_heading_expected[i],
					circle.circle_start_course,
					0.1);
			
			assertEquals("Circle at index " + i, 
					circle_start_timestamp_expected[i],
					circle.getTimestamp());
			
			i += 1;
		}
	}
	
	/**
	 * Flight contains 8 circles, first 4 are LH circles all strung together
	 * with a substantial correction between circles 2&3. The second thermal is
	 * the same except that the circles are made towards the right.
	 * 
	 * The purpose of this test is to make sure that CirclingAnalysis can deal
	 * with a mid-thermal centering move, still accepting that the elongated
	 * circle is a proper circle even though the turn rate certainly decayed
	 * below the threshold for a moment.
	 * 
	 * @throws AnalysisException
	 * @throws IOException
	 */
	@Test
	public void testCentringMoveIgnored() throws AnalysisException, IOException {
		ArrayList<GNSSPoint> igc_points = FlightAnalyserTestFacade.loadFromFile(
				"src/test/resources/CenteringMoveTest.igc").igc_points;
		
		Flight f = new FlightTestFacade(igc_points);
		
		f = new CirclesAnalysis().performAnalysis(f);
		f = new ThermalAnalysis().performAnalysis(f);
		
		assertEquals(2, f.thermals.size());
		Thermal t1 = f.thermals.get(0);
		Thermal t2 = f.thermals.get(1);
		
		Circle c = f.circles.get(0);
		assertEquals(21, c.duration);
		assertEquals(FlightMode.TURNING_LEFT, c.turn_direction);
		assertTrue(t1.circles.contains(c));
		
		c = f.circles.get(1);
		assertEquals(17, c.duration);
		assertEquals(FlightMode.TURNING_LEFT, c.turn_direction);
		assertTrue(t1.circles.contains(c));
		
		c = f.circles.get(2);
		assertEquals(23, c.duration);
		assertEquals(FlightMode.TURNING_LEFT, c.turn_direction);
		assertTrue(t1.circles.contains(c));
		
		c = f.circles.get(3);
		assertEquals(16, c.duration);
		assertEquals(FlightMode.TURNING_LEFT, c.turn_direction);
		assertTrue(t1.circles.contains(c));
		
		
		
		c = f.circles.get(4);
		assertEquals(21, c.duration);
		assertEquals(FlightMode.TURNING_RIGHT, c.turn_direction);
		assertTrue(t2.circles.contains(c));
		
		c = f.circles.get(5);
		assertEquals(19, c.duration);
		assertEquals(FlightMode.TURNING_RIGHT, c.turn_direction);
		assertTrue(t2.circles.contains(c));
		
		c = f.circles.get(6);
		assertEquals(23, c.duration);
		assertEquals(FlightMode.TURNING_RIGHT, c.turn_direction);
		assertTrue(t2.circles.contains(c));
		
		c = f.circles.get(7);
		assertEquals(17, c.duration);
		assertEquals(FlightMode.TURNING_RIGHT, c.turn_direction);
		assertTrue(t2.circles.contains(c));

		
		
		//Left-hand turning drift vectors
		/*
		assertEquals(9.5, f.circles.get(1).drift_vector.bearing, 3.0);
		assertEquals(8.0, f.circles.get(1).drift_vector.size, 2.0);
		
		assertEquals(221.6, f.circles.get(2).drift_vector.bearing, 3.0);
		assertEquals(79.0, f.circles.get(2).drift_vector.size, 2.0);
		
		assertEquals(342.8, f.circles.get(3).drift_vector.bearing, 3.0);
		assertEquals(11.0, f.circles.get(3).drift_vector.size, 2.0);
		*/
	}
}
