package net.leanix.metrics.excelimport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.leanix.dropkit.apiclient.ApiException;
import net.leanix.metrics.api.PointsApi;
import net.leanix.metrics.api.models.Field;
import net.leanix.metrics.api.models.Point;
import net.leanix.metrics.api.models.Tag;
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
	
	private void saveMeasurement(List<Measurement> measurementList) throws ApiException {
		for(Measurement measurement: measurementList){
			//create Client
			net.leanix.dropkit.apiclient.ApiClient metricsClient = new net.leanix.dropkit.apiclient.ApiClientBuilder()
					.withBasePath(String.format("https://%s/services/metrics/v1", measurement.getHost())).withTokenProviderHost(measurement.getHost())
					.withApiToken(measurement.getToken()).withDebugging(debug).build();
			PointsApi pointsApi = new PointsApi(metricsClient);
			Point point = new Point();
			point.setMeasurement(measurement.getName());
			point.setWorkspaceId(measurement.getWorkspaceID());
			point.setTime(measurement.getDate());

			Map<String, Double> listOfFields = measurement.getListOfFields();
			for(Map.Entry<String, Double> entry : listOfFields.entrySet()){
				Field field = new Field();
				field.setK(entry.getKey());
				field.setV(entry.getValue());
				point.getFields().add(field);
			}
			
			Map<String, String> listOfTags = measurement.getListOfTags();
			for(Map.Entry<String, String> entry : listOfTags.entrySet()){
				Tag tag = new Tag();
				tag.setK(entry.getKey());
				tag.setV(entry.getValue());
				point.getTags().add(tag);
			}
			pointsApi.createPoint(point);
		}
	}
}