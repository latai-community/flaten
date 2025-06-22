package com.flating.ignore;

import java.nio.file.Path;
import java.util.Set;


public class IgnoreRulesImpl {

    private final Set<String> extensions;
    private final Set<String> absoluteFileRelativePaths;
    private final Set<String> directoryRelativePaths;
    private final Path baseSourceDirectory;

    // Now accepts Set<String> for file and directory patterns
    public IgnoreRulesImpl(Set<String> extensions, Set<String> absoluteFilePatterns, Set<String> directoryPatterns, Path baseSourceDirectory) {
        this.extensions = extensions;
        this.baseSourceDirectory = baseSourceDirectory;

        this.absoluteFileRelativePaths = absoluteFilePatterns;
        this.directoryRelativePaths = directoryPatterns;
    }

    public boolean shouldIgnore(Path filePath) {
        Path relativePath = baseSourceDirectory.relativize(filePath);
        String normalizedRelativePathString = relativePath.toString().replace("\\", "/");

        if (absoluteFileRelativePaths.contains(normalizedRelativePathString)) {
            return true;
        }

        String fileName = normalizedRelativePathString.substring(normalizedRelativePathString.lastIndexOf('/') + 1);
        for (String ext : extensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }

        for (String dirPattern : directoryRelativePaths) {
            if (normalizedRelativePathString.startsWith(dirPattern)) {
                return true;
            }
        }

        return false;
    }
}