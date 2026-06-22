package com.revisiontracker.model;

import java.time.LocalDate;

public class Revision {
    private String id;
    private TrackableType itemType;
    private String itemId;
    private LocalDate dueDate;
    private RevisionStatus status;
    private int intervalDays;

    public Revision() {
    }

    public Revision(String id, TrackableType itemType, String itemId, LocalDate dueDate, RevisionStatus status, int intervalDays) {
        this.id = id;
        this.itemType = itemType;
        this.itemId = itemId;
        this.dueDate = dueDate;
        this.status = status;
        this.intervalDays = intervalDays;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TrackableType getItemType() { return itemType; }
    public void setItemType(TrackableType itemType) { this.itemType = itemType; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public RevisionStatus getStatus() { return status; }
    public void setStatus(RevisionStatus status) { this.status = status; }
    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }
}
