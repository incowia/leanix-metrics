package net.leanix.metrics.dashboard.dataquality;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public final class Main {

	public static final void main(String[] args) {
		// define cli's
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("h", "host", true, "The host, e.g. app.leanix.net (required).");
		options.addOption("w", "workspace", true, "The leanIX workspace (required).");
		/*
		 * TODO workspace id could be retrieved in different ways - query
		 * leanix-mtm-sdk-java -> ApiTokensApi.getApiTokens(...) - query
		 * undocumented rest api '/config' call - as cli option
		 */
		options.addOption("wid", "workspaceid", true, "The leanIX workspace id for metrics (required).");
		options.addOption("t", "token", true, "The API token  (required).");
		options.addOption("d", "debug", false, "Sets the debug mode (optional flag).");
		options.addOption("?", "help", false, "Prints this help and returns (optional flag).");
		HelpFormatter formatter = new HelpFormatter();
		// read cli values
		try {
			CommandLine line = parser.parse(options, args);
			if (args.length == 0 || line.hasOption("help")) {
				printHelp(formatter, options);
				System.exit(0);
			}
			String host = read(line, 'h');
			String workspace = read(line, 'w');
			String workspaceId = read(line, "wid");
			String token = read(line, 't');
			boolean debug = line.hasOption('d');
			// validate cli values
			if (host == null) {
				System.out.println("'host' is missing.\n");
				printHelp(formatter, options);
				System.exit(-1);
			}
			if (workspace == null) {
				System.out.println("'workspace' is missing.\n");
				printHelp(formatter, options);
				System.exit(-1);
			}
			if (workspaceId == null) {
				System.out.println("'workspaceId' is missing.\n");
				printHelp(formatter, options);
				System.exit(-1);
			}
			if (token == null) {
				System.out.println("'token' is missing.\n");
				printHelp(formatter, options);
				System.exit(-1);
			}
			// create clients
			net.leanix.api.common.ApiClient apiClient = new net.leanix.api.common.ApiClientBuilder()
					.withBasePath(String.format("https://%s/%s/api/v1", host, workspace)).withTokenProviderHost(host)
					.withApiToken(token).withDebugging(debug).build();
			net.leanix.dropkit.apiclient.ApiClient metricsClient = new net.leanix.dropkit.apiclient.ApiClientBuilder()
					.withBasePath(String.format("https://%s/services/metrics/v1", host)).withTokenProviderHost(host)
					.withApiToken(token).withDebugging(debug).build();
			// run import
			ImportJob job = new ImportJob(apiClient, metricsClient, workspaceId, debug);
			job.run();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			printHelp(formatter, options);
			System.exit(-1);
		}
	}

	private static String read(CommandLine line, char opt) {
		return line.hasOption(opt) ? line.getOptionValue(opt).trim() : null;
	}

	private static String read(CommandLine line, String opt) {
		return line.hasOption(opt) ? line.getOptionValue(opt).trim() : null;
	}

	private static void printHelp(HelpFormatter formatter, Options options) {
		formatter.printHelp("java -jar dashboard-data-quality.jar", options, true);
	}
}
