package com.flating.ignore;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class IgnoreListParserTest {

    @Test
    public void testParserIgnoresCorrectFiles() throws Exception {
        Path baseSource = Paths.get("src/test/resources/sample_project").toAbsolutePath().normalize();
        Path ignoreFile = baseSource.resolve("ignoreBinaryList.flatIgnore");

        IgnoreListParser parser = new IgnoreListParser(baseSource);
        IgnoreRulesImpl rules = parser.parse(ignoreFile);

        Path ignoredFile = baseSource.resolve("target/thisFileIsAvideo.mp4");
        Path goodFile = baseSource.resolve("src/good1.txt");

        assertTrue(rules.shouldIgnore(ignoredFile), "Expected .mp4 file to be ignored");
        assertFalse(rules.shouldIgnore(goodFile), "Expected .txt file to be processed");
    }
}