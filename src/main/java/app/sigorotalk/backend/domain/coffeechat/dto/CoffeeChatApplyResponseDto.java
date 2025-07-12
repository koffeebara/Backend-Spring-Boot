package app.sigorotalk.backend.domain.coffeechat.dto;

import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplication;

public record CoffeeChatApplyResponseDto(Long applicationId, String mentorName, String status) {
    public static CoffeeChatApplyResponseDto from(CoffeeChatApplication application) {
        return new CoffeeChatApplyResponseDto(
                application.getId(),
                application.getMentor().getUser().getName(),
                application.getStatus().name()
        );
    }
}
