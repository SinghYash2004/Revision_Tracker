package com.revisiontracker.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvTable {
    private CsvTable() {
    }

    public static List<List<String>> readRows(Path path) {
        try {
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<List<String>> rows = new ArrayList<>();
            for (String line : lines) {
                rows.add(parseLine(line));
            }
            return rows;
        } catch (IOException ex) {
            throw new CsvStorageException("Unable to read CSV file " + path, ex);
        }
    }

    public static void writeRows(Path path, List<String> header, List<List<String>> rows) {
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add(formatLine(header));
            for (List<String> row : rows) {
                lines.add(formatLine(row));
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new CsvStorageException("Unable to write CSV file " + path, ex);
        }
    }

    private static List<String> parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        return values;
    }

    private static String formatLine(List<String> values) {
        List<String> escaped = new ArrayList<>();
        for (String raw : values) {
            String value = raw == null ? "" : raw;
            if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
                escaped.add("\"" + value.replace("\"", "\"\"") + "\"");
            } else {
                escaped.add(value);
            }
        }
        return String.join(",", escaped);
    }
}
