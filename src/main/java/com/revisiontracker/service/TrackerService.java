package com.revisiontracker.service;

import com.revisiontracker.model.Problem;
import com.revisiontracker.model.Topic;
import com.revisiontracker.model.TrackableType;
import com.revisiontracker.storage.ProblemRepository;
import com.revisiontracker.storage.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TrackerService {
    private final TopicRepository topics;
    private final ProblemRepository problems;
    private final RevisionService revisions;
    private final StreakService streaks;

    public TrackerService(TopicRepository topics, ProblemRepository problems, RevisionService revisions, StreakService streaks) {
        this.topics = topics;
        this.problems = problems;
        this.revisions = revisions;
        this.streaks = streaks;
    }

    public List<Topic> topics() {
        return topics.findAllByOrderByDateLearnedDesc();
    }

    @Transactional
    public Topic addTopic(Topic topic) {
        topic.setId(UUID.randomUUID().toString());
        if (topic.getDateLearned() == null) {
            topic.setDateLearned(LocalDate.now());
        }
        topic.setLastRevised(topic.getDateLearned());
        topics.save(topic);
        revisions.createInitialSchedule(TrackableType.TOPIC, topic.getId(), topic.getDateLearned());
        streaks.recordTopic();
        return topic;
    }

    public List<Problem> problems() {
        return problems.findAllByOrderBySolvedDateDesc();
    }

    @Transactional
    public Problem addProblem(Problem problem) {
        problem.setId(UUID.randomUUID().toString());
        if (problem.getSolvedDate() == null) {
            problem.setSolvedDate(LocalDate.now());
        }
        problem.setLastRevised(problem.getSolvedDate());
        problems.save(problem);
        revisions.createInitialSchedule(TrackableType.PROBLEM, problem.getId(), problem.getSolvedDate());
        streaks.recordProblem();
        return problem;
    }
}
