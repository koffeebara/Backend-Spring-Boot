package app.sigorotalk.backend.domain.coffeechat.dto;

import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplication;

import java.time.LocalDateTime;
import java.util.List;

public record MyChatListResponseDto(
        Long applicationId,
        UserInfo mentor,
        UserInfo mentee,
        String status,
        LocalDateTime applicationDate,
        String contactInfo
) {
    public static MyChatListResponseDto from(CoffeeChatApplication application) {
        UserInfo mentorInfo = new UserInfo(
                application.getMentor().getUser().getName(),
                application.getMentor().getProfileImageUrl()
        );

        UserInfo menteeInfo = new UserInfo(
                application.getMentee().getName(),
                null // 멘티는 프로필 이미지가 없음
        );

        String contact = null;
        if (List.of(CoffeeChatApplication.Status.ACCEPTED, CoffeeChatApplication.Status.COMPLETED)
                .contains(application.getStatus())) {
            contact = application.getMentor().getUser().getEmail();
        }


        return new MyChatListResponseDto(
                application.getId(),
                mentorInfo,
                menteeInfo,
                application.getStatus().name(),
                application.getApplicationDate(),
                contact
        );
    }

    // record 내부에 다른 record를 정의할 수 있습니다.
    public record UserInfo(String name, String profileImageUrl) {
    }
}