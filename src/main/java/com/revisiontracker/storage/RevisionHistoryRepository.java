package com.revisiontracker.storage;

import com.revisiontracker.model.RevisionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevisionHistoryRepository extends JpaRepository<RevisionHistory, String> {
    List<RevisionHistory> findAllByOrderByRevisedDateDesc();
}
