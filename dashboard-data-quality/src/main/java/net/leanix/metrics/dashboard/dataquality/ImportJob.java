package net.leanix.metrics.dashboard.dataquality;

import java.util.List;
import java.util.Objects;

import net.leanix.api.BusinessCapabilitiesApi;
import net.leanix.api.models.BusinessCapability;

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
		// read business capabilities
		BusinessCapabilitiesApi businessCapabilitiesApi = new BusinessCapabilitiesApi(apiClient);
		List<BusinessCapability> businessCapabilities;
		businessCapabilities = businessCapabilitiesApi.getBusinessCapabilities(true, null);
		for (BusinessCapability businessCapability : businessCapabilities) {
			if (debug) {
				System.out.println(businessCapability.toString());
				System.out.println("Applications:");
				System.out.println(businessCapability.getServiceHasBusinessCapabilities());
			}
		}
	}
}