/**
 * 
 */
package net.leanix.metrics.dashboard.applicationdeployment;

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

/**
 * @author jsmr
 *
 */
public class ImportJob {
	
	private final net.leanix.api.common.ApiClient apiClient;
	private final net.leanix.dropkit.apiclient.ApiClient metricsClient;
	private final String workspaceId; // for metrics api's
	private final boolean debug;
	private List<String> tagsOfService;

	public ImportJob(net.leanix.api.common.ApiClient apiClient, net.leanix.dropkit.apiclient.ApiClient metricsClient,
			String workspaceId, boolean debug) throws NullPointerException {
		this.apiClient = Objects.requireNonNull(apiClient);
		this.metricsClient = Objects.requireNonNull(metricsClient);
		this.workspaceId = Objects.requireNonNull(workspaceId);
		this.debug = debug;
	}

	public void run() throws Exception {
		List<AppDeployment> measurementList = getAppDeploymentList();
		if (debug) {
			measurementList.forEach(System.out::println);
		}
		saveMeasurement(measurementList);
	}

	/**
	 * Read business capabilities
	 * 
	 * @return
	 * @throws ApiException
	 */
	private List<AppDeployment> getAppDeploymentList() throws ApiException {
		BusinessCapabilitiesApi bcApi = new BusinessCapabilitiesApi(apiClient);
		ServicesApi servicesApi = new ServicesApi(apiClient);
		List<AppDeployment> appDeployList = new ArrayList<>();
		// read services
		List<Service> allServices = servicesApi.getServices(false, null);
		Map<String, Service> allServicesAsMap = allServices.stream()
				.collect(Collectors.toMap(Service::getID, Function.identity()));
		// read business capabilities
		List<BusinessCapability> allBCs = bcApi.getBusinessCapabilities(true, null);
		Map<String, BusinessCapability> allBCsAsMap = allBCs.stream()
				.collect(Collectors.toMap(BusinessCapability::getID, Function.identity()));
		allBCs.forEach((bc) -> {
			AppDeployment appDeploy = new AppDeployment();
			appDeploy.setBusinessCapabilityID(bc.getID());
			appDeploy.setDisplayName(bc.getDisplayName());
			// read services of business capabilities
			List<Service> services = getServicesFromBC(bc, allBCsAsMap, allServicesAsMap);
			appDeploy.setTotalApps(services.size());
			countTags(appDeploy, services);
			appDeployList.add(appDeploy);
		});
		return appDeployList;
	}

	/**
	 * count the tags 'global' and 'local' 
	 * @param appDeploy hold the data
	 * @param services list of services (applications)
	 */
	private void countTags(AppDeployment appDeploy, List<Service> services) {
		services.forEach((s) -> {
			tagsOfService = s.getTags();
			tagsOfService.forEach((tag) -> {
				if(tag.equals("global")) {
					appDeploy.incrementGlobalTags();
				}
				if(tag.equals("local")) {
					appDeploy.incrementLocalTags();
				}
			});
		});
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

	/**
	 * save the measurement
	 * 
	 * @param measurementList
	 * @throws net.leanix.dropkit.apiclient.ApiException
	 */
	private void saveMeasurement(List<AppDeployment> measurementList) throws net.leanix.dropkit.apiclient.ApiException {
		PointsApi pointsApi = new PointsApi(metricsClient);
		Date current = new Date();
		for (AppDeployment appData : measurementList) {
			Point point = new Point();
			point.setMeasurement("dashboard-application-deployment");
			point.setWorkspaceId(workspaceId);
			point.setTime(current);

			Field field = new Field();
			field.setK("local");
			field.setV(Double.valueOf(appData.getGlobalApps()));

			Field field2 = new Field();
			field2.setK("global");
			field2.setV(Double.valueOf(appData.getLocalApps()));
			
			point.getFields().add(field);
			point.getFields().add(field2);

			Tag tag = new Tag();
			tag.setK("factsheetId");
			tag.setV(appData.getBusinessCapabilityID());

			point.getTags().add(tag);

			pointsApi.createPoint(point);
		}
	}

}