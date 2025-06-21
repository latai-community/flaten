package com.flating.ignore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses an ignore list file (.flatIgnore) and builds IgnoreRulesImpl.
 */
public class IgnoreListParser {

    public IgnoreRulesImpl parse(Path ignoreFile) throws IOException {
        Set<String> extensions = new HashSet<>();
        Set<Path> absoluteFiles = new HashSet<>();
        Set<Path> directoryPaths = new HashSet<>();

        if (!Files.exists(ignoreFile)) {
            System.err.println("⚠️ Ignore file not found: " + ignoreFile + " (ignoring nothing)");
            return new IgnoreRulesImpl(extensions, absoluteFiles, directoryPaths);
        }

        Files.lines(ignoreFile)
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    if (line.startsWith("*.")) {
                        // Extension rule like *.log
                        extensions.add(line.substring(1)); // store ".log"
                    } else if (line.endsWith("/")) {
                        directoryPaths.add(Path.of(line).normalize());
                    } else {
                        absoluteFiles.add(Path.of(line).normalize());
                    }
                });

        return new IgnoreRulesImpl(extensions, absoluteFiles, directoryPaths);
    }
}
