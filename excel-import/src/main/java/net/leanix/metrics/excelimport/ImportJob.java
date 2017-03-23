package net.leanix.metrics.excelimport;

import java.io.IOException;
import java.util.List;
import net.leanix.metrics.excelimport.Measurement;

/**
 * Import of a excel sheet to metrics
 *
 */
public class ImportJob {
	
	private boolean debug;
	private String path;
	
	public ImportJob(String path, boolean debug) throws NullPointerException {
		this.path = path;
		this.debug = debug;
	}

	public void run() throws Exception {
		List<Measurement> measurementList = getMeasurementList();
		if (debug) {
			System.out.println("\n");
			measurementList.forEach(System.out::println);
		}
		saveMeasurement(measurementList);
	}

	private List<Measurement> getMeasurementList() throws IOException {
		return new ReadExcel().readExcel(path, debug);
	}
	
	private void saveMeasurement(List<Measurement> measurementList) {
		// TODO Auto-generated method stub
		
	}
}