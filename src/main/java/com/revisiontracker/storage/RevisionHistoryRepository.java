package com.revisiontracker.storage;

import com.revisiontracker.model.RevisionHistory;
import com.revisiontracker.model.TrackableType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class RevisionHistoryRepository {
    private static final List<String> HEADER = List.of("id", "itemType", "itemId", "revisedDate", "rating", "recallLevel", "notes");
    private final DataPaths paths;

    public RevisionHistoryRepository(DataPaths paths) {
        this.paths = paths;
    }

    public synchronized List<RevisionHistory> findAll() {
        List<List<String>> rows = CsvTable.readRows(paths.revisionHistory());
        List<RevisionHistory> history = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() >= 7 && !row.get(0).isBlank()) {
                history.add(new RevisionHistory(row.get(0), TrackableType.valueOf(row.get(1)), row.get(2),
                        LocalDate.parse(row.get(3)), Integer.parseInt(row.get(4)), row.get(5), row.get(6)));
            }
        }
        history.sort(Comparator.comparing(RevisionHistory::getRevisedDate).reversed());
        return history;
    }

    public synchronized RevisionHistory save(RevisionHistory item) {
        List<RevisionHistory> history = findAll();
        history.add(item);
        write(history);
        return item;
    }

    public synchronized void write(List<RevisionHistory> history) {
        List<List<String>> rows = history.stream()
                .map(h -> List.of(h.getId(), h.getItemType().name(), h.getItemId(), h.getRevisedDate().toString(),
                        String.valueOf(h.getRating()), safe(h.getRecallLevel()), safe(h.getNotes())))
                .toList();
        CsvTable.writeRows(paths.revisionHistory(), HEADER, rows);
    }

    private static String safe(String value) { return value == null ? "" : value; }
}
