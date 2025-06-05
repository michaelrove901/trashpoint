package com.trashpoint.backend.exception;

import lombok.Getter;

@Getter
public class CustomErrorResponse extends RuntimeException {
    private final int code;

    public CustomErrorResponse(String message, int code) {
        super(message);
        this.code = code;
    }
}
