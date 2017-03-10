package net.leanix.metrics.dashboard.dataquality;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import net.leanix.api.BusinessCapabilitiesApi;
import net.leanix.api.common.ApiClient;
import net.leanix.api.common.ApiClientBuilder;
import net.leanix.api.common.ApiException;
import net.leanix.api.models.BusinessCapability;

/**
 * import of leanIX to metrics
 */
public final class ImportJob implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(ImportJob.class.getName());

	private final String host;
	private final String workspace;
	private final String token;
	private final boolean debug;

	private ApiClient apiClient;

	public ImportJob(String host, String workspace, String token, boolean debug) throws NullPointerException {
		this.host = Objects.requireNonNull(host);
		this.workspace = Objects.requireNonNull(workspace);
		this.token = Objects.requireNonNull(token);
		this.debug = debug;
	}

	@Override
	public void run() {
		// create clients for access to leanIX and metrics
		ApiClientBuilder apiClientBuilder = new ApiClientBuilder();
		apiClient = apiClientBuilder.withBasePath(String.format("https://%s/%s/api/v1", host, workspace))
				.withApiToken(token).withTokenProviderHost(host).withDebugging(debug).build();
		// read business capabilities
		BusinessCapabilitiesApi businessCapabilitiesApi = new BusinessCapabilitiesApi(apiClient);
		List<BusinessCapability> businessCapabilities;
		try {
			businessCapabilities = businessCapabilitiesApi.getBusinessCapabilities(true, null);
			for (BusinessCapability businessCapability : businessCapabilities) {
				if (debug) {
					LOGGER.info(businessCapability.toString());
				}
			}
		} catch (ApiException e) {
			LOGGER.warning("Unable to read business capabilities: " + e.getMessage());
		}
	}
}