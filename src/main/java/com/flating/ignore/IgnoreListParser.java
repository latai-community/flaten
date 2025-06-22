package com.flating.ignore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class IgnoreListParser {

    private final Path baseSourceDirectory;

    public IgnoreListParser(Path baseSourceDirectory) {
        this.baseSourceDirectory = baseSourceDirectory;
    }

    public IgnoreRulesImpl parse(Path ignoreFile) throws IOException {
        Set<String> extensions = new HashSet<>();
        Set<String> absoluteFilePatterns = new HashSet<>();
        Set<String> directoryPatterns = new HashSet<>();

        if (!Files.exists(ignoreFile)) {
            System.err.println("⚠️ Warning: Ignore file not found: " + ignoreFile + " (ignoring nothing)");
            return new IgnoreRulesImpl(extensions, absoluteFilePatterns, directoryPatterns, baseSourceDirectory);
        }

        String sourceDirName = baseSourceDirectory.getFileName().toString();

        Files.lines(ignoreFile)
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    String processedLine = line.replace("\\", "/");

                    if (processedLine.startsWith("/")) processedLine = processedLine.substring(1);

                    if (processedLine.startsWith(sourceDirName + "/")) {
                        processedLine = processedLine.substring(sourceDirName.length() + 1);
                    } else if (processedLine.equals(sourceDirName)) {
                        processedLine = "";
                    }

                    if (processedLine.startsWith("*.")) {
                        extensions.add(processedLine.substring(1));
                    } else if (processedLine.endsWith("/")) {
                        directoryPatterns.add(processedLine);
                    } else {
                        absoluteFilePatterns.add(processedLine);
                    }
                });
        return new IgnoreRulesImpl(extensions, absoluteFilePatterns, directoryPatterns, baseSourceDirectory);
    }
}