package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionSummaryResponse;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestionBookmark;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionBookmarkRepository;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiQuestionBookmarkService {

    private final AiQuestionBookmarkRepository bookmarkRepository;
    private final AiQuestionService questionService;

    public void addBookmark(User user, Long questionId) {
        if (bookmarkRepository.existsByUser_IdAndQuestion_Id(user.getId(), questionId)) {
            throw new DuplicateResourceException("Question " + questionId + " is already bookmarked");
        }
        AiQuestion question = questionService.findQuestionOrThrow(questionId);
        bookmarkRepository.save(AiQuestionBookmark.builder().user(user).question(question).build());
    }

    public void removeBookmark(User user, Long questionId) {
        AiQuestionBookmark bookmark = bookmarkRepository.findByUser_IdAndQuestion_Id(user.getId(), questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found for question " + questionId));
        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public Page<AiQuestionSummaryResponse> listBookmarks(User user, Pageable pageable) {
        Page<AiQuestionBookmark> page = bookmarkRepository.findByUser_Id(user.getId(), pageable);
        List<Long> questionIds = page.getContent().stream().map(b -> b.getQuestion().getId()).toList();
        var categories = questionService.categoriesForQuestions(questionIds);
        return page.map(b -> AiQuestionSummaryResponse.from(
                b.getQuestion(), categories.getOrDefault(b.getQuestion().getId(), List.of()), true));
    }
}
