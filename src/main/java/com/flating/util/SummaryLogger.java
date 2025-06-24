package com.flating.util;

import com.sun.tools.jconsole.JConsoleContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Responsible for collecting summary statistics and writing summary.log at the end.
 */
public class SummaryLogger {

    private final Path summaryLogFile;

    private final Map<Path, Long> processedFiles = new LinkedHashMap<>();
    private final Set<Path> ignoredFiles = new HashSet<>();
    private final Map<Path, String> failedFiles = new LinkedHashMap<>();

    public SummaryLogger(Path summaryLogFile) {
        this.summaryLogFile = summaryLogFile;
    }

    public void addProcessed(Path file, long sizeBytes) {
        processedFiles.put(file, sizeBytes);
    }

    public void addIgnored(Path file) {
        ignoredFiles.add(file);
    }

    public void addFailed(Path file, String reason) {
        failedFiles.put(file, reason);
    }

    public void writeSummary() {
        StringBuilder summary = new StringBuilder();

        long totalBytes = processedFiles.values().stream().mapToLong(Long::longValue).sum();

        summary.append("Files copied in flat: ").append(processedFiles.size()).append("\n");
        processedFiles.forEach((path, size) ->
                summary.append("\t")
                        .append(path.toString())
                        .append("\t\t")
                        .append(toHumanReadable(size))
                        .append("\n"));

        summary.append("\nFiles ignored:  ").append(ignoredFiles.size()).append("\n");

        summary.append("Files unable to be processed : ").append(failedFiles.size()).append("\n");
        failedFiles.forEach((path, reason) ->
                summary.append("\t")
                        .append(path.toString())
                        .append("\t\t(")
                        .append(reason)
                        .append(")\n"));

        summary.append("\nFlat filename = ").append(summaryLogFile.getFileName()).append("\n");
        summary.append("Flat total size = ").append(toHumanReadable(totalBytes)).append("\n");

        try {
            Files.writeString(
                    summaryLogFile,
                    summary.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("❌ Failed to write summary log: " + e.getMessage());
        }
    }

    private String toHumanReadable(long sizeBytes) {
        if (sizeBytes >= 1024 * 1024) {
            return String.format("%.1f Mb", sizeBytes / (1024.0 * 1024));
        } else if (sizeBytes >= 1024) {
            return String.format("%.1f Kb", sizeBytes / 1024.0);
        } else {
            return sizeBytes + " bytes";
        }
    }
}
