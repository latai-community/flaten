package com.flating.ignore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IgnoreListParserTest {

    private Path tempIgnoreFile;

    @BeforeEach
    void setup() throws IOException {
        List<String> lines = List.of(
                "# This is a comment",
                "*.log",
                "*.tmp",
                "/test/output/",
                "/absolute/path/to/ignore.txt",
                "",  // empty line
                "*.mp4"
        );

        tempIgnoreFile = Files.createTempFile("ignoreTest", ".flatIgnore");
        Files.write(tempIgnoreFile, lines);
    }

    @Test
    void testParserLoadsCorrectRules() throws IOException {
        IgnoreListParser parser = new IgnoreListParser(Path.of("resources/sample_project"));
        IgnoreRulesImpl rules = parser.parse(tempIgnoreFile);

        // Extension match
        assertTrue(rules.shouldIgnore(Path.of("error.log")));
        assertTrue(rules.shouldIgnore(Path.of("video.mp4")));
        assertFalse(rules.shouldIgnore(Path.of("notes.md")));

        // Absolute file path match
        assertTrue(rules.shouldIgnore(Path.of("/absolute/path/to/ignore.txt")));
        assertFalse(rules.shouldIgnore(Path.of("/absolute/path/to/not_ignored.txt")));

        // Directory match
        assertTrue(rules.shouldIgnore(Path.of("/test/output/some.txt")));
        assertFalse(rules.shouldIgnore(Path.of("/test/input/some.txt")));
    }
}
