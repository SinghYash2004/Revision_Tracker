package com.revisiontracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    private String id;
    private String platform;
    private String problemNumber;
    private String name;
    private String difficulty;
    private String topicTags;
    private LocalDate solvedDate;
    private String timeTaken;
    private boolean usedHint;
    private boolean solvedIndependently;
    @Column(columnDefinition = "TEXT")
    private String personalNotes;
    private LocalDate lastRevised;

    public Problem() {
    }

    public Problem(String id, String platform, String problemNumber, String name, String difficulty, String topicTags,
                   LocalDate solvedDate, String timeTaken, boolean usedHint, boolean solvedIndependently,
                   String personalNotes, LocalDate lastRevised) {
        this.id = id;
        this.platform = platform;
        this.problemNumber = problemNumber;
        this.name = name;
        this.difficulty = difficulty;
        this.topicTags = topicTags;
        this.solvedDate = solvedDate;
        this.timeTaken = timeTaken;
        this.usedHint = usedHint;
        this.solvedIndependently = solvedIndependently;
        this.personalNotes = personalNotes;
        this.lastRevised = lastRevised;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getProblemNumber() { return problemNumber; }
    public void setProblemNumber(String problemNumber) { this.problemNumber = problemNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getTopicTags() { return topicTags; }
    public void setTopicTags(String topicTags) { this.topicTags = topicTags; }
    public LocalDate getSolvedDate() { return solvedDate; }
    public void setSolvedDate(LocalDate solvedDate) { this.solvedDate = solvedDate; }
    public String getTimeTaken() { return timeTaken; }
    public void setTimeTaken(String timeTaken) { this.timeTaken = timeTaken; }
    public boolean isUsedHint() { return usedHint; }
    public void setUsedHint(boolean usedHint) { this.usedHint = usedHint; }
    public boolean isSolvedIndependently() { return solvedIndependently; }
    public void setSolvedIndependently(boolean solvedIndependently) { this.solvedIndependently = solvedIndependently; }
    public String getPersonalNotes() { return personalNotes; }
    public void setPersonalNotes(String personalNotes) { this.personalNotes = personalNotes; }
    public LocalDate getLastRevised() { return lastRevised; }
    public void setLastRevised(LocalDate lastRevised) { this.lastRevised = lastRevised; }
}
