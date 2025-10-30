package com.scoreboard.initialization.datasource;

import com.scoreboard.exception.ApplicationStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextFileDataSource implements DataSource{
    private static final Logger logger = LoggerFactory.getLogger(TextFileDataSource.class);

    private final String fileName;

    public TextFileDataSource(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        this.fileName = fileName;
    }

    @Override
    public List<String> getPlayerNames() {
        logger.debug("Reading player names from file: {}", fileName);

        List<String> playerNames = new ArrayList<>();

        try (InputStream input = getFileInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(input, StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                if (!trimmed.isEmpty()) {
                    playerNames.add(trimmed);
                    logger.trace("Line {}: '{}'", lineNumber, trimmed);
                }
            }
        } catch (IOException e) {
            String message = String.format("Failed to read player names from file: %s", fileName);
            logger.error(message, e);
            throw new ApplicationStartupException(message);
        }

        logger.info("Loaded {} player names from file: {}", playerNames.size(), fileName);
        return playerNames;
    }

    private InputStream getFileInputStream() {
        InputStream input = getClass().getResourceAsStream(fileName);

        if (input == null) {
            String message = String.format("File not found in classpath: %s", fileName);
            logger.error(message);
            throw new ApplicationStartupException(message);
        }

        return input;
    }
}
