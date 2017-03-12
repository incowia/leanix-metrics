package net.leanix.metrics.dashboard.dataquality;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ImportJobTest {

	@Mock
	private net.leanix.api.common.ApiClient apiClient;

	@Mock
	private net.leanix.dropkit.apiclient.ApiClient metricsClient;

	private String workspaceId = "<id>";
	private boolean debug = false;

	@Before
	public void setUp() throws Exception {
		// TODO create leanix api response objects to fill mocks
	}

	@Test
	public void run() {
		// TODO fill mocks with additional test specific objects
		ImportJob job = new ImportJob(apiClient, metricsClient, workspaceId, debug);
		// TODO test run method
		assertTrue(true);
	}
}
