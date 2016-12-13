package com.polymorph.soaringcoach.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.polymorph.soaringcoach.Flight;
import com.polymorph.soaringcoach.FlightTestFacade;

public class TestUtilities {
	
	public static void testHasBeenRun(AAnalysis a, Flight f) throws AnalysisException {
		if (f == null) {
			f = new FlightTestFacade(null);
		}
		
		assertFalse(a.hasBeenRun(f));
		
		a.performAnalysis(f);
		
		assertTrue(a.hasBeenRun(f));
	}
}
