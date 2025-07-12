package app.sigorotalk.backend.domain.coffeechat;

import app.sigorotalk.backend.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatErrorCode implements ErrorCode {

    MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 멘토 정보를 찾을 수 없습니다."),
    COFFEE_CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 커피챗 신청 정보를 찾을 수 없습니다."),
    NO_AUTHORITY_TO_UPDATE_STATUS(HttpStatus.FORBIDDEN, 403, "해당 커피챗의 상태를 변경할 권한이 없습니다."),
    INVALID_STATUS_FOR_COMPLETION(HttpStatus.BAD_REQUEST, 400, "수락(ACCEPTED) 상태의 커피챗만 완료 처리할 수 있습니다."),
    CANNOT_CHANGE_STATUS(HttpStatus.BAD_REQUEST, 400, "현재 상태에서는 요청한 작업으로 상태를 변경할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}
