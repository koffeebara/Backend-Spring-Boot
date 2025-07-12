package app.sigorotalk.backend.domain.coffeechat;


import app.sigorotalk.backend.common.response.ApiResponse;
import app.sigorotalk.backend.domain.coffeechat.dto.ChatStatusUpdateResponseDto;
import app.sigorotalk.backend.domain.coffeechat.dto.CoffeeChatApplyRequestDto;
import app.sigorotalk.backend.domain.coffeechat.dto.CoffeeChatApplyResponseDto;
import app.sigorotalk.backend.domain.coffeechat.dto.MyChatListResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class CoffeeChatController {
    private final CoffeeChatService coffeeChatService;

    @PostMapping
    public ResponseEntity<ApiResponse<CoffeeChatApplyResponseDto>> applyForChat(
            @Valid @RequestBody CoffeeChatApplyRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long menteeId = Long.valueOf(userDetails.getUsername());
        CoffeeChatApplyResponseDto responseDto = coffeeChatService.applyForChat(requestDto, menteeId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<MyChatListResponseDto>>> getMyChats(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        List<MyChatListResponseDto> responseDto = coffeeChatService.getMyChats(userId);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PatchMapping("/{chatId}/accept")
    public ResponseEntity<ApiResponse<ChatStatusUpdateResponseDto>> acceptChat(
            @PathVariable("chatId") Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long mentorId = Long.valueOf(userDetails.getUsername());
        // 수정된 서비스 메서드 호출
        ChatStatusUpdateResponseDto responseDto = coffeeChatService.acceptChat(applicationId, mentorId);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PatchMapping("/{chatId}/reject")
    public ResponseEntity<ApiResponse<ChatStatusUpdateResponseDto>> rejectChat(
            @PathVariable("chatId") Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long mentorId = Long.valueOf(userDetails.getUsername());
        // 수정된 서비스 메서드 호출
        ChatStatusUpdateResponseDto responseDto = coffeeChatService.rejectChat(applicationId, mentorId);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PatchMapping("/{chatId}/complete")
    public ResponseEntity<ApiResponse<ChatStatusUpdateResponseDto>> completeChat(
            @PathVariable("chatId") Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long menteeId = Long.valueOf(userDetails.getUsername());
        // 수정된 서비스 메서드 호출
        ChatStatusUpdateResponseDto responseDto = coffeeChatService.completeChat(applicationId, menteeId);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}
