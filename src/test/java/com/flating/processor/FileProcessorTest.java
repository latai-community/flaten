package com.flating.processor;

import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessorTest {

    private Path tempFile;
    private Path largeFile;

    @AfterEach
    void cleanup() throws Exception {
        if (tempFile != null) Files.deleteIfExists(tempFile);
        if (largeFile != null) Files.deleteIfExists(largeFile);
    }

    @Test
    void testNormalFileProcessing() throws Exception {
        // Prepare small file
        tempFile = Files.createTempFile("small", ".txt");
        String fileContent = "Hello World\nThis is a test.";
        Files.writeString(tempFile, fileContent, StandardCharsets.UTF_8);

        StringWriter output = new StringWriter();

        TestErrorLogger errorLogger = new TestErrorLogger();
        TestSummaryLogger summaryLogger = new TestSummaryLogger();

        FileProcessor processor = new FileProcessor(
                "--LINE--", "--CTX--", "UTF-8", errorLogger, summaryLogger
        );

        processor.processFile(tempFile, output);

        String result = output.toString();
        assertTrue(result.contains("--LINE--"));
        assertTrue(result.contains("--CTX--"));
        assertTrue(result.contains("Hello World"));
        assertTrue(result.contains(tempFile.toString()));

        assertFalse(errorLogger.truncationLogged);
        assertTrue(summaryLogger.processedSize > 0);
    }

    @Test
    void testLargeFileTruncation() throws Exception {
        // Create file > 1MB
        largeFile = Files.createTempFile("large", ".txt");
        StringBuilder bigContent = new StringBuilder();
        for (int i = 0; i < 1100000; i++) {
            bigContent.append("A");
        }
        Files.writeString(largeFile, bigContent.toString(), StandardCharsets.UTF_8);

        StringWriter output = new StringWriter();
        TestErrorLogger errorLogger = new TestErrorLogger();
        TestSummaryLogger summaryLogger = new TestSummaryLogger();

        FileProcessor processor = new FileProcessor(
                "--LINE--", "--CTX--", "UTF-8", errorLogger, summaryLogger
        );

        processor.processFile(largeFile, output);

        assertTrue(errorLogger.truncationLogged, "Expected truncation log for large file");
        assertTrue(summaryLogger.processedSize <= 1_048_576);
    }

    // Stub ErrorLogger
    static class TestErrorLogger extends ErrorLogger {
        boolean truncationLogged = false;

        public TestErrorLogger() {
            super(Path.of("logErrorFlat.log")); // won't be used
        }

        @Override
        public void logTruncation(Path file, long original, long truncated) {
            truncationLogged = true;
        }

        @Override
        public void log(Path file, Exception e) {
            fail("Unexpected error logged: " + e.getMessage());
        }
    }

    // Stub SummaryLogger
    static class TestSummaryLogger extends SummaryLogger {
        long processedSize = 0;

        public TestSummaryLogger() {
            super(Path.of("summary.log")); // won't be used
        }

        @Override
        public void addProcessed(Path file, long sizeBytes) {
            processedSize = sizeBytes;
        }

        @Override
        public void addFailed(Path file, String reason) {
            fail("File processing should not fail: " + file);
        }
    }
}
