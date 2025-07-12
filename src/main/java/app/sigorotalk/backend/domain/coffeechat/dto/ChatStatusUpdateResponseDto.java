package app.sigorotalk.backend.domain.coffeechat.dto;

import app.sigorotalk.backend.domain.coffeechat.CoffeeChatApplication;

public record ChatStatusUpdateResponseDto(Long applicationId, String status) {
    public static ChatStatusUpdateResponseDto from(CoffeeChatApplication application) {
        return new ChatStatusUpdateResponseDto(application.getId(), application.getStatus().name());
    }
}