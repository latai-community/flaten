package com.flating.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Responsible for logging all non-blocking errors and truncation warnings.
 */
public class ErrorLogger {

    private final Path errorLogFile;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ErrorLogger(Path errorLogFile) {
        this.errorLogFile = errorLogFile;

        try {
            if (!Files.exists(errorLogFile)) {
                Files.createFile(errorLogFile);
            }
        } catch (IOException e) {
            System.err.println("❌ Cannot create error log file: " + errorLogFile);
        }
    }

    public void log(Path file, Exception e) {
        String timestamp = now();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        String message = String.format(
                "[%s] ERROR processing file: %s%nReason: %s%nStackTrace:%n%s%n",
                timestamp,
                file.toAbsolutePath(),
                e.getMessage(),
                stackTrace
        );

        append(message);
    }

    public void logTruncation(Path file, long originalSizeBytes, long truncatedToBytes) {
        String timestamp = now();
        String message = String.format(
                "[%s] WARN: File %s (size %.2f MB) truncated to %.2f MB.%n",
                timestamp,
                file.toAbsolutePath(),
                originalSizeBytes / 1_048_576.0,
                truncatedToBytes / 1_048_576.0
        );

        append(message);
    }

    private void append(String message) {
        try {
            Files.writeString(
                    errorLogFile,
                    message + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException ioException) {
            System.err.println("❌ Failed to write to error log: " + ioException.getMessage());
        }
    }

    private String now() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }
}
