package com.flating.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    public void testCLIArgsOverrideProperties() throws IOException {
        String[] args = {
            "-option:Args",
            "-source.dir=sample_project",
            "-generation.file=CLI_flat.flat",
            "-summary.file=cli_summary.log",
            "-error.file=cli_error.log",
            "-ignoreList.file=ignoreBinaryList.flatIgnore"
        };
        Properties props = ConfigLoader.load(args);

        assertEquals("sample_project", props.getProperty("source.dir"));
        assertEquals("CLI_flat.flat", props.getProperty("generation.file"));
        assertEquals("cli_summary.log", props.getProperty("summary.file"));
    }

    @Test
    public void testFallbackToDefaultsIfFileMissing() {
        String[] args = { "-option:Prop", "-fileProp=nonexistent.properties" };

        assertThrows(IOException.class, () -> {
            ConfigLoader.load(args);
        });
    }


    @Test
    public void testLoadFromPropertiesFile() throws IOException {
        String[] args = {
            "-option:Prop",
            "-fileProp=src/test/resources/test-flaten.properties"
        };

        Properties props = ConfigLoader.load(args);

        assertEquals("sample_project", props.getProperty("source.dir"));
        assertEquals("flat_test.flat", props.getProperty("generation.file"));
        //assertEquals("FLAT_root.flat", props.getProperty("generation.file"));
    }
}