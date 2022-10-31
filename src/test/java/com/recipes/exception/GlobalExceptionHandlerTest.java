package com.recipes.exception;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void testHandleRecipeNotFoundException() {
        var ex = new RecipeNotFoundException(ErrorConstants.RECIPE_NOT_FOUND);
        var responseEntity = globalExceptionHandler.handleRecipeNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
        assertTrue(responseEntity.hasBody());
    }

    @Test
    public void testHandleBadRequestException() {
        var ex = new BadRequestException(ErrorConstants.INVALID_REQUEST);
        var responseEntity = globalExceptionHandler.handleBadRequestException(ex);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
        assertTrue(responseEntity.hasBody());
    }

}
