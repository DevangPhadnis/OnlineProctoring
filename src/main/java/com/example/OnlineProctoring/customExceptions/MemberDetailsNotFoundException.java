package com.example.OnlineProctoring.customExceptions;

public class MemberDetailsNotFoundException extends RuntimeException {

    public MemberDetailsNotFoundException(String message) {
        super(message);
    }
}
