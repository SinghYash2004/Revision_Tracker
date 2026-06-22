package com.revisiontracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revisiontracker.dto.AuthRequest;
import com.revisiontracker.dto.AuthResponse;
import com.revisiontracker.dto.RevisionCompleteRequest;
import com.revisiontracker.model.Problem;
import com.revisiontracker.model.Topic;
import com.revisiontracker.service.AnalyticsService;
import com.revisiontracker.service.AuthService;
import com.revisiontracker.service.RevisionService;
import com.revisiontracker.service.TrackerService;
import com.revisiontracker.storage.DataPaths;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final TrackerService tracker;
    private final RevisionService revisions;
    private final AnalyticsService analytics;
    private final AuthService authService;
    private final DataPaths dataPaths;
    private final ObjectMapper objectMapper;

    public ApiController(TrackerService tracker, RevisionService revisions, AnalyticsService analytics,
                         AuthService authService, DataPaths dataPaths, ObjectMapper objectMapper) {
        this.tracker = tracker;
        this.revisions = revisions;
        this.analytics = analytics;
        this.authService = authService;
        this.dataPaths = dataPaths;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/auth/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/dashboard")
    public Object dashboard() {
        return analytics.dashboard();
    }

    @GetMapping("/topics")
    public List<Topic> topics(@RequestParam(required = false) String q,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return tracker.topics().stream()
                .filter(t -> q == null || contains(t.getName(), q) || contains(t.getNotes(), q) || contains(t.getCategory(), q))
                .filter(t -> category == null || category.isBlank() || t.getCategory().equalsIgnoreCase(category))
                .filter(t -> date == null || date.equals(t.getDateLearned()))
                .toList();
    }

    @PostMapping("/topics")
    public Topic addTopic(@RequestBody Topic topic) {
        return tracker.addTopic(topic);
    }

    @GetMapping("/problems")
    public List<Problem> problems(@RequestParam(required = false) String q,
                                  @RequestParam(required = false) String difficulty,
                                  @RequestParam(required = false) String tag,
                                  @RequestParam(required = false) String platform,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return tracker.problems().stream()
                .filter(p -> q == null || contains(p.getName(), q) || contains(p.getProblemNumber(), q) || contains(p.getTopicTags(), q) || contains(p.getPersonalNotes(), q))
                .filter(p -> difficulty == null || difficulty.isBlank() || p.getDifficulty().equalsIgnoreCase(difficulty))
                .filter(p -> tag == null || tag.isBlank() || contains(p.getTopicTags(), tag))
                .filter(p -> platform == null || platform.isBlank() || p.getPlatform().equalsIgnoreCase(platform))
                .filter(p -> date == null || date.equals(p.getSolvedDate()))
                .toList();
    }

    @PostMapping("/problems")
    public Problem addProblem(@RequestBody Problem problem) {
        return tracker.addProblem(problem);
    }

    @GetMapping("/revisions/today")
    public Object today() {
        return Map.of("today", revisions.today(), "overdue", revisions.overdue(), "pending", revisions.allPending());
    }

    @PostMapping("/revisions/{revisionId}/complete")
    public Object completeRevision(@PathVariable String revisionId, @RequestBody RevisionCompleteRequest request) {
        return revisions.complete(revisionId, request);
    }

    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        return analytics.analytics();
    }

    @GetMapping("/progress")
    public Map<String, Object> progress() {
        Map<String, Object> data = new LinkedHashMap<>(analytics.analytics());
        data.put("dashboard", analytics.dashboard());
        return data;
    }

    @GetMapping("/export/json")
    public ResponseEntity<ByteArrayResource> exportJson() throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("topics", tracker.topics());
        payload.put("problems", tracker.problems());
        payload.put("revisions", revisions.allPending());
        payload.put("revisionHistory", revisions.history());
        byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(payload);
        return download("dsa-revision-tracker-export.json", MediaType.APPLICATION_JSON, bytes);
    }

    @GetMapping("/export/csv/{file}")
    public ResponseEntity<ByteArrayResource> exportCsv(@PathVariable String file) throws IOException {
        List<String> allowed = List.of("topics.csv", "problems.csv", "revisions.csv", "revision_history.csv", "streaks.csv");
        if (!allowed.contains(file)) {
            return ResponseEntity.badRequest().build();
        }
        byte[] bytes = Files.readAllBytes(dataPaths.dataDir().resolve(file));
        return download(file, MediaType.parseMediaType("text/csv"), bytes);
    }

    private static boolean contains(String value, String query) {
        return value != null && query != null && value.toLowerCase().contains(query.toLowerCase());
    }

    private static ResponseEntity<ByteArrayResource> download(String filename, MediaType mediaType, byte[] bytes) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .contentLength(bytes.length)
                .body(new ByteArrayResource(bytes));
    }
}
