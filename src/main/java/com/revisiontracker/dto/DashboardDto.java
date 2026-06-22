package com.revisiontracker.dto;

import com.revisiontracker.model.StreakStats;

import java.util.List;
import java.util.Map;

public record DashboardDto(
        List<RevisionItem> today,
        List<RevisionItem> overdue,
        long topicsLearned,
        long problemsSolved,
        int estimatedRevisionMinutes,
        StreakStats streaks,
        Map<String, Integer> readiness,
        int overallReadiness
) {
}
