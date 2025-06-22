package com.flating.service;

import com.flating.ignore.IgnoreRulesImpl;
import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlattenerServiceTest {

    private Path tempDir;
    private Path outputFlat;

    @BeforeEach
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("flatTest");
        Files.writeString(tempDir.resolve("file1.txt"), "hello from file1");
        Files.writeString(tempDir.resolve("file2.sh"), "#!/bin/bash\necho test");

        outputFlat = Files.createTempFile("out", ".flat");
    }

    @Test
    void testFlattenerProcessesExpectedFiles() throws IOException {
        // No ignore rules
        IgnoreRulesImpl ignore = new IgnoreRulesImpl(Set.of(), Set.of(), Set.of(), tempDir);

        MockErrorLogger errLogger = new MockErrorLogger();
        MockSummaryLogger summaryLogger = new MockSummaryLogger();

        FlattenerService service = new FlattenerService(
                ignore,
                errLogger,
                summaryLogger,
                "--DELIM--",
                "--CTX--",
                "UTF-8",
                outputFlat
        );

        service.flattenDirectory(tempDir);

        // Verify output file is not empty
        String content = Files.readString(outputFlat, StandardCharsets.UTF_8);
        assertTrue(content.contains("file1.txt"));
        assertTrue(content.contains("file2.sh"));
        assertTrue(content.contains("--DELIM--"));
        assertTrue(content.contains("--CTX--"));

        // Ensure summary has 2 files
        assertEquals(2, summaryLogger.processedCount);
    }

    // === Mock Classes ===

    static class MockErrorLogger extends ErrorLogger {
        public MockErrorLogger() {
            super(Path.of("mockError.log"));
        }

        @Override
        public void log(Path file, Exception e) {
            fail("Should not log errors during clean test");
        }

        @Override
        public void logTruncation(Path file, long orig, long trunc) {
            // allowed silently
        }
    }

    static class MockSummaryLogger extends SummaryLogger {
        int processedCount = 0;

        public MockSummaryLogger() {
            super(Path.of("mockSummary.log"));
        }

        @Override
        public void addProcessed(Path file, long sizeBytes) {
            processedCount++;
        }

        @Override
        public void addFailed(Path file, String reason) {
            fail("Should not fail during clean test");
        }
    }
}
