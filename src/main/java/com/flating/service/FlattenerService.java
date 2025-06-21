package com.flating.service;

import com.flating.ignore.IgnoreRulesImpl;
import com.flating.processor.FileProcessor;
import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * Core class responsible for directory traversal and flattening.
 */
public class FlattenerService {

    private final IgnoreRulesImpl ignoreRules;
    private final ErrorLogger errorLogger;
    private final SummaryLogger summaryLogger;
    private final String delimiterLine;
    private final String delimiterContext;
    private final String encoding;
    private final Path outputFlatFile;

    private final FileProcessor fileProcessor;

    public FlattenerService(
            IgnoreRulesImpl ignoreRules,
            ErrorLogger errorLogger,
            SummaryLogger summaryLogger,
            String delimiterLine,
            String delimiterContext,
            String encoding,
            Path outputFlatFile
    ) {
        this.ignoreRules = ignoreRules;
        this.errorLogger = errorLogger;
        this.summaryLogger = summaryLogger;
        this.delimiterLine = delimiterLine;
        this.delimiterContext = delimiterContext;
        this.encoding = encoding;
        this.outputFlatFile = outputFlatFile;

        this.fileProcessor = new FileProcessor(
                delimiterLine,
                delimiterContext,
                encoding,
                errorLogger,
                summaryLogger
        );
    }

    public void flattenDirectory(Path sourceDir) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(outputFlatFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING),
                Charset.forName(encoding)))
        ) {
            try (Stream<Path> paths = Files.walk(sourceDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> !ignoreRules.shouldIgnore(path))
                        .forEach(path -> {
                            try {
                                fileProcessor.processFile(path, writer);
                            } catch (Exception e) {
                                errorLogger.log(path, e);
                                summaryLogger.addFailed(path, e.getMessage());
                            }
                        });
            }
        } catch (IOException e) {
            System.err.println("❌ Could not write to output file: " + outputFlatFile);
            errorLogger.log(outputFlatFile, e);
        }
    }
}
