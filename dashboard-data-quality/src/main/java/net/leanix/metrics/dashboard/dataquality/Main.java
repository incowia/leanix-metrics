package net.leanix.metrics.dashboard.dataquality;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public final class Main {

	private static final String CLI_SYNTAX = "java -jar dashboard-data-quality.jar";

	public static final void main(String[] args) {
		// define cli's
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("h", "host", true, "The host, e.g. app.leanix.net (required).");
		options.addOption("w", "workspace", true, "The leanIX workspace (required).");
		options.addOption("t", "token", true, "The API token  (required).");
		options.addOption("d", "debug", false, "Sets the debug mode (optional flag).");
		HelpFormatter formatter = new HelpFormatter();
		String host = null;
		String workspace = null;
		String token = null;
		boolean debug = false;
		// read cli values
		try {
			CommandLine line = parser.parse(options, args);
			host = read(line, 'h');
			workspace = read(line, 'w');
			token = read(line, 't');
			debug = line.hasOption('d');
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			formatter.printHelp(CLI_SYNTAX, options, true);
			System.exit(-1);
		}
		// validate cli values
		if (host == null) {
			System.out.println("'host' is missing.\n");
			formatter.printHelp(CLI_SYNTAX, options, true);
			System.exit(-1);
		}
		if (workspace == null) {
			System.out.println("'workspace' is missing.\n");
			formatter.printHelp(CLI_SYNTAX, options, true);
			System.exit(-1);
		}
		if (token == null) {
			System.out.println("'token' is missing.\n");
			formatter.printHelp(CLI_SYNTAX, options, true);
			System.exit(-1);
		}
		// run import
		ImportJob job = new ImportJob(host, workspace, token, debug);
		job.run();
	}

	private static String read(CommandLine line, char opt) {
		return line.hasOption(opt) ? line.getOptionValue(opt) : null;
	}
}
