package com.flating.util;

import com.sun.tools.jconsole.JConsoleContext;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class SummaryLogger {

    private final Path summaryFile;
    private final Map<Path, Long> copiedFiles = new LinkedHashMap<>();
    private final Map<Path, String> failedFiles = new LinkedHashMap<>();
    private int ignoredCount = 0;

    public SummaryLogger(Path summaryFile) {
        this.summaryFile = summaryFile;
    }

    public void addCopied(Path file, long sizeInBytes) {
        copiedFiles.put(file, sizeInBytes);
    }

    public void addFailed(Path file, String reason) {
        failedFiles.put(file, reason);
    }

    public void incrementIgnored() {
        ignoredCount++;
    }

    public void writeSummary() {
        try (BufferedWriter writer = Files.newBufferedWriter(summaryFile, StandardCharsets.UTF_8)) {
            writer.write("Files copied in flat: " + copiedFiles.size());
            writer.newLine();
            for (Map.Entry<Path, Long> entry : copiedFiles.entrySet()) {
                writer.write("    " + entry.getKey() + "    " + entry.getValue() + " bytes");
                writer.newLine();
            }

            writer.newLine();
            writer.write("Files ignored:  " + ignoredCount);
            writer.newLine();

            writer.write("Files unable to be processed : " + failedFiles.size());
            writer.newLine();
            for (Map.Entry<Path, String> entry : failedFiles.entrySet()) {
                writer.write("    " + entry.getKey() + "    (" + entry.getValue() + ")");
                writer.newLine();
            }

            long totalBytes = copiedFiles.values().stream().mapToLong(Long::longValue).sum();
            writer.newLine();
            writer.write("Flat filename = " + summaryFile.getFileName());
            writer.newLine();
            writer.write("Flat total size = " + String.format("%.1f Kb", totalBytes / 1024.0));
            writer.newLine();

        } catch (IOException e) {
            System.err.println("❌ Failed to write summary log: " + e.getMessage());
        }
    }
}