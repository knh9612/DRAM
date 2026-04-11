package com.macdduck.dram.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // Auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    OAUTH_FAILED(HttpStatus.UNAUTHORIZED, "소셜 인증에 실패했습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // Whisky
    WHISKY_NOT_FOUND(HttpStatus.NOT_FOUND, "위스키를 찾을 수 없습니다."),

    // Wishlist
    WISHLIST_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 위시리스트에 추가된 위스키입니다."),

    // Note
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "테이스팅 노트를 찾을 수 없습니다."),

    // Chat
    SESSION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 세션 수를 초과했습니다.");

    private final HttpStatus status;
    private final String message;
}