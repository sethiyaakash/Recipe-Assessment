package com.recipes.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
    public class ErrorResponse {
        private final int status;
        private final String message;
        private final LocalDateTime dateTime;
}
