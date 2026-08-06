package com.malik.InterviewPilot.controller;

import com.malik.InterviewPilot.dto.leaderboard.LeaderboardEntryResponse;
import com.malik.InterviewPilot.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public List<LeaderboardEntryResponse> getLeaderboard() {
        return leaderboardService.getTopScores();
    }
}
