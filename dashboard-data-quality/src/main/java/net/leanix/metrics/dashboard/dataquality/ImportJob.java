package net.leanix.metrics.dashboard.dataquality;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.leanix.api.BusinessCapabilitiesApi;
import net.leanix.api.ServicesApi;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.BusinessCapability;
import net.leanix.api.models.Service;
import net.leanix.api.models.ServiceHasBusinessCapability;

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
		getBusinessCapabilities();
	}

	/**
	 * Read business capabilities
	 * @throws ApiException
	 */
	private void getBusinessCapabilities() throws ApiException {
		BusinessCapabilitiesApi businessCapabilitiesApi = new BusinessCapabilitiesApi(apiClient);
		ServicesApi servicesApi = new ServicesApi(apiClient);
		List<BusinessCapability> businessCapabilities;
		//read business capabilities
		businessCapabilities = businessCapabilitiesApi.getBusinessCapabilities(true, null);
		for (BusinessCapability businessCapability : businessCapabilities) {
			if (debug) {
				System.out.println(businessCapability.toString());
				System.out.println("Applications:");
				System.out.println(businessCapability.getServiceHasBusinessCapabilities());
			}
			//read services of business capabilities
			List<ServiceHasBusinessCapability> serviceHasBusinessCapabilities = businessCapability.getServiceHasBusinessCapabilities();
			for(ServiceHasBusinessCapability serviceHasBusinessCapability: serviceHasBusinessCapabilities) {
				String id = serviceHasBusinessCapability.getServiceID();

				Service service = servicesApi.getService(id, null);
				String completion = service.getCompletion();
				if (debug) {
					System.out.println("completion: " + completion);
				}
			}
		}
	}
}