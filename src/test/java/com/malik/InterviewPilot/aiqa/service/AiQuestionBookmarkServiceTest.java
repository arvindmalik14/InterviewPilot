package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestionBookmark;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionBookmarkRepository;
import com.malik.InterviewPilot.entity.User;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiQuestionBookmarkServiceTest {

    private AiQuestionBookmarkRepository bookmarkRepository;
    private AiQuestionService questionService;
    private AiQuestionBookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        bookmarkRepository = mock(AiQuestionBookmarkRepository.class);
        questionService = mock(AiQuestionService.class);
        bookmarkService = new AiQuestionBookmarkService(bookmarkRepository, questionService);
    }

    @Test
    void addBookmark_savesBookmark_whenNotAlreadyBookmarked() {
        User user = User.builder().id(1L).build();
        AiQuestion question = AiQuestion.builder().id(2L).build();
        when(bookmarkRepository.existsByUser_IdAndQuestion_Id(1L, 2L)).thenReturn(false);
        when(questionService.findQuestionOrThrow(2L)).thenReturn(question);

        bookmarkService.addBookmark(user, 2L);

        verify(bookmarkRepository).save(any(AiQuestionBookmark.class));
    }

    @Test
    void addBookmark_rejectsDuplicateBookmark() {
        User user = User.builder().id(1L).build();
        when(bookmarkRepository.existsByUser_IdAndQuestion_Id(1L, 2L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> bookmarkService.addBookmark(user, 2L));
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void removeBookmark_throwsResourceNotFound_whenNoBookmarkExists() {
        User user = User.builder().id(1L).build();
        when(bookmarkRepository.findByUser_IdAndQuestion_Id(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.removeBookmark(user, 2L));
    }
}
