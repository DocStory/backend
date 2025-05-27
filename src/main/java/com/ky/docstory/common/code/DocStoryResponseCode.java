package com.ky.docstory.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DocStoryResponseCode {
    SUCCESS(100, "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),
    BAD_REQUEST(400, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 비즈니스 로직 관련 상세 오류
    VALIDATION_ERROR(1000, "유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_RESOURCE(1001, "중복된 데이터가 존재합니다.", HttpStatus.CONFLICT),
    RESOURCE_CONFLICT(1002, "리소스 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    DATABASE_ERROR(1003, "데이터베이스 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REGISTRATION_FAILED(1004, "데이터 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_FAILED(1005, "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PARAMETER_ERROR(1006, "파라미터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    JWT_EXPIRED(1007, "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_MALFORMED(1008, "유효하지 않은 토큰 형식입니다.", HttpStatus.UNAUTHORIZED),
    JWT_BADSIGN(1009, "토큰 서명 검증 실패", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED(1010, "지원되지 않는 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_DECODING(1011, "토큰 디코딩 오류입니다.", HttpStatus.UNAUTHORIZED),
    JWT_ILLEGAL(1012, "토큰이 비어있거나 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNAUTHORIZED(1013, "토큰 검증 중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_SOCIAL_PROVIDER(1014, "지원하지 않는 소셜 로그인 제공자입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1015, "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_HANDLED(1016, "이미 처리된 요청입니다.", HttpStatus.CONFLICT),
    FILE_DOWNLOAD_FAILED(1017, "파일 다운로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED(1018, "파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE(1019, "유효하지 않은 파일입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_MERGED_PROPOSAL(1020, "이미 병합된 Proposal 입니다.", HttpStatus.CONFLICT),
    BAD_JSON_FORMAT(1021, "요청 형식이 올바르지 않습니다. 입력 값을 확인해주세요.", HttpStatus.BAD_REQUEST);



    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
