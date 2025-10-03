package com.example.OnlineProctoring.customExceptions;

public class ExamQuestionEmptyException extends RuntimeException {

    public ExamQuestionEmptyException(String message) {
        super(message);
    }
}
