package com.revisiontracker.storage;

import com.revisiontracker.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    List<UserAccount> findAllByOrderByCreatedAtDesc();
}
