package com.flating.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads configuration from flating.properties.
 * Command-line args can override specific parameters.
 */
public class ConfigLoader {

    private static final String DEFAULT_PROPERTIES_FILE = "flating.properties";

    public Properties load(String[] args) throws IOException {
        Properties props = new Properties();

        // 1. Load defaults from flating.properties
        try (FileInputStream fis = new FileInputStream(DEFAULT_PROPERTIES_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("⚠️ Warning: Could not load flating.properties. Using CLI args only.");
        }

        // 2. Override certain properties with CLI args if provided
        if (args.length >= 1) props.setProperty("source.dir", args[0]);
        if (args.length >= 2) props.setProperty("output.file", args[1]);
        if (args.length >= 3) props.setProperty("ignoreList.file", args[2]);
        if (args.length >= 4) props.setProperty("error.file", args[3]);
        if (args.length >= 5) props.setProperty("summary.file", args[4]);

        // 3. Sanity defaults
        props.putIfAbsent("encoding", "UTF-8");
        props.putIfAbsent("delimiter.line", "--🖤ñÑñ----🌞ñÑñ----♠️ñÑñ----❌--");
        props.putIfAbsent("delimiter.context", "--❌----ñÑñ️♠----ñÑñ🌞----ñÑñ🖤--");

        return props;
    }
}
