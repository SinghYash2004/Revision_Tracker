package com.revisiontracker.service;

import com.revisiontracker.dto.RevisionCompleteRequest;
import com.revisiontracker.dto.RevisionItem;
import com.revisiontracker.model.*;
import com.revisiontracker.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RevisionService {
    private static final int[] INITIAL_INTERVALS = {1, 3, 7, 15, 30, 60, 120};
    private static final Map<Integer, Integer> RATING_INTERVALS = Map.of(1, 1, 2, 3, 3, 7, 4, 15, 5, 30);

    private final RevisionRepository revisions;
    private final RevisionHistoryRepository history;
    private final TopicRepository topics;
    private final ProblemRepository problems;
    private final StreakService streakService;

    public RevisionService(RevisionRepository revisions, RevisionHistoryRepository history, TopicRepository topics,
                           ProblemRepository problems, StreakService streakService) {
        this.revisions = revisions;
        this.history = history;
        this.topics = topics;
        this.problems = problems;
        this.streakService = streakService;
    }

    @Transactional
    public void createInitialSchedule(TrackableType type, String itemId, LocalDate baseDate) {
        List<Revision> additions = new ArrayList<>();
        for (int interval : INITIAL_INTERVALS) {
            additions.add(new Revision(id(), type, itemId, baseDate.plusDays(interval), RevisionStatus.PENDING, interval));
        }
        revisions.saveAll(additions); // replaces old addAll()
    }

    public List<RevisionItem> today() {
        LocalDate today = LocalDate.now();
        return revisions.findAllByOrderByDueDateAsc().stream()
                .filter(r -> r.getStatus() == RevisionStatus.PENDING && r.getDueDate().equals(today))
                .map(this::toItem)
                .toList();
    }

    public List<RevisionItem> overdue() {
        LocalDate today = LocalDate.now();
        return revisions.findAllByOrderByDueDateAsc().stream()
                .filter(r -> r.getStatus() == RevisionStatus.PENDING && r.getDueDate().isBefore(today))
                .map(this::toItem)
                .toList();
    }

    public List<RevisionItem> allPending() {
        return revisions.findAllByOrderByDueDateAsc().stream()
                .filter(r -> r.getStatus() == RevisionStatus.PENDING)
                .map(this::toItem)
                .toList();
    }

    @Transactional
    public Revision complete(String revisionId, RevisionCompleteRequest request) {
        Revision revision = revisions.findById(revisionId).orElseThrow();
        revision.setStatus(RevisionStatus.COMPLETED);
        revisions.save(revision);

        int rating = Math.max(1, Math.min(5, request.getRating()));
        history.save(new RevisionHistory(id(), revision.getItemType(), revision.getItemId(), LocalDate.now(), rating,
                request.getRecallLevel(), request.getNotes()));

        if (revision.getItemType() == TrackableType.TOPIC) {
            topics.findById(revision.getItemId()).ifPresent(topic -> {
                topic.setLastRevised(LocalDate.now());
                topic.setConfidenceLevel(rating);
                topics.save(topic);
            });
        } else {
            problems.findById(revision.getItemId()).ifPresent(problem -> {
                problem.setLastRevised(LocalDate.now());
                problems.save(problem);
            });
        }

        // Delete all remaining PENDING revisions for this item, then schedule the next one
        revisions.deleteByItemTypeAndItemIdAndStatus(revision.getItemType(), revision.getItemId(), RevisionStatus.PENDING);
        int nextInterval = RATING_INTERVALS.get(rating);
        revisions.save(new Revision(id(), revision.getItemType(), revision.getItemId(),
                LocalDate.now().plusDays(nextInterval), RevisionStatus.PENDING, nextInterval));
        streakService.recordRevision();
        return revision;
    }

    public List<RevisionHistory> history() {
        return history.findAllByOrderByRevisedDateDesc();
    }

    private RevisionItem toItem(Revision revision) {
        LocalDate today = LocalDate.now();
        int daysLate = Math.max(0, (int) ChronoUnit.DAYS.between(revision.getDueDate(), today));
        if (revision.getItemType() == TrackableType.TOPIC) {
            Topic topic = topics.findById(revision.getItemId()).orElse(null);
            String title = topic == null ? "Deleted topic" : topic.getName();
            String subtitle = topic == null ? "" : topic.getCategory();
            String notes = topic == null ? "" : topic.getNotes();
            return new RevisionItem(revision.getId(), "TOPIC", revision.getItemId(), title, subtitle, notes, revision.getDueDate(), daysLate, revision);
        }
        Problem problem = problems.findById(revision.getItemId()).orElse(null);
        String title = problem == null ? "Deleted problem" : problem.getName();
        String subtitle = problem == null ? "" : problem.getPlatform() + " " + problem.getProblemNumber();
        String notes = problem == null ? "" : problem.getPersonalNotes();
        return new RevisionItem(revision.getId(), "PROBLEM", revision.getItemId(), title, subtitle, notes, revision.getDueDate(), daysLate, revision);
    }

    private static String id() {
        return UUID.randomUUID().toString();
    }
}
