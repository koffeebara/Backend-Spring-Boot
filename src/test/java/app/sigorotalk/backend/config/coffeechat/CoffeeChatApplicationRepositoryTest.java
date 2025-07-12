package app.sigorotalk.backend.config.coffeechat;

import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplication;
import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplicationRepository;
import app.sigorotalk.backend.domain.mentor.Mentor;
import app.sigorotalk.backend.domain.mentor.MentorRepository;
import app.sigorotalk.backend.domain.user.User;
import app.sigorotalk.backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CoffeeChatApplicationRepositoryTest {

    @Autowired
    private CoffeeChatApplicationRepository coffeeChatApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MentorRepository mentorRepository;

    private User user1_mentee, user2_both, user3_mentor;
    private Mentor mentor2, mentor3;

    @BeforeEach
    void setUp() {
        user1_mentee = userRepository.save(User.builder().email("mentee1@test.com").name("멘티1").password("pw").role(User.Role.ROLE_USER).build());
        user2_both = userRepository.save(User.builder().email("user2@test.com").name("유저2_멘토겸멘티").password("pw").role(User.Role.ROLE_MENTOR).build());
        user3_mentor = userRepository.save(User.builder().email("mentor3@test.com").name("멘토3").password("pw").role(User.Role.ROLE_MENTOR).build());

        mentor2 = mentorRepository.save(Mentor.builder().user(user2_both).build());
        mentor3 = mentorRepository.save(Mentor.builder().user(user3_mentor).build());
    }

    @Test
    @DisplayName("findMyChatsByUserId: 유저가 멘토/멘티인 모든 채팅을 조회하고, 최신순으로 정렬한다.")
    void findMyChatsByUserId_whenUserIsBothMentorAndMentee() {
        // given
        // 시나리오 1: user2가 멘토, user1이 멘티 (중간 날짜)
        CoffeeChatApplication app1 = coffeeChatApplicationRepository.save(CoffeeChatApplication.builder()
                .mentor(mentor2).mentee(user1_mentee).status(CoffeeChatApplication.Status.PENDING).applicationDate(LocalDateTime.now().minusDays(1)).build());
        // 시나리오 2: user3이 멘토, user2가 멘티 (가장 최신)
        CoffeeChatApplication app2 = coffeeChatApplicationRepository.save(CoffeeChatApplication.builder()
                .mentor(mentor3).mentee(user2_both).status(CoffeeChatApplication.Status.ACCEPTED).applicationDate(LocalDateTime.now()).build());
        // 시나리오 3: user2와 무관한 채팅
        coffeeChatApplicationRepository.save(CoffeeChatApplication.builder()
                .mentor(mentor3).mentee(user1_mentee).status(CoffeeChatApplication.Status.COMPLETED).applicationDate(LocalDateTime.now().minusDays(3)).build());

        // when (user2로 조회)
        List<CoffeeChatApplication> result = coffeeChatApplicationRepository.findMyChatsByUserId(user2_both.getId());

        // then
        // 1. 개수 및 내용 검증 (순서와 상관없이 올바른 데이터가 포함되었는지)
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CoffeeChatApplication::getId)
                .containsExactlyInAnyOrder(app1.getId(), app2.getId());

        // 2. 정렬(ORDER BY) 검증 (최신순으로 정렬되었는지)
        assertThat(result).isSortedAccordingTo(Comparator.comparing(CoffeeChatApplication::getApplicationDate).reversed());

        // 3. JOIN FETCH 검증 (첫번째 결과는 가장 최신인 app2여야 함)
        assertThat(result.get(0).getMentor().getUser().getName()).isEqualTo("멘토3");
        assertThat(result.get(0).getMentee().getName()).isEqualTo("유저2_멘토겸멘티");
    }
}