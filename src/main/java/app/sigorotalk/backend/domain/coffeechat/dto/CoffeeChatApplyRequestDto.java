package app.sigorotalk.backend.domain.coffeechat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoffeeChatApplyRequestDto {

    @NotNull(message = "멘토 ID는 필수입니다.")
    private Long mentorId;
}
