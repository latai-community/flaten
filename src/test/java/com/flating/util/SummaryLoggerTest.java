package com.flating.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SummaryLoggerTest {

    @Test
    public void testWriteSummary() {
        Path summaryPath = Paths.get("test-summary.log");
        SummaryLogger logger = new SummaryLogger(summaryPath);

        logger.addCopied(Paths.get("file1.txt"), 100);
        logger.addCopied(Paths.get("file2.txt"), 200);
        logger.addFailed(Paths.get("file3.txt"), "Permission denied");
        logger.incrementIgnored();

        assertDoesNotThrow(logger::writeSummary);
    }
}