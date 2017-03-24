package net.leanix.metrics.dashboard.dataquality;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.leanix.api.BusinessCapabilitiesApi;
import net.leanix.api.ServicesApi;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.BusinessCapability;
import net.leanix.api.models.FactSheetHasChild;
import net.leanix.api.models.Service;
import net.leanix.api.models.ServiceHasBusinessCapability;
import net.leanix.metrics.api.PointsApi;
import net.leanix.metrics.api.models.Field;
import net.leanix.metrics.api.models.Point;
import net.leanix.metrics.api.models.Tag;

public final class ImportJob {

	private final net.leanix.api.common.ApiClient apiClient;
	private final net.leanix.dropkit.apiclient.ApiClient metricsClient;
	private final String workspaceId;
	private final String measurement;
	private final boolean debug;

	public ImportJob(net.leanix.api.common.ApiClient apiClient, net.leanix.dropkit.apiclient.ApiClient metricsClient,
			String workspaceId, String measurement, boolean debug) throws NullPointerException {
		this.apiClient = Objects.requireNonNull(apiClient);
		this.metricsClient = Objects.requireNonNull(metricsClient);
		this.workspaceId = Objects.requireNonNull(workspaceId);
		this.measurement = Objects.requireNonNull(measurement);
		this.debug = debug;
	}

	public void run() throws Exception {
		List<DataQuality> measurement = getMeasurement();
		if (debug) {
			measurement.forEach(System.out::println);
		}
		saveMeasurement(measurement);
	}

	private List<DataQuality> getMeasurement() throws ApiException {
		BusinessCapabilitiesApi bcApi = new BusinessCapabilitiesApi(apiClient);
		ServicesApi servicesApi = new ServicesApi(apiClient);
		List<DataQuality> dataQualityList = new ArrayList<>();
		// read all services
		List<Service> allServices = servicesApi.getServices(false, null);
		Map<String, Service> allServicesAsMap = allServices.stream()
				.collect(Collectors.toMap(Service::getID, Function.identity()));
		// read all business capabilities
		List<BusinessCapability> allBCs = bcApi.getBusinessCapabilities(true, null);
		Map<String, BusinessCapability> allBCsAsMap = allBCs.stream()
				.collect(Collectors.toMap(BusinessCapability::getID, Function.identity()));
		allBCs.forEach((bc) -> {
			DataQuality dataQuality = new DataQuality();
			dataQuality.setFactsheetId(bc.getID());
			dataQuality.setDisplayName(bc.getDisplayName());
			// get services of a business capability
			List<Service> services = getServicesFromBC(bc, allBCsAsMap, allServicesAsMap);
			services.forEach((s) -> {
				double completion = 1.0d;
				try {
					completion = Double.parseDouble(s.getCompletion());
				} catch (Exception e) {
					// ignore
				}
				dataQuality.addToSumOfComplete(completion);
				if (completion < 1.0d) {
					dataQuality.incrementIncomplete();
				} else {
					dataQuality.incrementComplete();
				}
			});
			dataQualityList.add(dataQuality);
		});
		return dataQualityList;
	}

	private List<Service> getServicesFromBC(BusinessCapability bc, Map<String, BusinessCapability> allBCs,
			Map<String, Service> allServices) {
		List<Service> result = new ArrayList<>();
		List<ServiceHasBusinessCapability> serviceRefs = bc.getServiceHasBusinessCapabilities();
		if (serviceRefs != null) {
			serviceRefs.forEach((ref) -> {
				Service s = allServices.get(ref.getServiceID());
				if (s != null) {
					result.add(s);
				}
			});
		}
		List<FactSheetHasChild> children = bc.getFactSheetHasChildren();
		if (children != null) {
			children.forEach((child) -> {
				BusinessCapability bcChild = allBCs.get(child.getFactSheetID());
				if (bcChild != null) {
					List<Service> servicesChild = getServicesFromBC(bcChild, allBCs, allServices);
					result.addAll(servicesChild);
				}
			});
		}
		return result;
	}

	private void saveMeasurement(List<DataQuality> measurementList) throws net.leanix.dropkit.apiclient.ApiException {
		PointsApi pointsApi = new PointsApi(metricsClient);
		Date current = new Date();
		for (DataQuality dataQuality : measurementList) {
			Point point = new Point();
			point.setMeasurement(measurement);
			point.setWorkspaceId(workspaceId);
			point.setTime(current);

			Field field = new Field();
			field.setK("complete (count, absolute)");
			field.setV(Double.valueOf(dataQuality.getComplete()));

			Field field2 = new Field();
			field2.setK("not complete (count, absolute)");
			field2.setV(Double.valueOf(dataQuality.getIncomplete()));

			Field field3 = new Field();
			field3.setK("complete (count, relative)");
			field3.setV(Double.valueOf(dataQuality.getCompleteInPercent()));

			Field field4 = new Field();
			field4.setK("not complete (count, relative)");
			field4.setV(Double.valueOf(dataQuality.getIncompleteInPercent()));

			Field field5 = new Field();
			field5.setK("complete (average of values)");
			field5.setV(Double.valueOf(dataQuality.getAvgOfComplete()));

			Field field6 = new Field();
			field6.setK("not complete (average of values)");
			field6.setV(Double.valueOf(dataQuality.getAvgOfIncomplete()));

			point.getFields().add(field);
			point.getFields().add(field2);
			point.getFields().add(field3);
			point.getFields().add(field4);
			point.getFields().add(field5);
			point.getFields().add(field6);

			Tag tag = new Tag();
			tag.setK("factsheetId");
			tag.setV(dataQuality.getFactsheetId());

			point.getTags().add(tag);

			pointsApi.createPoint(point);
		}
	}
}