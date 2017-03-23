package net.leanix.metrics.dashboard.dataquality;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Main {

	public static final void main(String[] args) {
		// define cli's
		Options options = new Options()
				.addRequiredOption("h", "host", true, "The host, e.g. app.leanix.net (required).")
				.addRequiredOption("w", "workspace", true, "The leanIX workspace (required).")
				.addRequiredOption("wid", "workspaceid", true, "The leanIX workspace id for metrics (required).")
				.addRequiredOption("t", "token", true, "The API token  (required).")
				.addOption("d", "debug", false, "Enables the debug mode.")
				.addOption("?", "help", false, "Prints this help and returns.");
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {
			// read cli values
			CommandLine line = parser.parse(options, args);
			if (args.length == 0 || line.hasOption("help")) {
				printHelp(formatter, options, 0);
			}
			String host = read(line, 'h');
			String workspace = read(line, 'w');
			String workspaceId = read(line, "wid");
			String token = read(line, 't');
			boolean debug = line.hasOption('d');
			// create clients
			net.leanix.api.common.ApiClient apiClient = new net.leanix.api.common.ApiClientBuilder()
					.withBasePath(String.format("https://%s/%s/api/v1", host, workspace)).withTokenProviderHost(host)
					.withApiToken(token).withDebugging(debug).build();
			net.leanix.dropkit.apiclient.ApiClient metricsClient = new net.leanix.dropkit.apiclient.ApiClientBuilder()
					.withBasePath(String.format("https://%s/services/metrics/v1", host)).withTokenProviderHost(host)
					.withApiToken(token).withDebugging(debug).build();
			// run import
			new ImportJob(apiClient, metricsClient, workspaceId, debug).run();
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage() + "\n");
			printHelp(formatter, options, -1);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.exit(-2);
		}
	}

	private static String read(CommandLine line, char opt) {
		return line.hasOption(opt) ? line.getOptionValue(opt).trim() : null;
	}

	private static String read(CommandLine line, String opt) {
		return line.hasOption(opt) ? line.getOptionValue(opt).trim() : null;
	}

	private static void printHelp(HelpFormatter formatter, Options options, int exitCode) {
		formatter.printHelp("java -jar dashboard-data-quality.jar", options, true);
		System.exit(exitCode);
	}
}
