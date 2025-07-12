package app.sigorotalk.backend.config.coffeechat;

import app.sigorotalk.backend.common.exception.BusinessException;
import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplication;
import app.sigorotalk.backend.domain.mentor.Mentor;
import app.sigorotalk.backend.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoffeeChatApplicationTest {

    private User mentorUser;
    private User menteeUser;
    private User anotherUser;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        mentorUser = User.builder().id(1L).name("테스트멘토").build();
        menteeUser = User.builder().id(2L).name("테스트멘티").build();
        anotherUser = User.builder().id(3L).name("다른사용자").build();
        mentor = Mentor.builder().id(1L).user(mentorUser).build();
    }

    private CoffeeChatApplication createPendingApplication() {
        return CoffeeChatApplication.builder()
                .id(1L)
                .mentor(mentor)
                .mentee(menteeUser)
                .status(CoffeeChatApplication.Status.PENDING)
                .build();
    }

    @Nested
    @DisplayName("커피챗 수락(accept) 테스트")
    class AcceptTest {
        @Test
        @DisplayName("성공: 멘토가 PENDING 상태의 신청을 수락한다.")
        void accept_Success() {
            CoffeeChatApplication application = createPendingApplication();
            application.accept(mentorUser);
            assertThat(application.getStatus()).isEqualTo(CoffeeChatApplication.Status.ACCEPTED);
            assertThat(application.getAcceptedAt()).isNotNull();
        }

        @Test
        @DisplayName("실패: 멘토가 아닌 다른 사용자가 수락을 시도하면 예외가 발생한다.")
        void accept_Failure_NoAuthority() {
            CoffeeChatApplication application = createPendingApplication();
            assertThatThrownBy(() -> application.accept(anotherUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 커피챗의 상태를 변경할 권한이 없습니다.");
        }

        @Test
        @DisplayName("실패: PENDING 상태가 아닌 신청을 수락하면 예외가 발생한다.")
        void accept_Failure_InvalidStatus() {
            CoffeeChatApplication application = createPendingApplication();
            application.accept(mentorUser);
            assertThatThrownBy(() -> application.accept(mentorUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("현재 상태에서는 요청한 작업으로 상태를 변경할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("커피챗 거절(reject) 테스트")
    class RejectTest {
        @Test
        @DisplayName("성공: 멘토가 PENDING 상태의 신청을 거절한다.")
        void reject_Success() {
            CoffeeChatApplication application = createPendingApplication();
            application.reject(mentorUser);
            assertThat(application.getStatus()).isEqualTo(CoffeeChatApplication.Status.REJECTED);
            assertThat(application.getAcceptedAt()).isNull();
        }

        @Test
        @DisplayName("실패: 멘토가 아닌 다른 사용자가 거절하면 예외가 발생한다.")
        void reject_Failure_NoAuthority() {
            CoffeeChatApplication application = createPendingApplication();
            assertThatThrownBy(() -> application.reject(menteeUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 커피챗의 상태를 변경할 권한이 없습니다.");
        }

        @Test
        @DisplayName("실패: PENDING 상태가 아닌 신청을 거절하면 예외가 발생한다.")
        void reject_Failure_InvalidStatus() {
            CoffeeChatApplication application = createPendingApplication();
            application.accept(mentorUser);
            assertThatThrownBy(() -> application.reject(mentorUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("현재 상태에서는 요청한 작업으로 상태를 변경할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("커피챗 완료(complete) 테스트")
    class CompleteTest {
        @Test
        @DisplayName("성공: 멘티가 ACCEPTED 상태의 커피챗을 완료 처리한다.")
        void complete_Success() {
            CoffeeChatApplication application = createPendingApplication();
            application.accept(mentorUser);
            application.complete(menteeUser);
            assertThat(application.getStatus()).isEqualTo(CoffeeChatApplication.Status.COMPLETED);
        }

        @Test
        @DisplayName("실패: 멘티가 아닌 다른 사용자가 완료를 시도하면 예외가 발생한다.")
        void complete_Failure_NoAuthority() {
            CoffeeChatApplication application = createPendingApplication();
            application.accept(mentorUser);
            assertThatThrownBy(() -> application.complete(anotherUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 커피챗의 상태를 변경할 권한이 없습니다.");
        }

        @Test
        @DisplayName("실패: ACCEPTED 상태가 아닌 커피챗을 완료하면 예외가 발생한다.")
        void complete_Failure_InvalidStatus() {
            CoffeeChatApplication application = createPendingApplication();
            assertThatThrownBy(() -> application.complete(menteeUser))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("수락(ACCEPTED) 상태의 커피챗만 완료 처리할 수 있습니다.");
        }
    }
}