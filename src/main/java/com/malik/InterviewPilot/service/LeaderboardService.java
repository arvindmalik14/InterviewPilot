package com.malik.InterviewPilot.service;

import com.malik.InterviewPilot.dto.leaderboard.LeaderboardEntryResponse;
import com.malik.InterviewPilot.entity.TestAttempt;
import com.malik.InterviewPilot.repository.TestAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardService {

    private static final int TOP_N = 20;

    private final TestAttemptRepository testAttemptRepository;

    public List<LeaderboardEntryResponse> getTopScores() {
        List<TestAttempt> best = testAttemptRepository.findByStatusOrderByScoreDesc("COMPLETED").stream()
                .collect(Collectors.toMap(
                        a -> a.getUser().getId() + "-" + a.getExam().getId(),
                        a -> a,
                        (a, b) -> a.getScore() >= b.getScore() ? a : b))
                .values().stream()
                .sorted(Comparator.comparing(TestAttempt::getScore).reversed())
                .limit(TOP_N)
                .toList();

        List<LeaderboardEntryResponse> entries = new ArrayList<>();
        int rank = 1;
        for (TestAttempt attempt : best) {
            entries.add(new LeaderboardEntryResponse(
                    rank++, attempt.getUser().getName(), attempt.getExam().getName(),
                    attempt.getScore(), attempt.getTotalQuestions()));
        }
        return entries;
    }
}
