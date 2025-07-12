package app.sigorotalk.backend.config.mentor;

import app.sigorotalk.backend.common.exception.BusinessException;
import app.sigorotalk.backend.domain.mentor.Mentor;
import app.sigorotalk.backend.domain.mentor.MentorRepository;
import app.sigorotalk.backend.domain.mentor.MentorService;
import app.sigorotalk.backend.domain.review.Review;
import app.sigorotalk.backend.domain.review.ReviewRepository;
import app.sigorotalk.backend.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @InjectMocks
    private MentorService mentorService;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("멘토 목록 조회 성공: 페이징된 멘토 목록을 DTO로 변환하여 반환한다.")
    void getMentorList_Success() {
        // given
        User testUser = User.builder().name("테스트 멘토").build();
        Mentor testMentor = Mentor.builder().user(testUser).build();
        Page<Mentor> mentorPage = new PageImpl<>(Collections.singletonList(testMentor));
        Pageable pageable = PageRequest.of(0, 10);

        when(mentorRepository.findAllWithUser(any(Pageable.class))).thenReturn(mentorPage);

        // when
        var result = mentorService.getMentorList(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스트 멘토");
    }

    @Test
    @DisplayName("멘토 상세 조회 성공: 멘토 정보와 리뷰 목록을 포함한 DTO를 반환한다.")
    void getMentorDetail_Success() {
        // given
        User testUser = User.builder().name("상세조회 멘토").build();
        Mentor testMentor = Mentor.builder().id(1L).user(testUser).build();
        List<Review> reviews = Collections.emptyList();

        when(mentorRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testMentor));
        when(reviewRepository.findByCoffeeChatApplicationMentorId(1L)).thenReturn(reviews);

        // when
        var result = mentorService.getMentorDetail(1L);

        // then
        assertThat(result.getMentorId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("상세조회 멘토");
        assertThat(result.getReviews()).isEmpty();
    }

    @Test
    @DisplayName("멘토 상세 조회 실패: 존재하지 않는 ID로 조회 시 BusinessException 발생")
    void getMentorDetail_Failure_NotFound() {
        // given
        when(mentorRepository.findByIdWithUser(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> mentorService.getMentorDetail(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("요청한 리소스를 찾을 수 없습니다.");
    }
}