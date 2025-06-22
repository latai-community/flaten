package com.flating;

import com.flating.config.ConfigLoader;
import com.flating.ignore.IgnoreListParser;
import com.flating.ignore.IgnoreRulesImpl;
import com.flating.service.FlattenerService;
import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class MainApp {

    public static void main(String[] args) {
        try {
            System.out.println(">> Starting Flating Application...");

            // 1. Load configuration (from args or properties file)
            ConfigLoader configLoader = new ConfigLoader();
            Properties config = configLoader.load(args);

            Path sourceDir = Path.of(args.length > 0 ? args[0] : config.getProperty("source.dir"));
            Path outputFlatFile = Path.of(args.length > 1 ? args[1] : config.getProperty("output.file", "FLAT_root.flat"));
            Path ignoreFile = Path.of(config.getProperty("ignoreList.file", "ignoreBinaryList.flatIgnore"));
            Path errorLogFile = Path.of(config.getProperty("error.file", "logErrorFlat.log"));
            Path summaryLogFile = Path.of(config.getProperty("summary.file", "summary.log"));

            String lineDelimiter = config.getProperty("delimiter.line");
            String contextDelimiter = config.getProperty("delimiter.context");
            String encoding = config.getProperty("encoding", "UTF-8");

            // 2. Prepare utilities
            ErrorLogger errorLogger = new ErrorLogger(errorLogFile);
            SummaryLogger summaryLogger = new SummaryLogger(summaryLogFile);

            // 3. Parse ignore rules
            IgnoreListParser ignoreListParser = new IgnoreListParser(sourceDir);
            IgnoreRulesImpl ignoreRules = ignoreListParser.parse(ignoreFile);

            // 4. Run the flattener
            FlattenerService flattener = new FlattenerService(
                    ignoreRules,
                    errorLogger,
                    summaryLogger,
                    lineDelimiter,
                    contextDelimiter,
                    encoding,
                    outputFlatFile
            );

            if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
                System.err.println("❌ Source directory does not exist or is not a directory: " + sourceDir);
                System.exit(1);
            }

            flattener.flattenDirectory(sourceDir);

            // 5. Finalize logs
            summaryLogger.writeSummary();

            System.out.println("✅ Flating complete.");
        } catch (IOException e) {
            System.err.println("❌ Fatal error during initialization: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(99);
        }
    }
}