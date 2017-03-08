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

package soaringcoach.rest;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import soaringcoach.analysis.GNSSPoint;

/**
 * Created by russel on 15/12/10.
 */
@CrossOrigin(origins = "*")
@RestController
public class FileUploadController {

    ArrayList<GNSSPoint> gnssPointList;

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload( @RequestParam("file") MultipartFile file){

        gnssPointList = new ArrayList<>();

        if (!file.isEmpty()) {
            try {
				// TODO perhaps move most of this logic into FlightAnalyser? It
				// may be easier to work with this code (and the app), if the
				// steps are something like:
            	//
            	// 1. Get file from user
            	// 2. Throw file at FlightAnalyser, which persists it to the DB and kicks off calculation
            	// 3. Get notified once calculation is complete.  Somehow tell the user about this event
            	// 4. User either clicks on the flight or some refresh button to get updated aggregate stats
            	
            	/*
                GNSSPoint gnssPoint;
                
                String line;
                InputStream is = file.getInputStream();
                LineInputStream lis = new LineInputStream(is);
                try {
	                while ((line = lis.readLine()) != null) {
	                    gnssPoint = GNSSPoint.createGNSSPoint(file.getName(), line);
	                    if(gnssPoint != null) {
	                        gnssPointList.add(gnssPoint);
	                    }
	                }
	            } finally {
	            	lis.close();
	            }*/

              /*  FlightAnalyser flightAnalyser = new FlightAnalyser(gnssPointList);
                
                double totalDistance = flightAnalyser.calcTotalDistance();

                ArrayList<Circle> turns = flightAnalyser.analyseCircling();
                
                int turn_count = turns.size();
*/                
                //get turn durations display string
                //String turn_details = "";
                //for (Turn turn : turns) {
                //	turn_details += "\t";
				//	turn_details += turn.toString();
				//	turn_details += "\n";
				//}
                
               /* ArrayList<Thermal> thermals = flightAnalyser.calculateThermals();
                int thermal_count = thermals.size();
                
				String thermal_details = "";
				for (Thermal thermal : thermals) {
					thermal_details += "\t";
					thermal_details += thermal.toString();
					thermal_details += "\n";
					
					int i = 1;
					for (Circle turn : thermal.getCircles()) {
						thermal_details += "\t\t" + i++ + ".  ";
						thermal_details += turn.toString();
						thermal_details += "\n";
					}
					
					thermal_details += "\n";
				}
				
				
				return "Ground Track Distance = ["+String.valueOf(Math.round(totalDistance)/1000.0)+"] kilometers.\n\n"
					+ "Total thermals detected = ["+thermal_count+"]\n\n"
                	+ "Total circles detected = ["+turn_count+"]\n\n"
        			+ "Thermal details = \n"+thermal_details +"\n\n";*/
                return "";
				
            } catch (Exception e) {
                return "You failed to upload " + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload  because the file was empty.";
        }
    }
}