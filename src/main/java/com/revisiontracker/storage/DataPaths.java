package com.revisiontracker.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class DataPaths {
    private final Path dataDir;
    private static final List<String> FILES = List.of(
            "topics.csv",
            "problems.csv",
            "revisions.csv",
            "revision_history.csv",
            "streaks.csv",
            "users.csv"
    );

    public DataPaths(@Value("${tracker.data-dir:data}") String dataDir) {
        this.dataDir = Path.of(dataDir);
    }

    @PostConstruct
    public void initializeDataFiles() {
        try {
            Files.createDirectories(dataDir);
            for (String file : FILES) {
                Path target = dataDir.resolve(file);
                if (!Files.exists(target)) {
                    ClassPathResource sample = new ClassPathResource("data/" + file);
                    try (InputStream in = sample.getInputStream()) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException ex) {
            throw new CsvStorageException("Unable to initialize CSV data files", ex);
        }
    }

    public Path topics() { return dataDir.resolve("topics.csv"); }
    public Path problems() { return dataDir.resolve("problems.csv"); }
    public Path revisions() { return dataDir.resolve("revisions.csv"); }
    public Path revisionHistory() { return dataDir.resolve("revision_history.csv"); }
    public Path streaks() { return dataDir.resolve("streaks.csv"); }
    public Path users() { return dataDir.resolve("users.csv"); }
    public Path dataDir() { return dataDir; }
}
