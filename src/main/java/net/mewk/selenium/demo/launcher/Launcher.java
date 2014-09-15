package net.mewk.selenium.demo.launcher;

import net.mewk.selenium.demo.tests.twitter.RepositoryTestWin7;
import net.mewk.selenium.demo.tests.twitter.RepositoryTestWin8;
import org.apache.commons.cli.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Mewes Kochheim <mewes@kochheim.de>
 */
public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private static String hubUrl;

    public static String getHubUrl() {
        return hubUrl;
    }

    private static boolean parallel;

    public static boolean isParallel() {
        return parallel;
    }

    public static void main(String[] args) {
        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the options
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "Print this message"));
        options.addOption(new Option("p", "parallel", false, "Runs the tests in parallel"));

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // --help
            if (line.hasOption("help")) {
                printHelp(options);
                return;
            }

            // --parallel
            boolean parallel = false;
            if (line.hasOption("parallel")) {
                parallel = true;
            }

            // [hub]
            if (line.getArgs().length == 1) {
                String hubUrl = line.getArgs()[0];
                UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS);
                if (urlValidator.isValid(hubUrl)) {
                    System.out.println("Runinng tests on " + hubUrl);
                    runTests(hubUrl, parallel);
                } else {
                    throw new ParseException("Invalid argument: hub ('" + hubUrl + "' is not a valid URL)");
                }
            } else {
                throw new ParseException("Missing required argument: hub");
            }
        } catch (ParseException exp) {
            System.out.println("Error: " + exp.getMessage());
            System.out.println();

            printHelp(options);
        }
    }

    public static void printHelp(Options options) {
        // get jar filename (will return "classes" if not packaged)
        String jarName = new java.io.File(Launcher.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();

        // generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar " + jarName + " [options] [hub]", options);
    }

    public static void runTests(String hubUrl, boolean parallel) {
        Launcher.hubUrl = hubUrl;
        Launcher.parallel = parallel;

        Result result = JUnitCore.runClasses(
                ParallelComputer.classes(),
                new Class[]{RepositoryTestWin7.class,RepositoryTestWin8.class }
        );

        try {
            // Log header
            StringBuilder logHeader = new StringBuilder();
            logHeader.append(result.getRunCount());
            logHeader.append(" tests run in ");
            logHeader.append(result.getRunTime() / 1000);
            logHeader.append(" seconds");
            LOG.info(logHeader.toString());

            // Log body
            List<Failure> failures = result.getFailures();
            for (Failure failure : failures) {
                LOG.error(failure.getMessage(), failure.getException());
            }

            // Log footer
            StringBuilder logFooter = new StringBuilder();
            logFooter.append(result.getFailureCount());
            logFooter.append(" of ");
            logFooter.append(result.getRunCount());
            logFooter.append(" tests failed");
            LOG.info(logFooter.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
