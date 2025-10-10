package com.scoreboard.start_initialization.data_source;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TextFileDataSource implements DataSource{
    private final String fileName;

    @Override
    public List<String> getPlayerNames() {
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
