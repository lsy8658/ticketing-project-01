package com.ticket.concert.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트를 찾을 수 없습니다."),
    VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "공연장을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    SEAT_ALREADY_TAKEN(HttpStatus.CONFLICT, "이미 선택된 좌석이 있습니다."),
    CONCERT_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "공연 일정을 찾을 수 없습니다."),
    CONCERT_SCHEDULE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 공연 일정입니다."),
    SCHEDULE_SEAT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 회차의 좌석이 등록되어 있습니다."),
    SEAT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 좌석입니다."),
    SEAT_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "수용인원을 초과하여 좌석을 등록할 수 없습니다."),
    SEAT_GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석 등급을 찾을 수 없습니다."),
    RESERVATION_SEAT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "좌석은 최대 4매까지 예약 가능합니다."),
    SCHEDULE_SEAT_MISMATCH(HttpStatus.BAD_REQUEST, "해당 공연 회차의 좌석이 아닙니다."),
    SCHEDULE_SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
    RESERVATION_FORBIDDEN(HttpStatus.FORBIDDEN, "본인 예약만 취소할 수 있습니다."),
    RESERVATION_ALREADY_CANCELLED(HttpStatus.CONFLICT, "이미 취소된 예약입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일 입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    PAYMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 예약만 결제할 수 있습니다."),
    RESERVATION_CANCELLED(HttpStatus.BAD_REQUEST, "취소된 예약은 결제할 수 없습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 올바르지 않습니다."),
    ALREADY_PAID(HttpStatus.CONFLICT, "이미 결제된 예약입니다."),
    SEAT_NOT_HOLDING(HttpStatus.BAD_REQUEST, "결제할 수 없는 좌석이 있습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    CONCERT_SCHEDULE_HAS_RESERVATION(HttpStatus.CONFLICT, "예약이 있는 회차는 삭제할 수 없습니다."),
    ROLE_REQUEST_ALREADY_PENDING(HttpStatus.CONFLICT, "이미 대기중인 권한 신청이 있습니다."),
    ROLE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "권한 신청을 찾을 수 없습니다."),
    ROLE_REQUEST_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 권한 신청입니다."),
    ROLE_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 권한 신청입니다."),
    CONCERT_HAS_SCHEDULE(HttpStatus.CONFLICT, "공연 일정이 있는 콘서트는 삭제할 수 없습니다."),
    CONCERT_SALES_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "판매 시작일은 마감일보다 빨라야 합니다."),
    VENUE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 공연장입니다."),
    INVALID_GRADE_NAME(HttpStatus.BAD_REQUEST, "존재하지 않는 등급명입니다."),
    CONCERT_SCHEDULE_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "공연 시작시간은 종료시간보다 빨라야 합니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    VENUE_HAS_SCHEDULE(HttpStatus.CONFLICT, "공연 일정이 있는 공연장은 삭제할 수 없습니다."),
    VENUE_HAS_SEATS(HttpStatus.CONFLICT, "좌석이 등록된 공연장은 삭제할 수 없습니다."),
    RESERVATION_CANCEL_DEADLINE_PASSED(HttpStatus.BAD_REQUEST, "공연 하루 전부터는 예약을 취소할 수 없습니다."),
    RESERVATION_SALES_NOT_OPEN(HttpStatus.BAD_REQUEST, "티켓 판매 기간이 아닙니다."),
    CONCERT_SUSPENDED(HttpStatus.BAD_REQUEST, "판매가 중단된 콘서트입니다."),
    CONCERT_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "이미 시작된 공연은 예약할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}