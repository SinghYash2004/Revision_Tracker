package com.revisiontracker.storage;

import com.revisiontracker.model.Problem;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class ProblemRepository {
    private static final List<String> HEADER = List.of("id", "platform", "problemNumber", "name", "difficulty", "topicTags", "solvedDate", "timeTaken", "usedHint", "solvedIndependently", "personalNotes", "lastRevised");
    private final DataPaths paths;

    public ProblemRepository(DataPaths paths) {
        this.paths = paths;
    }

    public synchronized List<Problem> findAll() {
        List<List<String>> rows = CsvTable.readRows(paths.problems());
        List<Problem> problems = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() >= 12 && !row.get(0).isBlank()) {
                problems.add(new Problem(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5),
                        date(row.get(6)), row.get(7), bool(row.get(8)), bool(row.get(9)), row.get(10), date(row.get(11))));
            }
        }
        problems.sort(Comparator.comparing(Problem::getSolvedDate).reversed());
        return problems;
    }

    public synchronized Optional<Problem> findById(String id) {
        return findAll().stream().filter(problem -> problem.getId().equals(id)).findFirst();
    }

    public synchronized Problem save(Problem problem) {
        List<Problem> problems = findAll();
        problems.removeIf(existing -> existing.getId().equals(problem.getId()));
        problems.add(problem);
        write(problems);
        return problem;
    }

    public synchronized void write(List<Problem> problems) {
        List<List<String>> rows = problems.stream()
                .map(p -> List.of(p.getId(), safe(p.getPlatform()), safe(p.getProblemNumber()), safe(p.getName()),
                        safe(p.getDifficulty()), safe(p.getTopicTags()), text(p.getSolvedDate()), safe(p.getTimeTaken()),
                        String.valueOf(p.isUsedHint()), String.valueOf(p.isSolvedIndependently()), safe(p.getPersonalNotes()), text(p.getLastRevised())))
                .toList();
        CsvTable.writeRows(paths.problems(), HEADER, rows);
    }

    private static LocalDate date(String value) { return value == null || value.isBlank() ? null : LocalDate.parse(value); }
    private static boolean bool(String value) { return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")); }
    private static String text(LocalDate date) { return date == null ? "" : date.toString(); }
    private static String safe(String value) { return value == null ? "" : value; }
}
