package com.malik.InterviewPilot.dto.leaderboard;

public record LeaderboardEntryResponse(
        int rank,
        String userName,
        String examName,
        int score,
        int totalQuestions
) {
}
