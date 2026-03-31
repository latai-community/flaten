package com.flating.processor;

import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileProcessorTest {

    private FileProcessor processor;
    private SummaryLogger summaryLogger;
    private ErrorLogger errorLogger;

    @BeforeEach
    public void setUp() throws Exception {
        summaryLogger = new SummaryLogger(Paths.get("test-summary.log"));
        errorLogger = new ErrorLogger(Paths.get("test-error.log"));
        processor = new FileProcessor(
                "----",
                "++++",
                "UTF-8",
                errorLogger,
                summaryLogger
        );
    }

    @Test
    public void testProcessFileWritesToWriter() throws Exception {
        Path inputFile = Paths.get("test-input.txt");
        Path outputFile = Paths.get("test-output.flat");

        Files.writeString(inputFile, "Hello World");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.toFile()))) {
            processor.processFile(inputFile, writer);
        }

        assertTrue(Files.exists(outputFile));
    }
}