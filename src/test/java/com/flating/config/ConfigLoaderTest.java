package com.flating.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    private Path tempPropsFile;

    @BeforeEach
    void setup() throws IOException {
        String props = """
            delimiter.line=--DEMO_LINE--
            delimiter.context=--DEMO_CTX--
            ignoreList.file=customIgnore.flatIgnore
            error.file=customError.log
            summary.file=customSummary.log
            encoding=UTF-16
            """;

        tempPropsFile = Path.of("flating.properties");
        Files.writeString(tempPropsFile, props);
    }

    @Test
    void testLoadFromPropertiesFile() throws IOException {
        ConfigLoader loader = new ConfigLoader();
        Properties props = loader.load(new String[]{}); // no CLI args

        assertEquals("--DEMO_LINE--", props.getProperty("delimiter.line"));
        assertEquals("UTF-16", props.getProperty("encoding"));
        assertEquals("customIgnore.flatIgnore", props.getProperty("ignoreList.file"));
    }

    @Test
    void testCLIArgsOverrideProperties() throws IOException {
        ConfigLoader loader = new ConfigLoader();
        String[] cliArgs = {
                "/some/source",
                "FLAT_output.flat",
                "myIgnoreList.ignore",
                "errorOut.log",
                "finalSummary.log"
        };

        Properties props = loader.load(cliArgs);

        assertEquals("/some/source", props.getProperty("source.dir"));
        assertEquals("FLAT_output.flat", props.getProperty("output.file"));
        assertEquals("myIgnoreList.ignore", props.getProperty("ignoreList.file"));
        assertEquals("errorOut.log", props.getProperty("error.file"));
        assertEquals("finalSummary.log", props.getProperty("summary.file"));
    }

    @Test
    void testFallbackToDefaultsIfFileMissing() throws IOException {
        // Delete the props file temporarily
        Files.deleteIfExists(tempPropsFile);

        ConfigLoader loader = new ConfigLoader();
        Properties props = loader.load(new String[]{});

        assertEquals("UTF-8", props.getProperty("encoding"));
        assertNotNull(props.getProperty("delimiter.line"));
        assertNotNull(props.getProperty("delimiter.context"));
    }
}
