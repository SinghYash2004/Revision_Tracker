package com.revisiontracker.storage;

import com.revisiontracker.model.UserAccount;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private static final List<String> HEADER = List.of("id", "name", "email", "passwordHash", "createdAt");
    private final DataPaths paths;

    public UserRepository(DataPaths paths) {
        this.paths = paths;
    }

    public synchronized List<UserAccount> findAll() {
        List<List<String>> rows = CsvTable.readRows(paths.users());
        List<UserAccount> users = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() >= 5 && !row.get(0).isBlank()) {
                users.add(new UserAccount(row.get(0), row.get(1), row.get(2), row.get(3), LocalDateTime.parse(row.get(4))));
            }
        }
        users.sort(Comparator.comparing(UserAccount::getCreatedAt).reversed());
        return users;
    }

    public synchronized Optional<UserAccount> findById(String id) {
        return findAll().stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public synchronized Optional<UserAccount> findByEmail(String email) {
        String normalized = normalize(email);
        return findAll().stream().filter(user -> normalize(user.getEmail()).equals(normalized)).findFirst();
    }

    public synchronized UserAccount save(UserAccount user) {
        List<UserAccount> users = findAll();
        users.removeIf(existing -> existing.getId().equals(user.getId()));
        users.add(user);
        List<List<String>> rows = users.stream()
                .map(u -> List.of(u.getId(), safe(u.getName()), normalize(u.getEmail()), u.getPasswordHash(), u.getCreatedAt().toString()))
                .toList();
        CsvTable.writeRows(paths.users(), HEADER, rows);
        return user;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
