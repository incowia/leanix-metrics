package net.leanix.metrics.excelimport;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.leanix.metrics.excelimport.ImportJob;

public class Main {
	public static final void main(String[] args) {
		// define cli's
		Options options = new Options();
		options.addOption(Option.builder("p").longOpt("path").required().hasArg()
				.desc("The path to excel sheet (required).").build());
		options.addOption(Option.builder("d").longOpt("debug").desc("Enables the debug mode.").build());
		options.addOption(Option.builder("?").longOpt("help").desc("Prints this help and returns.").build());
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {
			// read cli values
			CommandLine line = parser.parse(options, args);
			if (args.length == 0 || line.hasOption("help")) {
				printHelp(formatter, options, 0);
			}
			String path = read(line, 'p');
			boolean debug = line.hasOption('d');
<<<<<<< HEAD
			//run import
=======
			// create client
			/*net.leanix.dropkit.apiclient.ApiClient metricsClient = new net.leanix.dropkit.apiclient.ApiClientBuilder()
					.withBasePath(String.format("https://%s/services/metrics/v1", host)).withTokenProviderHost(host)
					.withApiToken(token).withDebugging(debug).build();*/
			// run import
>>>>>>> 64bc94a3a9944e10d3f7310638c7ce71ef2de9c2
			new ImportJob(path, debug).run();
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
	
	private static void printHelp(HelpFormatter formatter, Options options, int exitCode) {
		formatter.printHelp("java -jar excel-import.jar", options, true);
		System.exit(exitCode);
	}
}