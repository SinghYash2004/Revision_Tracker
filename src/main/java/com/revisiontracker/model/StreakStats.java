package com.revisiontracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Singleton entity – there is always exactly one row in this table (id = 1).
 * The StreakRepository handles fetching/creating it automatically.
 */
@Entity
@Table(name = "streak_stats")
public class StreakStats {
    @Id
    private int id = 1; // singleton row

    private int currentStreak;
    private int longestStreak;
    private int totalTopicsLearned;
    private int totalProblemsSolved;
    private int totalRevisionsCompleted;
    private LocalDate lastActivityDate;

    public StreakStats() {
    }

    public StreakStats(int currentStreak, int longestStreak, int totalTopicsLearned, int totalProblemsSolved,
                       int totalRevisionsCompleted, LocalDate lastActivityDate) {
        this.id = 1;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.totalTopicsLearned = totalTopicsLearned;
        this.totalProblemsSolved = totalProblemsSolved;
        this.totalRevisionsCompleted = totalRevisionsCompleted;
        this.lastActivityDate = lastActivityDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public int getTotalTopicsLearned() { return totalTopicsLearned; }
    public void setTotalTopicsLearned(int totalTopicsLearned) { this.totalTopicsLearned = totalTopicsLearned; }
    public int getTotalProblemsSolved() { return totalProblemsSolved; }
    public void setTotalProblemsSolved(int totalProblemsSolved) { this.totalProblemsSolved = totalProblemsSolved; }
    public int getTotalRevisionsCompleted() { return totalRevisionsCompleted; }
    public void setTotalRevisionsCompleted(int totalRevisionsCompleted) { this.totalRevisionsCompleted = totalRevisionsCompleted; }
    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }
}
