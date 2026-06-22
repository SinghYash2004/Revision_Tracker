package com.revisiontracker.model;

import java.time.LocalDate;

public class Topic {
    private String id;
    private String name;
    private String category;
    private LocalDate dateLearned;
    private int confidenceLevel;
    private String notes;
    private LocalDate lastRevised;

    public Topic() {
    }

    public Topic(String id, String name, String category, LocalDate dateLearned, int confidenceLevel, String notes, LocalDate lastRevised) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.dateLearned = dateLearned;
        this.confidenceLevel = confidenceLevel;
        this.notes = notes;
        this.lastRevised = lastRevised;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDateLearned() { return dateLearned; }
    public void setDateLearned(LocalDate dateLearned) { this.dateLearned = dateLearned; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(int confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDate getLastRevised() { return lastRevised; }
    public void setLastRevised(LocalDate lastRevised) { this.lastRevised = lastRevised; }
}
