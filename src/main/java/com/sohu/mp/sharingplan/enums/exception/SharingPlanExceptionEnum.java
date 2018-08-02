package com.sohu.mp.sharingplan.enums.exception;

import com.sohu.mp.common.interfaces.ResponseCodeMsg;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author lvjinwang
 */

public enum  SharingPlanExceptionEnum implements ResponseCodeMsg {

    /**
     * 无权限
     * 资产校验错误
     * 体现状态错误
     */
    MULCT_NO_AUTH(20001, "operator no auth", UNAUTHORIZED),
    AMOUNT_CHECK_ERROR(20001, "amount check error", BAD_REQUEST),
    WITHDRAW_STATUS_ERROR(20001,"withdraw status check error",BAD_REQUEST);


    private int code;
    private String message;
    private HttpStatus httpStatus;

    SharingPlanExceptionEnum(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
