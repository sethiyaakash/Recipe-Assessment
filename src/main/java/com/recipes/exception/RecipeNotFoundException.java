package com.recipes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RecipeNotFoundException extends ResponseStatusException {

    public RecipeNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
