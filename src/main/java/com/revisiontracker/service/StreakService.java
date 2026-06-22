package com.revisiontracker.service;

import com.revisiontracker.model.StreakStats;
import com.revisiontracker.storage.StreakRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StreakService {
    private final StreakRepository repository;

    public StreakService(StreakRepository repository) {
        this.repository = repository;
    }

    public StreakStats get() {
        return repository.get();
    }

    public void recordTopic() {
        StreakStats stats = recordActivity();
        stats.setTotalTopicsLearned(stats.getTotalTopicsLearned() + 1);
        repository.save(stats);
    }

    public void recordProblem() {
        StreakStats stats = recordActivity();
        stats.setTotalProblemsSolved(stats.getTotalProblemsSolved() + 1);
        repository.save(stats);
    }

    public void recordRevision() {
        StreakStats stats = recordActivity();
        stats.setTotalRevisionsCompleted(stats.getTotalRevisionsCompleted() + 1);
        repository.save(stats);
    }

    private StreakStats recordActivity() {
        StreakStats stats = repository.get();
        LocalDate today = LocalDate.now();
        LocalDate last = stats.getLastActivityDate();
        if (last == null) {
            stats.setCurrentStreak(1);
        } else if (last.equals(today)) {
            return stats;
        } else if (last.equals(today.minusDays(1))) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else {
            stats.setCurrentStreak(1);
        }
        stats.setLongestStreak(Math.max(stats.getLongestStreak(), stats.getCurrentStreak()));
        stats.setLastActivityDate(today);
        return stats;
    }
}
