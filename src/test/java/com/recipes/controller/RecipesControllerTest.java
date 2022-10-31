package com.recipes.controller;

import com.recipes.pojos.Ingredient;
import com.recipes.pojos.Recipe;
import com.recipes.service.RecipesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipesControllerTest {

    @Mock
    RecipesService recipesService;

    @InjectMocks
    RecipesController recipesController;

    @Test
    public void testCreateRecipe() {
        var recipe = getRecipe();
        Mockito.when(recipesService.createRecipe(any(Recipe.class))).thenReturn(1);
        var responseEntity =recipesController.createRecipe(recipe);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().intValue());
    }

    @Test
    public void testGetRecipe() {
        Mockito.when(recipesService.getRecipe(any(Integer.class))).thenReturn(getRecipe());
        var responseEntity = recipesController.getRecipe(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var recipe = responseEntity.getBody();
        verifyResults(recipe);
    }

    @Test
    public void testGetAllRecipes() {
        Mockito.when(recipesService.getAllRecipes()).thenReturn(Arrays.asList(getRecipe(), getRecipe()));
        var responseEntity = recipesController.getAllRecipes();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        var recipes = responseEntity.getBody();
        assertEquals(2, recipes.size());

        var recipe = recipes.get(0);

        verifyResults(recipe);
    }

    @Test
    public void testModifyRecipe() {
        Mockito.when(recipesService.modifyExistingRecipe(any(Recipe.class))).thenReturn(getRecipe());
        var recipeResponseEntity = recipesController.modifyRecipe(getRecipe());

        assertEquals(HttpStatus.OK, recipeResponseEntity.getStatusCode());
        var recipe = recipeResponseEntity.getBody();
        verifyResults(recipe);
    }

    @Test
    public void testDeleteRecipe() {
        doNothing().when(recipesService).deleteRecipe(any(Integer.class));
        var responseEntity = recipesController.deleteRecipe(1);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(recipesService, times(1)).deleteRecipe(any(Integer.class));
    }

    @Test
    public void testSearchRecipe() {
        Mockito.when(recipesService.searchRecipe(anyString(), anyInt(), anyString(), anyBoolean(), anyString())).thenReturn(Arrays.asList(getRecipe()));
        var responseEntity = recipesController.searchRecipe("nonveg", 4, "Ingredient1", true, "Instruction 1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().size());
        var recipe = responseEntity.getBody().get(0);
        verifyResults(recipe);
    }

    private Recipe getRecipe() {
        var ingredient = new Ingredient();
        ingredient.setName("Ingredient 1");

        var recipe = new Recipe();
        recipe.setId(1);
        recipe.setName("Bread");
        recipe.setType("nonveg");
        recipe.setServingCapacity(4);
        recipe.setInstruction("Instruction");
        recipe.setIngredientList(Arrays.asList(ingredient));

        return recipe;
    }

    private void verifyResults(Recipe recipe) {
        assertEquals(1, recipe.getId().intValue());
        assertEquals("Bread", recipe.getName());
        assertEquals("nonveg", recipe.getType());
        assertEquals(4, recipe.getServingCapacity().intValue());
        assertEquals("Instruction", recipe.getInstruction());
        assertEquals(1, recipe.getIngredientList().size());
        assertEquals("Ingredient 1", recipe.getIngredientList().get(0).getName());
    }

}
