package com.flating.service;

import com.flating.ignore.IgnoreRulesImpl;
import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlattenerServiceTest {

    private FlattenerService service;
    private Path flatOutput;

    @BeforeEach
    public void setUp() throws Exception {
        Path baseDir = Paths.get("sample_project");
        flatOutput = Paths.get("FLAT_root.flat");

        IgnoreRulesImpl ignoreRules = new IgnoreRulesImpl(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), baseDir);
        ErrorLogger errorLogger = new ErrorLogger(Paths.get("test-error.log"));
        SummaryLogger summaryLogger = new SummaryLogger(Paths.get("test-summary.log"));

        service = new FlattenerService(ignoreRules, errorLogger, summaryLogger,
                "----", "++++", "UTF-8", flatOutput);
    }

    @Test
    public void testFlattenDirectoryCreatesFlatFile() throws Exception {
        Path baseDir = Paths.get("sample_project");
        service.flattenDirectory(baseDir);

        assertTrue(Files.exists(flatOutput), "Expected flat output file to be created in working directory");
    }
}