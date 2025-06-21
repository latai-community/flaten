package com.flating.processor;

import com.flating.util.ErrorLogger;
import com.flating.util.SummaryLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles reading, truncating, and writing a single text file to the .flat output.
 */
public class FileProcessor {

    private static final long MAX_FILE_SIZE_BYTES = 1_048_576L; // 1 MB

    private final String delimiterLine;
    private final String delimiterContext;
    private final String encoding;

    private final ErrorLogger errorLogger;
    private final SummaryLogger summaryLogger;

    public FileProcessor(
            String delimiterLine,
            String delimiterContext,
            String encoding,
            ErrorLogger errorLogger,
            SummaryLogger summaryLogger
    ) {
        this.delimiterLine = delimiterLine;
        this.delimiterContext = delimiterContext;
        this.encoding = encoding;
        this.errorLogger = errorLogger;
        this.summaryLogger = summaryLogger;
    }

    public void processFile(Path file, Writer writer) {
        try {
            long fileSize = Files.size(file);
            boolean willTruncate = fileSize > MAX_FILE_SIZE_BYTES;

            // Read only up to MAX_FILE_SIZE_BYTES if truncating
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName(encoding))) {
                long totalBytesRead = 0;
                int nextChar;

                while ((nextChar = reader.read()) != -1) {
                    totalBytesRead++;
                    contentBuilder.append((char) nextChar);
                    if (willTruncate && totalBytesRead >= MAX_FILE_SIZE_BYTES) break;
                }
            }

            // Log truncation if needed
            if (willTruncate) {
                errorLogger.logTruncation(file, fileSize, MAX_FILE_SIZE_BYTES);
            }

            String filePathStr = file.toString();

            // Write the structured flat block
            writer.write(delimiterLine);
            writer.write(System.lineSeparator());

            writer.write(filePathStr);
            writer.write(System.lineSeparator());

            // Context delimiter must be longer than file path
            writer.write(delimiterContext);
            writer.write(System.lineSeparator());

            writer.write(contentBuilder.toString());
            writer.write(System.lineSeparator());

            writer.write(delimiterLine);
            writer.write(System.lineSeparator());

            writer.flush();

            summaryLogger.addProcessed(file, Math.min(fileSize, MAX_FILE_SIZE_BYTES));

        } catch (IOException e) {
            errorLogger.log(file, e);
            summaryLogger.addFailed(file, e.getMessage());
        }
    }
}
