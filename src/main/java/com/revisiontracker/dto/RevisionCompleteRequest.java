package com.revisiontracker.dto;

public class RevisionCompleteRequest {
    private int rating;
    private String recallLevel;
    private String notes;

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getRecallLevel() { return recallLevel; }
    public void setRecallLevel(String recallLevel) { this.recallLevel = recallLevel; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
