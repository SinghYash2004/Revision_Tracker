package com.revisiontracker.dto;

import com.revisiontracker.model.Revision;

import java.time.LocalDate;

public record RevisionItem(
        String revisionId,
        String itemType,
        String itemId,
        String title,
        String subtitle,
        String notes,
        LocalDate dueDate,
        int daysLate,
        Revision revision
) {
}
