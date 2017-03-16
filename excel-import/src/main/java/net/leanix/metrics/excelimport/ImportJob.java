package net.leanix.metrics.excelimport;

import java.util.List;
import java.util.Objects;
import net.leanix.dropkit.apiclient.ApiClient;
import net.leanix.metrics.excelimport.Measurement;

/**
 * Import of a excel sheet to metrics
 *
 */
public class ImportJob {
	
	private final ApiClient metricsClient;
	private final String workspaceId; // for metrics api's
	private final String path; // for excel sheet
	private boolean debug;
	
	public ImportJob(ApiClient metricsClient, String workspaceId, String path, boolean debug) throws NullPointerException {
		this.metricsClient = Objects.requireNonNull(metricsClient);
		this.workspaceId = Objects.requireNonNull(workspaceId);
		this.path = Objects.requireNonNull(workspaceId);
		this.debug = debug;
	}

	public void run() throws Exception {
		List<Measurement> measurementList = getMeasurementList();
		if (debug) {
			measurementList.forEach(System.out::println);
		}
		saveMeasurement(measurementList);
	}

	private List<Measurement> getMeasurementList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void saveMeasurement(List<Measurement> measurementList) {
		// TODO Auto-generated method stub
		
	}
}