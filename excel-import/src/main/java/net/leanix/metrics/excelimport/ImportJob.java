package net.leanix.metrics.excelimport;

import java.util.List;
import net.leanix.metrics.excelimport.Measurement;

/**
 * Import of a excel sheet to metrics
 *
 */
public class ImportJob {
	
	private boolean debug;
	
	public ImportJob(Measurement measurement, boolean debug) throws NullPointerException {
		this.debug = debug;
	}

	public void run() throws Exception {
//		List<Measurement> measurementList = getMeasurementList();
//		if (debug) {
//			measurementList.forEach(System.out::println);
//		}
//		saveMeasurement(measurementList);
	}

	private List<Measurement> getMeasurementList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void saveMeasurement(List<Measurement> measurementList) {
		// TODO Auto-generated method stub
		
	}
}