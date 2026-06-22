package com.revisiontracker.service;

import com.revisiontracker.dto.DashboardDto;
import com.revisiontracker.model.Problem;
import com.revisiontracker.model.RevisionHistory;
import com.revisiontracker.model.Topic;
import com.revisiontracker.storage.ProblemRepository;
import com.revisiontracker.storage.TopicRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private static final List<String> CORE_TAGS = List.of("Array", "String", "HashMap", "Binary Search", "Sliding Window", "Tree", "Graph", "DP");

    private final TopicRepository topics;
    private final ProblemRepository problems;
    private final RevisionService revisions;
    private final StreakService streaks;

    public AnalyticsService(TopicRepository topics, ProblemRepository problems, RevisionService revisions, StreakService streaks) {
        this.topics = topics;
        this.problems = problems;
        this.revisions = revisions;
        this.streaks = streaks;
    }

    public DashboardDto dashboard() {
        int dueCount = revisions.today().size() + revisions.overdue().size();
        Map<String, Integer> readiness = readinessByCategory();
        int overall = readiness.values().stream().mapToInt(Integer::intValue).sum() / Math.max(1, readiness.size());
        return new DashboardDto(revisions.today(), revisions.overdue(), topics.findAll().size(), problems.findAll().size(),
                dueCount * 12, streaks.get(), readiness, overall);
    }

    public Map<String, Object> analytics() {
        Map<String, Object> data = new LinkedHashMap<>();
        List<Topic> topicList = topics.findAll();
        List<Problem> problemList = problems.findAll();
        List<RevisionHistory> history = revisions.history();
        data.put("problemsPerMonth", perMonth(problemList.stream().map(Problem::getSolvedDate).toList()));
        data.put("topicsPerMonth", perMonth(topicList.stream().map(Topic::getDateLearned).toList()));
        data.put("tagDistribution", tagCounts(problemList));
        data.put("difficultyDistribution", problemList.stream().collect(Collectors.groupingBy(Problem::getDifficulty, TreeMap::new, Collectors.counting())));
        long completed = history.size();
        long pendingDue = revisions.today().size() + revisions.overdue().size();
        data.put("revisionCompletionRate", completed == 0 ? 0 : Math.round((completed * 100.0) / (completed + pendingDue)));
        data.put("forgottenTopics", forgottenTopics());
        data.put("weakAreas", weakAreas());
        data.put("recommendations", recommendations());
        data.put("readiness", readinessByCategory());
        data.put("overallReadiness", dashboard().overallReadiness());
        data.put("heatmap", heatmap());
        return data;
    }

    public List<Map<String, Object>> forgottenTopics() {
        LocalDate today = LocalDate.now();
        return topics.findAll().stream()
                .map(topic -> {
                    LocalDate last = topic.getLastRevised() == null ? topic.getDateLearned() : topic.getLastRevised();
                    long days = ChronoUnit.DAYS.between(last, today);
                    String risk = days >= 90 ? "High Risk" : days >= 60 ? "Medium Risk" : days >= 30 ? "Watch" : "Fresh";
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("topic", topic.getName());
                    item.put("category", topic.getCategory());
                    item.put("daysSinceRevision", days);
                    item.put("risk", risk);
                    return item;
                })
                .filter(item -> ((Long) item.get("daysSinceRevision")) >= 30)
                .toList();
    }

    public Map<String, Long> tagCounts(List<Problem> problemList) {
        Map<String, Long> counts = new TreeMap<>();
        for (Problem problem : problemList) {
            for (String tag : splitTags(problem.getTopicTags())) {
                counts.put(tag, counts.getOrDefault(tag, 0L) + 1);
            }
        }
        return counts;
    }

    public List<Map<String, Object>> weakAreas() {
        Map<String, Long> counts = tagCounts(problems.findAll());
        return CORE_TAGS.stream()
                .map(tag -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("tag", tag);
                    item.put("count", counts.getOrDefault(tag, 0L));
                    item.put("recommendation", "Practice " + tag + " problems this week.");
                    return item;
                })
                .sorted(Comparator.comparingLong(item -> (Long) item.get("count")))
                .limit(4)
                .toList();
    }

    public List<String> recommendations() {
        Set<String> solvedNames = problems.findAll().stream().map(p -> p.getName().toLowerCase()).collect(Collectors.toSet());
        Map<String, List<String>> catalog = Map.of(
                "HashMap", List.of("Top K Frequent Elements", "Longest Consecutive Sequence"),
                "Array", List.of("Product of Array Except Self", "Maximum Subarray"),
                "String", List.of("Longest Substring Without Repeating Characters", "Encode and Decode Strings"),
                "Binary Search", List.of("Search in Rotated Sorted Array", "Median of Two Sorted Arrays"),
                "Graph", List.of("Number of Islands", "Course Schedule"),
                "DP", List.of("Climbing Stairs", "House Robber"),
                "Tree", List.of("Invert Binary Tree", "Binary Tree Level Order Traversal")
        );
        Set<String> tags = tagCounts(problems.findAll()).keySet();
        List<String> recs = new ArrayList<>();
        for (String tag : tags) {
            for (String candidate : catalog.getOrDefault(tag, List.of())) {
                if (!solvedNames.contains(candidate.toLowerCase())) {
                    recs.add(candidate + " (" + tag + ")");
                }
            }
        }
        if (recs.isEmpty()) {
            recs.add("Practice Graph problems this week.");
            recs.add("Add two medium DP problems to improve category balance.");
        }
        return recs.stream().limit(6).toList();
    }

    public Map<String, Integer> readinessByCategory() {
        if (topics.findAll().isEmpty() && problems.findAll().isEmpty()) {
            Map<String, Integer> emptyReadiness = new LinkedHashMap<>();
            for (String tag : CORE_TAGS) {
                emptyReadiness.put(tag, 0);
            }
            return emptyReadiness;
        }
        Map<String, Long> tags = tagCounts(problems.findAll());
        Map<String, Integer> readiness = new LinkedHashMap<>();
        for (String tag : CORE_TAGS) {
            long solved = tags.getOrDefault(tag, 0L);
            int coverage = (int) Math.min(100, solved * 12);
            int revisionFactor = Math.max(0, 100 - revisions.overdue().size() * 4);
            readiness.put(tag, Math.round((coverage * 0.65f) + (revisionFactor * 0.35f)));
        }
        return readiness;
    }

    private Map<String, Long> perMonth(List<LocalDate> dates) {
        return dates.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(d -> YearMonth.from(d).toString(), TreeMap::new, Collectors.counting()));
    }

    private Map<String, Long> heatmap() {
        Map<String, Long> days = new TreeMap<>();
        for (Topic topic : topics.findAll()) {
            days.put(topic.getDateLearned().toString(), days.getOrDefault(topic.getDateLearned().toString(), 0L) + 1);
        }
        for (Problem problem : problems.findAll()) {
            days.put(problem.getSolvedDate().toString(), days.getOrDefault(problem.getSolvedDate().toString(), 0L) + 1);
        }
        for (RevisionHistory item : revisions.history()) {
            days.put(item.getRevisedDate().toString(), days.getOrDefault(item.getRevisedDate().toString(), 0L) + 1);
        }
        return days;
    }

    private static List<String> splitTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split("[;|,]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
