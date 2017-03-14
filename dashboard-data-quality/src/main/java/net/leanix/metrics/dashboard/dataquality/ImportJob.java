package net.leanix.metrics.dashboard.dataquality;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import net.leanix.api.BusinessCapabilitiesApi;
import net.leanix.api.ServicesApi;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.BusinessCapability;
import net.leanix.api.models.Service;
import net.leanix.api.models.ServiceHasBusinessCapability;
import net.leanix.metrics.api.PointsApi;
import net.leanix.metrics.api.models.Field;
import net.leanix.metrics.api.models.Point;
import net.leanix.metrics.api.models.Tag;

public final class ImportJob {

	private final net.leanix.api.common.ApiClient apiClient;
	private final net.leanix.dropkit.apiclient.ApiClient metricsClient;
	private final String workspaceId; // for metrics api's
	private final boolean debug;

	public ImportJob(net.leanix.api.common.ApiClient apiClient, net.leanix.dropkit.apiclient.ApiClient metricsClient,
			String workspaceId, boolean debug) throws NullPointerException {
		this.apiClient = Objects.requireNonNull(apiClient);
		this.metricsClient = Objects.requireNonNull(metricsClient);
		this.workspaceId = Objects.requireNonNull(workspaceId);
		this.debug = debug;
	}

	public void run() throws Exception {
		ArrayList<DataQuality> measurementList = new ArrayList<DataQuality>();
		// read business capabilities
		measurementList = getDataQualityList();
		if(debug) {
			for(DataQuality dataQuality: measurementList) {
				System.out.println("Ergebnis: " + dataQuality.getBusinessCapabilityID() + ";" + dataQuality.getDisplayName() + ";" + dataQuality.getIncompleteApps() + ";" + dataQuality.getCompleteApps());
			}
		}
		setMeasurement(measurementList);
	}

	/**
	 * Read business capabilities
	 * @return 
	 * @throws ApiException
	 */
	private ArrayList<DataQuality> getDataQualityList() throws ApiException {
		BusinessCapabilitiesApi businessCapabilitiesApi = new BusinessCapabilitiesApi(apiClient);
		List<BusinessCapability> businessCapabilities;
		ServicesApi servicesApi = new ServicesApi(apiClient);
		ArrayList<DataQuality> dataQualityList = new ArrayList<DataQuality>();
		int i = 0;
		int j = 0;
		//read business capabilities
		businessCapabilities = businessCapabilitiesApi.getBusinessCapabilities(true, null);
		for (BusinessCapability businessCapability : businessCapabilities) {
			//Objekt zum Merken
			DataQuality dataQuality = new DataQuality();
			dataQuality.setBusinessCapabilityID(businessCapability.getID());
			dataQuality.setDisplayName(businessCapability.getDisplayName());
			if (debug) {
				System.out.println(businessCapability.toString());
			}
			//read services of business capabilities
			List<ServiceHasBusinessCapability> serviceHasBusinessCapabilities = businessCapability.getServiceHasBusinessCapabilities();
			for(ServiceHasBusinessCapability serviceHasBusinessCapability: serviceHasBusinessCapabilities) {
				String id = serviceHasBusinessCapability.getServiceID();
				Service service = servicesApi.getService(id, null);
				String completion = service.getCompletion();
				double parseCompletion = Double.parseDouble(completion);
				if(parseCompletion < 1) {
					i++;
				}
				else {
					j++;
				}
			}
			dataQuality.setIncompleteApps(i);
			dataQuality.setCompleteApps(j);
			dataQualityList.add(dataQuality);
		}
		return dataQualityList;
	}
	
	/**
	 * save the measuremnt
	 * @param measurementList
	 * @throws ApiException 
	 * @throws net.leanix.dropkit.apiclient.ApiException 
	 */
	private void setMeasurement(ArrayList<DataQuality> measurementList) throws ApiException, net.leanix.dropkit.apiclient.ApiException {
		
		PointsApi pointsApi = new PointsApi(metricsClient);
		for(DataQuality dataQuality: measurementList) {
			Point point = new Point();
			point.setMeasurement("dashboard-data-quality");
			point.setWorkspaceId(workspaceId);
			point.setTime(new Date());
			
			Field field = new Field();
			field.setK("not complete");
			field.setV(dataQuality.getIncompleteApps());
			
			Field field2 = new Field();
			field2.setK("complete");
			field2.setV(dataQuality.getCompleteApps());
			
			point.getFields().add(field);
			point.getFields().add(field2);
			
			Tag tag =  new Tag();
			tag.setK("factsheetId");
			tag.setV(dataQuality.getBusinessCapabilityID());
			
			point.getTags().add(tag);
			
			pointsApi.createPoint(point);
		}
		
	}
}