package com.flating.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryLoggerTest {

    @Test
    void testSummaryWriting() throws IOException {
        Path tempSummaryLog = Files.createTempFile("summary", ".log");

        SummaryLogger logger = new SummaryLogger(tempSummaryLog);

        // Add some fake stats
        logger.addProcessed(Path.of("src/file1.txt"), 123);
        logger.addProcessed(Path.of("src/file2.sh"), 1_048_576); // 1MB
        logger.addIgnored(Path.of("build/tmp/file.tmp"));
        logger.addFailed(Path.of("src/file3.txt"), "permission denied");

        logger.writeSummary();

        List<String> lines = Files.readAllLines(tempSummaryLog);
        String content = String.join("\n", lines);

        assertTrue(content.contains("Files copied in flat: 2"));
        assertTrue(content.contains("src/file1.txt"));
        assertTrue(content.contains("1.0 Mb"));
        assertTrue(content.contains("Files ignored:  1"));
        assertTrue(content.contains("Files unable to be processed : 1"));
        assertTrue(content.contains("permission denied"));
        assertTrue(content.contains("Flat total size ="));
    }
}
