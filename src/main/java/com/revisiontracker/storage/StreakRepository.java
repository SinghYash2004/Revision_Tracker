package com.revisiontracker.storage;

import com.revisiontracker.model.StreakStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the singleton StreakStats row.
 * Use StreakRepository directly in StreakService via the helper methods below.
 * The row always has id = 1.
 */
@Repository
public interface StreakRepository extends JpaRepository<StreakStats, Integer> {

    /**
     * Returns the singleton stats row, creating a zero-state one if it doesn't exist yet.
     */
    default StreakStats get() {
        return findById(1).orElseGet(() -> {
            StreakStats blank = new StreakStats(0, 0, 0, 0, 0, null);
            return save(blank);
        });
    }
}
