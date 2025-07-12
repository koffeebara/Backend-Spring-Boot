package app.sigorotalk.backend.domain.coffeechat;

import app.sigorotalk.backend.common.entity.BaseTimeEntity;
import app.sigorotalk.backend.common.exception.BusinessException;
import app.sigorotalk.backend.domain.mentor.Mentor;
import app.sigorotalk.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "tb_coffee_chat_application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CoffeeChatApplication extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private User mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    private LocalDateTime applicationDate;

    private LocalDateTime acceptedAt;

    /**
     * 멘토가 커피챗 신청을 수락합니다.
     *
     * @param mentorUser 요청한 사용자 (반드시 멘토여야 함)
     */
    public void accept(User mentorUser) {
        // 1. 권한 검증: 요청자가 이 커피챗의 멘토인지 확인
        if (!this.getMentor().getUser().getId().equals(mentorUser.getId())) {
            throw new BusinessException(CoffeeChatErrorCode.NO_AUTHORITY_TO_UPDATE_STATUS);
        }
        // 2. 상태 검증: 현재 상태가 PENDING일 때만 수락 가능
        if (this.status != Status.PENDING) {
            throw new BusinessException(CoffeeChatErrorCode.CANNOT_CHANGE_STATUS);
        }
        // 3. 상태 변경
        this.status = Status.ACCEPTED;
        this.acceptedAt = LocalDateTime.now(); // 수락 시간 기록
    }

    // --- 비즈니스 로직을 엔티티로 이전 ---

    /**
     * 멘토가 커피챗 신청을 거절합니다.
     *
     * @param mentorUser 요청한 사용자 (반드시 멘토여야 함)
     */
    public void reject(User mentorUser) {
        if (!this.getMentor().getUser().getId().equals(mentorUser.getId())) {
            throw new BusinessException(CoffeeChatErrorCode.NO_AUTHORITY_TO_UPDATE_STATUS);
        }
        if (this.status != Status.PENDING) {
            throw new BusinessException(CoffeeChatErrorCode.CANNOT_CHANGE_STATUS);
        }
        this.status = Status.REJECTED;
    }

    /**
     * 멘티가 커피챗을 완료 처리합니다.
     *
     * @param menteeUser 요청한 사용자 (반드시 멘티여야 함)
     */
    public void complete(User menteeUser) {
        // 1. 권한 검증: 요청자가 이 커피챗의 멘티인지 확인
        if (!this.getMentee().getId().equals(menteeUser.getId())) {
            throw new BusinessException(CoffeeChatErrorCode.NO_AUTHORITY_TO_UPDATE_STATUS);
        }
        // 2. 상태 검증: 수락(ACCEPTED) 상태의 커피챗만 완료 처리 가능
        if (this.status != Status.ACCEPTED) {
            throw new BusinessException(CoffeeChatErrorCode.INVALID_STATUS_FOR_COMPLETION);
        }
        // 3. 상태 변경
        this.status = Status.COMPLETED;
    }

    public enum Status {
        PENDING, ACCEPTED, REJECTED, COMPLETED
    }
}
