package com.malik.InterviewPilot.aiqa.service;

import com.malik.InterviewPilot.aiqa.dto.question.AiQuestionDetailResponse;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.repository.AiPlanQuestionRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionBookmarkRepository;
import com.malik.InterviewPilot.exception.DuplicateResourceException;
import com.malik.InterviewPilot.exception.ResourceNotFoundException;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionStatus;
import com.malik.InterviewPilot.razorpay.entity.UserSubscription;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionRepository;
import com.malik.InterviewPilot.razorpay.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AiPlanQuestionServiceTest {

    private AiPlanQuestionRepository planQuestionRepository;
    private AiQuestionService questionService;
    private AiQuestionBookmarkRepository bookmarkRepository;
    private PlanService planService;
    private SubscriptionRepository subscriptionRepository;
    private AiPlanQuestionService service;

    @BeforeEach
    void setUp() {
        planQuestionRepository = mock(AiPlanQuestionRepository.class);
        questionService = mock(AiQuestionService.class);
        bookmarkRepository = mock(AiQuestionBookmarkRepository.class);
        planService = mock(PlanService.class);
        subscriptionRepository = mock(SubscriptionRepository.class);
        service = new AiPlanQuestionService(planQuestionRepository, questionService, bookmarkRepository, planService, subscriptionRepository);
    }

    private void stubActivePlan(long userId, long planId, int questionLimit, long assignedCount, List<Long> orderedIds) {
        SubscriptionPlan plan = SubscriptionPlan.builder().planId(planId).questionLimit(questionLimit).build();
        UserSubscription subscription = UserSubscription.builder().plan(plan).subscriptionStatus(SubscriptionStatus.ACTIVE).build();
        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(userId, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.of(subscription));
        when(planQuestionRepository.countByPlan_PlanId(planId)).thenReturn(assignedCount);
        when(planQuestionRepository.findQuestionIdsByPlanIdOrderByQuestionIdAsc(planId)).thenReturn(orderedIds);
    }

    @Test
    void getAccessibleQuestionDetail_allowsQuestion_withinTheEffectiveCap() {
        // questionLimit=3 caps below the 5 assigned rows, so only the first 3 ids (by id asc) are accessible
        stubActivePlan(1L, 10L, 3, 5, List.of(10L, 20L, 30L, 40L, 50L));
        AiQuestion question = AiQuestion.builder().id(30L).title("Q").build();
        when(questionService.findQuestionOrThrow(30L)).thenReturn(question);
        when(questionService.categoriesFor(30L)).thenReturn(List.of());
        when(bookmarkRepository.existsByUser_IdAndQuestion_Id(1L, 30L)).thenReturn(false);

        AiQuestionDetailResponse response = service.getAccessibleQuestionDetail(1L, 30L);

        assertEquals(30L, response.id());
    }

    @Test
    void getAccessibleQuestionDetail_deniesQuestion_beyondTheEffectiveCap() {
        stubActivePlan(1L, 10L, 3, 5, List.of(10L, 20L, 30L, 40L, 50L));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.getAccessibleQuestionDetail(1L, 40L));
        verify(questionService, never()).findQuestionOrThrow(any());
    }

    @Test
    void getAccessibleQuestionDetail_capsByAssignedCount_whenLowerThanPlanLimit() {
        // questionLimit=500 (Basic) but only 2 rows are actually assigned — the assigned count is the binding cap
        stubActivePlan(1L, 20L, 500, 2, List.of(5L, 6L));
        AiQuestion question = AiQuestion.builder().id(6L).title("Q").build();
        when(questionService.findQuestionOrThrow(6L)).thenReturn(question);
        when(questionService.categoriesFor(6L)).thenReturn(List.of());

        assertDoesNotThrow(() -> service.getAccessibleQuestionDetail(1L, 6L));
    }

    @Test
    void getAccessibleQuestionDetail_throwsResourceNotFound_whenNoActiveSubscription() {
        when(subscriptionRepository.findByUserIdAndSubscriptionStatus(1L, SubscriptionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getAccessibleQuestionDetail(1L, 1L));
    }

    @Test
    void assignQuestionToPlan_rejectsDuplicateAssignment() {
        when(planQuestionRepository.existsByPlan_PlanIdAndQuestion_Id(1L, 2L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.assignQuestionToPlan(1L, 2L));
        verify(planQuestionRepository, never()).save(any());
    }

    @Test
    void removeQuestionFromPlan_throwsResourceNotFound_whenNotAssigned() {
        when(planQuestionRepository.findByPlan_PlanIdAndQuestion_Id(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.removeQuestionFromPlan(1L, 2L));
    }
}
