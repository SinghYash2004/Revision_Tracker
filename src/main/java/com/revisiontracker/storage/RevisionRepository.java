package com.revisiontracker.storage;

import com.revisiontracker.model.Revision;
import com.revisiontracker.model.RevisionStatus;
import com.revisiontracker.model.TrackableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, String> {

    List<Revision> findAllByOrderByDueDateAsc();

    /**
     * Replaces the old deletePendingFor() logic — removes all PENDING revisions
     * for a given item so a fresh one can be scheduled after completion.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Revision r WHERE r.itemType = :type AND r.itemId = :itemId AND r.status = :status")
    void deleteByItemTypeAndItemIdAndStatus(
            @Param("type") TrackableType type,
            @Param("itemId") String itemId,
            @Param("status") RevisionStatus status);
}
