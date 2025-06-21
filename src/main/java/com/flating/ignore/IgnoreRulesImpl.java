package com.flating.ignore;

import java.nio.file.Path;
import java.util.Set;

/**
 * Holds ignore rules and checks whether a path should be excluded.
 */
public class IgnoreRulesImpl {

    private final Set<String> extensions;
    private final Set<Path> absoluteFiles;
    private final Set<Path> directories;

    public IgnoreRulesImpl(Set<String> extensions, Set<Path> absoluteFiles, Set<Path> directories) {
        this.extensions = extensions;
        this.absoluteFiles = absoluteFiles;
        this.directories = directories;
    }

    /**
     * Determines whether a given file should be ignored.
     */
    public boolean shouldIgnore(Path filePath) {
        Path normalizedPath = filePath.normalize();

        // Absolute file match
        if (absoluteFiles.contains(normalizedPath)) return true;

        // Extension match (e.g., *.log)
        String fileName = normalizedPath.getFileName().toString();
        for (String ext : extensions) {
            if (fileName.endsWith(ext)) return true;
        }

        // Directory path match
        for (Path dir : directories) {
            if (normalizedPath.startsWith(dir)) return true;
        }

        return false;
    }
}
