package com.scoreboard.start_initialization.data_source;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFileDataSource implements DataSource{
    private final String fileName;

    public TextFileDataSource(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<String> getPlayers() {
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

        return lines;
    }
}
