package com.revisiontracker.storage;

import com.revisiontracker.model.Revision;
import com.revisiontracker.model.RevisionStatus;
import com.revisiontracker.model.TrackableType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class RevisionRepository {
    private static final List<String> HEADER = List.of("id", "itemType", "itemId", "dueDate", "status", "intervalDays");
    private final DataPaths paths;

    public RevisionRepository(DataPaths paths) {
        this.paths = paths;
    }

    public synchronized List<Revision> findAll() {
        List<List<String>> rows = CsvTable.readRows(paths.revisions());
        List<Revision> revisions = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() >= 6 && !row.get(0).isBlank()) {
                revisions.add(new Revision(row.get(0), TrackableType.valueOf(row.get(1)), row.get(2),
                        LocalDate.parse(row.get(3)), RevisionStatus.valueOf(row.get(4)), Integer.parseInt(row.get(5))));
            }
        }
        revisions.sort(Comparator.comparing(Revision::getDueDate));
        return revisions;
    }

    public synchronized Optional<Revision> findById(String id) {
        return findAll().stream().filter(revision -> revision.getId().equals(id)).findFirst();
    }

    public synchronized Revision save(Revision revision) {
        List<Revision> revisions = findAll();
        revisions.removeIf(existing -> existing.getId().equals(revision.getId()));
        revisions.add(revision);
        write(revisions);
        return revision;
    }

    public synchronized void deletePendingFor(TrackableType type, String itemId) {
        List<Revision> revisions = findAll();
        revisions.removeIf(r -> r.getItemType() == type && r.getItemId().equals(itemId) && r.getStatus() == RevisionStatus.PENDING);
        write(revisions);
    }

    public synchronized void addAll(List<Revision> additions) {
        List<Revision> revisions = findAll();
        revisions.addAll(additions);
        write(revisions);
    }

    public synchronized void write(List<Revision> revisions) {
        List<List<String>> rows = revisions.stream()
                .map(r -> List.of(r.getId(), r.getItemType().name(), r.getItemId(), r.getDueDate().toString(),
                        r.getStatus().name(), String.valueOf(r.getIntervalDays())))
                .toList();
        CsvTable.writeRows(paths.revisions(), HEADER, rows);
    }
}
