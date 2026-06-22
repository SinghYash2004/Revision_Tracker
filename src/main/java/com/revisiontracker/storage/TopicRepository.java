package com.revisiontracker.storage;

import com.revisiontracker.model.Topic;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class TopicRepository {
    private static final List<String> HEADER = List.of("id", "name", "category", "dateLearned", "confidenceLevel", "notes", "lastRevised");
    private final DataPaths paths;

    public TopicRepository(DataPaths paths) {
        this.paths = paths;
    }

    public synchronized List<Topic> findAll() {
        List<List<String>> rows = CsvTable.readRows(paths.topics());
        List<Topic> topics = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() >= 7 && !row.get(0).isBlank()) {
                topics.add(new Topic(row.get(0), row.get(1), row.get(2), date(row.get(3)), integer(row.get(4)), row.get(5), date(row.get(6))));
            }
        }
        topics.sort(Comparator.comparing(Topic::getDateLearned).reversed());
        return topics;
    }

    public synchronized Optional<Topic> findById(String id) {
        return findAll().stream().filter(topic -> topic.getId().equals(id)).findFirst();
    }

    public synchronized Topic save(Topic topic) {
        List<Topic> topics = findAll();
        topics.removeIf(existing -> existing.getId().equals(topic.getId()));
        topics.add(topic);
        write(topics);
        return topic;
    }

    public synchronized void write(List<Topic> topics) {
        List<List<String>> rows = topics.stream()
                .map(t -> List.of(t.getId(), safe(t.getName()), safe(t.getCategory()), text(t.getDateLearned()),
                        String.valueOf(t.getConfidenceLevel()), safe(t.getNotes()), text(t.getLastRevised())))
                .toList();
        CsvTable.writeRows(paths.topics(), HEADER, rows);
    }

    private static LocalDate date(String value) { return value == null || value.isBlank() ? null : LocalDate.parse(value); }
    private static int integer(String value) { return value == null || value.isBlank() ? 0 : Integer.parseInt(value); }
    private static String text(LocalDate date) { return date == null ? "" : date.toString(); }
    private static String safe(String value) { return value == null ? "" : value; }
}
