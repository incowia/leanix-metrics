package net.leanix.metrics.excelimport;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	public static final void main(String[] args) {
		// define cli's
		Options options = new Options()
				.addRequiredOption("h", "host", true, "The host, e.g. app.leanix.net (required).")
				.addRequiredOption("wid", "workspaceid", true, "The leanIX workspace id for metrics (required).")
				.addRequiredOption("t", "token", true, "The API token  (required).")
				.addRequiredOption("p", "path", true, "The path to excel sheet (required).")
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
			String workspaceId = read(line, "wid");
			String token = read(line, 't');
			String path = read(line, 'p');
			boolean debug = line.hasOption('d');
			// create clients
			net.leanix.dropkit.apiclient.ApiClient metricsClient = new net.leanix.dropkit.apiclient.ApiClientBuilder()
					.withBasePath(String.format("https://%s/services/metrics/v1", host)).withTokenProviderHost(host)
					.withApiToken(token).withDebugging(debug).build();
			new ImportJob(metricsClient, workspaceId, path, debug).run();
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
		formatter.printHelp("java -jar excel-import.jar", options, true);
		System.exit(exitCode);
	}
}