package com.flating.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads configuration from flating.properties.
 * Command-line args can override specific parameters.
 */
public class ConfigLoader {

    public static Properties load(String[] args) throws IOException {
        if (args.length == 0 || !args[0].startsWith("-option:")) {
            throw new IllegalArgumentException("Missing required -option:[Args|Prop]");
        }

        String mode = args[0].substring("-option:".length());
        Properties props = new Properties();

        switch (mode) {
            case "Args" -> parseInlineArgs(args, props);
            case "Prop" -> loadFromFile(args, props);
            default -> throw new IllegalArgumentException("Unknown option mode: " + mode);
        }

        applyDefaults(props);
        return props;
    }

    private static void parseInlineArgs(String[] args, Properties props) {
        for (String arg : args) {
            if (arg.contains("=")) {
                String[] keyValue = arg.split("=", 2);
                String key = keyValue[0].replaceFirst("^-+", "");
                props.setProperty(key, keyValue[1]);
            }
        }
    }

    private static void loadFromFile(String[] args, Properties props) throws IOException {
        if (args.length < 2 || !args[1].startsWith("-fileProp=")) {
            throw new IllegalArgumentException("Missing or invalid -fileProp argument.");
        }

        Path propPath = Paths.get(args[1].substring("-fileProp=".length()));
        if (!Files.exists(propPath)) {
            throw new IOException("Properties file not found: " + propPath);
        }

        try (var reader = Files.newBufferedReader(propPath)) {
            props.load(reader);
        }
    }

    private static void applyDefaults(Properties props) {
        props.putIfAbsent("encoding", "UTF-8");
        props.putIfAbsent("delimiter.line", "--🖤ñÑñ----🌞ñÑñ----♠️ñÑñ----❌--");
        props.putIfAbsent("delimiter.context", "--❌----ñÑñ️♠----ñÑñ🌞----ñÑñ🖤--");
        props.putIfAbsent("output.file", "output.flat");
    }
}