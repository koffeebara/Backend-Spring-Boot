package app.sigorotalk.backend.domain.coffeechat;

import app.sigorotalk.backend.common.exception.BusinessException;
import app.sigorotalk.backend.domain.coffeechat.dto.ChatStatusUpdateResponseDto;
import app.sigorotalk.backend.domain.coffeechat.dto.CoffeeChatApplyRequestDto;
import app.sigorotalk.backend.domain.coffeechat.dto.CoffeeChatApplyResponseDto;
import app.sigorotalk.backend.domain.coffeechat.dto.MyChatListResponseDto;
import app.sigorotalk.backend.domain.mentor.Mentor;
import app.sigorotalk.backend.domain.mentor.MentorRepository;
import app.sigorotalk.backend.domain.user.User;
import app.sigorotalk.backend.domain.user.UserErrorCode;
import app.sigorotalk.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoffeeChatService {
    private final CoffeeChatApplicationRepository coffeeChatApplicationRepository;
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;

    /**
     * 멘티가 멘토에게 커피챗을 신청합니다.
     *
     * @param requestDto 멘토 ID를 포함한 요청 DTO
     * @param menteeId   신청하는 멘티의 ID
     * @return 생성된 커피챗 신청 정보
     */
    @Transactional
    public CoffeeChatApplyResponseDto applyForChat(CoffeeChatApplyRequestDto requestDto, Long menteeId) {
        User mentee = findUserById(menteeId);
        Mentor mentor = mentorRepository.findById(requestDto.getMentorId())
                .orElseThrow(() -> new BusinessException(CoffeeChatErrorCode.MENTOR_NOT_FOUND));

        CoffeeChatApplication application = CoffeeChatApplication.builder()
                .mentee(mentee)
                .mentor(mentor)
                .status(CoffeeChatApplication.Status.PENDING)
                .applicationDate(LocalDateTime.now())
                .build();

        CoffeeChatApplication savedApplication = coffeeChatApplicationRepository.save(application);
        return CoffeeChatApplyResponseDto.from(savedApplication);
    }

    /**
     * 특정 사용자가 관련된 모든 커피챗 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 커피챗 목록 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<MyChatListResponseDto> getMyChats(Long userId) {
        List<CoffeeChatApplication> myApplications = coffeeChatApplicationRepository.findMyChatsByUserId(userId);
        return myApplications.stream()
                .map(MyChatListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 멘토가 커피챗 신청을 수락합니다.
     *
     * @param applicationId 신청 ID
     * @param mentorUserId  수락하는 멘토의 ID
     * @return 상태 변경 결과
     */
    @Transactional
    public ChatStatusUpdateResponseDto acceptChat(Long applicationId, Long mentorUserId) {
        return updateStatus(applicationId, mentorUserId, CoffeeChatApplication::accept);
    }

    /**
     * 멘토가 커피챗 신청을 거절합니다.
     *
     * @param applicationId 신청 ID
     * @param mentorUserId  거절하는 멘토의 ID
     * @return 상태 변경 결과
     */
    @Transactional
    public ChatStatusUpdateResponseDto rejectChat(Long applicationId, Long mentorUserId) {
        return updateStatus(applicationId, mentorUserId, CoffeeChatApplication::reject);
    }

    /**
     * 멘티가 커피챗을 완료 처리합니다.
     *
     * @param applicationId 신청 ID
     * @param menteeUserId  완료하는 멘티의 ID
     * @return 상태 변경 결과
     */
    @Transactional
    public ChatStatusUpdateResponseDto completeChat(Long applicationId, Long menteeUserId) {
        return updateStatus(applicationId, menteeUserId, CoffeeChatApplication::complete);
    }

    /**
     * 상태 변경 로직을 처리하는 공통 헬퍼 메서드
     *
     * @param applicationId 신청 ID
     * @param userId        요청한 사용자의 ID
     * @param action        엔티티의 상태 변경 메서드 (e.g., application::accept)
     * @return 상태 변경 결과 DTO
     */
    private ChatStatusUpdateResponseDto updateStatus(Long applicationId, Long userId, BiConsumer<CoffeeChatApplication, User> action) {
        CoffeeChatApplication application = findApplicationById(applicationId);
        User user = findUserById(userId);
        action.accept(application, user);
        return ChatStatusUpdateResponseDto.from(application);
    }

    // --- private 헬퍼 메서드: 중복되는 조회 로직 분리 ---
    private CoffeeChatApplication findApplicationById(Long applicationId) {
        return coffeeChatApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(CoffeeChatErrorCode.COFFEE_CHAT_NOT_FOUND));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}


