package com.revisiontracker.storage;

import com.revisiontracker.model.StreakStats;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class StreakRepository {
    private static final List<String> HEADER = List.of("currentStreak", "longestStreak", "totalTopicsLearned", "totalProblemsSolved", "totalRevisionsCompleted", "lastActivityDate");
    private final DataPaths paths;

    public StreakRepository(DataPaths paths) {
        this.paths = paths;
    }

    public synchronized StreakStats get() {
        List<List<String>> rows = CsvTable.readRows(paths.streaks());
        if (rows.size() < 2 || rows.get(1).size() < 6) {
            return new StreakStats(0, 0, 0, 0, 0, null);
        }
        List<String> row = rows.get(1);
        return new StreakStats(integer(row.get(0)), integer(row.get(1)), integer(row.get(2)), integer(row.get(3)),
                integer(row.get(4)), row.get(5).isBlank() ? null : LocalDate.parse(row.get(5)));
    }

    public synchronized void save(StreakStats stats) {
        CsvTable.writeRows(paths.streaks(), HEADER, List.of(List.of(
                String.valueOf(stats.getCurrentStreak()),
                String.valueOf(stats.getLongestStreak()),
                String.valueOf(stats.getTotalTopicsLearned()),
                String.valueOf(stats.getTotalProblemsSolved()),
                String.valueOf(stats.getTotalRevisionsCompleted()),
                stats.getLastActivityDate() == null ? "" : stats.getLastActivityDate().toString()
        )));
    }

    private static int integer(String value) { return value == null || value.isBlank() ? 0 : Integer.parseInt(value); }
}
