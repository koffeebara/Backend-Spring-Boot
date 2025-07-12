package app.sigorotalk.backend.config.coffeechat;

import app.sigorotalk.backend.common.exception.BusinessException;
import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplication;
import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplicationRepository;
import app.sigorotalk.backend.domain.coffeechat.CoffeeChatErrorCode;
import app.sigorotalk.backend.domain.coffeechat.CoffeeChatService;
import app.sigorotalk.backend.domain.coffeechat.dto.CoffeeChatApplyRequestDto;
import app.sigorotalk.backend.domain.coffeechat.dto.CoffeeChatApplyResponseDto;
import app.sigorotalk.backend.domain.coffeechat.dto.MyChatListResponseDto;
import app.sigorotalk.backend.domain.mentor.Mentor;
import app.sigorotalk.backend.domain.mentor.MentorRepository;
import app.sigorotalk.backend.domain.user.User;
import app.sigorotalk.backend.domain.user.UserErrorCode;
import app.sigorotalk.backend.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoffeeChatServiceTest {

    @InjectMocks
    private CoffeeChatService coffeeChatService;
    @Mock
    private CoffeeChatApplicationRepository coffeeChatApplicationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorRepository mentorRepository;

    @Test
    @DisplayName("신청 성공: applyForChat은 PENDING 상태의 신청서를 생성하고 save를 호출한다.")
    void applyForChat_Success() {
        long menteeId = 1L;
        long mentorId = 2L;
        CoffeeChatApplyRequestDto requestDto = new CoffeeChatApplyRequestDto();
        ReflectionTestUtils.setField(requestDto, "mentorId", mentorId);
        User mentee = User.builder().id(menteeId).build();
        User mentorUser = User.builder().id(mentorId).name("테스트멘토").build();
        Mentor mentor = Mentor.builder().id(mentorId).user(mentorUser).build();

        when(userRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(coffeeChatApplicationRepository.save(any(CoffeeChatApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CoffeeChatApplyResponseDto response = coffeeChatService.applyForChat(requestDto, menteeId);

        verify(coffeeChatApplicationRepository, times(1)).save(any(CoffeeChatApplication.class));
        assertThat(response.status()).isEqualTo(CoffeeChatApplication.Status.PENDING.name());
        assertThat(response.mentorName()).isEqualTo("테스트멘토");
    }

    @Test
    @DisplayName("수락 성공: acceptChat은 올바른 엔티티를 찾아 accept() 메서드를 호출한다.")
    void acceptChat_Success() {
        long applicationId = 1L;
        long mentorUserId = 2L;
        User mentorUser = User.builder().id(mentorUserId).build();
        CoffeeChatApplication application = spy(CoffeeChatApplication.builder().status(CoffeeChatApplication.Status.PENDING).build());

        when(coffeeChatApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(userRepository.findById(mentorUserId)).thenReturn(Optional.of(mentorUser));
        doNothing().when(application).accept(any(User.class));

        coffeeChatService.acceptChat(applicationId, mentorUserId);

        verify(application, times(1)).accept(mentorUser);
    }

    @Test
    @DisplayName("거절 성공: rejectChat은 올바른 엔티티를 찾아 reject() 메서드를 호출한다.")
    void rejectChat_Success() {
        long applicationId = 1L;
        long mentorUserId = 2L;
        User mentorUser = User.builder().id(mentorUserId).build();
        CoffeeChatApplication application = spy(CoffeeChatApplication.builder().status(CoffeeChatApplication.Status.PENDING).build());

        when(coffeeChatApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(userRepository.findById(mentorUserId)).thenReturn(Optional.of(mentorUser));
        doNothing().when(application).reject(any(User.class));

        coffeeChatService.rejectChat(applicationId, mentorUserId);

        verify(application, times(1)).reject(mentorUser);
    }

    @Test
    @DisplayName("완료 성공: completeChat은 올바른 엔티티를 찾아 complete() 메서드를 호출한다.")
    void completeChat_Success() {
        long applicationId = 1L;
        long menteeUserId = 3L;
        User menteeUser = User.builder().id(menteeUserId).build();
        CoffeeChatApplication application = spy(CoffeeChatApplication.builder().status(CoffeeChatApplication.Status.ACCEPTED).build());

        when(coffeeChatApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(userRepository.findById(menteeUserId)).thenReturn(Optional.of(menteeUser));
        doNothing().when(application).complete(any(User.class));

        coffeeChatService.completeChat(applicationId, menteeUserId);

        verify(application, times(1)).complete(menteeUser);
    }

    @Test
    @DisplayName("목록 조회 성공: getMyChats는 repository의 조회 결과를 DTO 리스트로 변환한다.")
    void getMyChats_Success() {
        // given
        long userId = 1L;
        User menteeUser = User.builder().id(userId).name("테스트멘티").build();
        User mentorUser = User.builder().id(2L).name("테스트멘토").build();
        Mentor mentor = Mentor.builder().user(mentorUser).profileImageUrl("mentor.jpg").build();

        CoffeeChatApplication app1 = CoffeeChatApplication.builder()
                .id(100L)
                .mentor(mentor)
                .mentee(menteeUser)
                .status(CoffeeChatApplication.Status.ACCEPTED)
                .build();

        when(coffeeChatApplicationRepository.findMyChatsByUserId(userId)).thenReturn(List.of(app1));

        // when
        List<MyChatListResponseDto> result = coffeeChatService.getMyChats(userId);

        // then
        verify(coffeeChatApplicationRepository, times(1)).findMyChatsByUserId(userId);

        // DTO의 내용까지 상세하게 검증
        assertThat(result).hasSize(1);
        MyChatListResponseDto resultDto = result.get(0);
        assertThat(resultDto.applicationId()).isEqualTo(100L);
        assertThat(resultDto.mentor().name()).isEqualTo("테스트멘토");
        assertThat(resultDto.mentee().name()).isEqualTo("테스트멘티");
        assertThat(resultDto.status()).isEqualTo("ACCEPTED");
        assertThat(resultDto.mentor().profileImageUrl()).isEqualTo("mentor.jpg");
    }

    @Test
    @DisplayName("신청 실패: 멘티를 찾을 수 없으면 BusinessException이 발생한다.")
    void applyForChat_Failure_MenteeNotFound() {
        // given
        long nonExistentMenteeId = 99L;
        CoffeeChatApplyRequestDto requestDto = new CoffeeChatApplyRequestDto();
        ReflectionTestUtils.setField(requestDto, "mentorId", 1L);

        when(userRepository.findById(nonExistentMenteeId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> coffeeChatService.applyForChat(requestDto, nonExistentMenteeId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("신청 실패: 멘토를 찾을 수 없으면 BusinessException이 발생한다.")
    void applyForChat_Failure_MentorNotFound() {
        // given
        long menteeId = 1L;
        long nonExistentMentorId = 99L;
        CoffeeChatApplyRequestDto requestDto = new CoffeeChatApplyRequestDto();
        ReflectionTestUtils.setField(requestDto, "mentorId", nonExistentMentorId);

        when(userRepository.findById(menteeId)).thenReturn(Optional.of(User.builder().id(menteeId).build()));
        when(mentorRepository.findById(nonExistentMentorId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> coffeeChatService.applyForChat(requestDto, menteeId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CoffeeChatErrorCode.MENTOR_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상태 변경 실패: 신청서가 존재하지 않으면 BusinessException이 발생한다.")
    void statusUpdate_Failure_ApplicationNotFound() {
        // given
        long nonExistentApplicationId = 99L;
        long userId = 1L;

        when(coffeeChatApplicationRepository.findById(nonExistentApplicationId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> coffeeChatService.acceptChat(nonExistentApplicationId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CoffeeChatErrorCode.COFFEE_CHAT_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> coffeeChatService.rejectChat(nonExistentApplicationId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CoffeeChatErrorCode.COFFEE_CHAT_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> coffeeChatService.completeChat(nonExistentApplicationId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CoffeeChatErrorCode.COFFEE_CHAT_NOT_FOUND.getMessage());
    }

}