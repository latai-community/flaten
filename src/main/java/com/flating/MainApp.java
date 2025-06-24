package com.flating;

import com.flating.config.ConfigLoader;
import com.flating.ignore.IgnoreListParser;
import com.flating.ignore.IgnoreRulesImpl;
import com.flating.service.FlattenerService;
import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MainApp {

    public static void main(String[] args) {
        System.out.println(">> Starting Flating Application...");

        try {
            Properties props = ConfigLoader.load(args);
            runFlattener(props);
            System.out.println("✅ Flating complete!");
        } catch (Exception e) {
            System.err.println("❌ " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runFlattener(Properties props) throws Exception {
        Path baseSourceDir = Paths.get(props.getProperty("source.dir")).toAbsolutePath();
        Path outputFile = Paths.get(props.getProperty("summary.file")).toAbsolutePath();
        String encoding = props.getProperty("encoding");
        String delimiterLine = props.getProperty("delimiter.line");
        String delimiterContext = props.getProperty("delimiter.context");
        Path ignoreFilePath = Paths.get(props.getProperty("ignoreList.file")).toAbsolutePath();

        IgnoreListParser parser = new IgnoreListParser(baseSourceDir);
        IgnoreRulesImpl ignoreRules = parser.parse(ignoreFilePath);

        ErrorLogger errorLogger = new ErrorLogger(Paths.get("error.log"));
        SummaryLogger summaryLogger = new SummaryLogger(Paths.get("summary.log"));

        FlattenerService flattenerService = new FlattenerService(
                ignoreRules,
                errorLogger,
                summaryLogger,
                delimiterLine,
                delimiterContext,
                encoding,
                outputFile
        );

        flattenerService.flattenDirectory(baseSourceDir);
        summaryLogger.writeSummary();
    }
}
