package com.iremayvaz.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ChapterAlreadyPendingException extends RuntimeException {
    public ChapterAlreadyPendingException() {
        super("Chapter is already pending review");
    }
}
