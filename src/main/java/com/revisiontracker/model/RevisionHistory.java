package com.revisiontracker.model;

import java.time.LocalDate;

public class RevisionHistory {
    private String id;
    private TrackableType itemType;
    private String itemId;
    private LocalDate revisedDate;
    private int rating;
    private String recallLevel;
    private String notes;

    public RevisionHistory() {
    }

    public RevisionHistory(String id, TrackableType itemType, String itemId, LocalDate revisedDate, int rating, String recallLevel, String notes) {
        this.id = id;
        this.itemType = itemType;
        this.itemId = itemId;
        this.revisedDate = revisedDate;
        this.rating = rating;
        this.recallLevel = recallLevel;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TrackableType getItemType() { return itemType; }
    public void setItemType(TrackableType itemType) { this.itemType = itemType; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public LocalDate getRevisedDate() { return revisedDate; }
    public void setRevisedDate(LocalDate revisedDate) { this.revisedDate = revisedDate; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getRecallLevel() { return recallLevel; }
    public void setRecallLevel(String recallLevel) { this.recallLevel = recallLevel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
