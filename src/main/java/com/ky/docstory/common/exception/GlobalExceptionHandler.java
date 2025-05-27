package com.ky.docstory.common.exception;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.dto.DocStoryResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DocStoryResponseBody<Void>> handleBusinessException(BusinessException e) {
        DocStoryResponseCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(DocStoryResponseBody.error(errorCode.getCode(), errorCode.getMessage()));
    }

    // 컨트롤러 유효성 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DocStoryResponseBody<Void>> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(DocStoryResponseCode.VALIDATION_ERROR.getHttpStatus())
                .body(DocStoryResponseBody.error(
                        DocStoryResponseCode.VALIDATION_ERROR.getCode(),
                        e.getBindingResult().getAllErrors().get(0).getDefaultMessage() // 첫 번째 메시지만 노출
                ));
    }

    // 요청 형식이 잘못된 경우 처리 (ex. Enum 파싱 실패 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DocStoryResponseBody<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        DocStoryResponseCode errorCode = DocStoryResponseCode.BAD_JSON_FORMAT;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(DocStoryResponseBody.error(errorCode.getCode(), errorCode.getMessage()));
    }


    // 그 외 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DocStoryResponseBody<Void>> handleGeneralException(Exception e) {
        return ResponseEntity
                .status(DocStoryResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(DocStoryResponseBody.error(
                        DocStoryResponseCode.INTERNAL_SERVER_ERROR.getCode(),
                        DocStoryResponseCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
