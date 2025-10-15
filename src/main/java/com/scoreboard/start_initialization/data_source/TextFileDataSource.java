package com.scoreboard.start_initialization.data_source;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TextFileDataSource implements DataSource{
    private static final Logger logger = LoggerFactory.getLogger(TextFileDataSource.class);
    private final String fileName;

    @Override
    public List<String> getPlayerNames() {
        logger.debug("Reading player names from file: {}", fileName);

        List<String> lines = new ArrayList<>();
        InputStream input = getClass().getResourceAsStream(fileName);

        if (input == null) {
            throw new RuntimeException("File not found: " + fileName);
        }

        try (input; BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                if (!trimmed.isEmpty()) {
                    lines.add(trimmed);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading file: " + fileName);
        }

        logger.info("Loaded {} player names from file: {}", lines.size(), fileName);
        return lines;
    }
}
