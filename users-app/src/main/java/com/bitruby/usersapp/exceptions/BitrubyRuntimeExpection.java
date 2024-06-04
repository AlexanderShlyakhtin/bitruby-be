package com.bitruby.usersapp.exceptions;


import org.springframework.http.HttpStatus;

public class BitrubyRuntimeExpection extends BaseException {

    public BitrubyRuntimeExpection(String message, Throwable throwable) {
        super(message, throwable);
    }
    public BitrubyRuntimeExpection(String message) {
        super(message);
    }
    @Override
    public HttpStatus httpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
