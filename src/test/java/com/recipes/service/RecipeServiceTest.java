package com.recipes.service;

import com.recipes.entities.IngredientEntity;
import com.recipes.entities.RecipeEntity;
import com.recipes.exception.RecipeNotFoundException;
import com.recipes.pojos.Ingredient;
import com.recipes.pojos.Recipe;
import com.recipes.repository.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    RecipeRepository recipeRepository;

    @InjectMocks
    private RecipesService recipesService;

    @Test
    public void testCreateRecipe() {
        var recipe = getRecipe();
        Mockito.when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(getRecipeEntity());
        var savedRecipeId =recipesService.createRecipe(recipe);

        assertEquals(1, savedRecipeId.intValue());
    }

    @Test
    public void testGetRecipeWhenPresent() {
        Mockito.when(recipeRepository.findById(any(Integer.class))).thenReturn(Optional.of(getRecipeEntity()));
        var recipe = recipesService.getRecipe(1);

        assertEquals(1, recipe.getId().intValue());
        assertEquals("nonveg", recipe.getType());
        assertEquals(4, recipe.getServingCapacity().intValue());
        assertEquals("Instruction", recipe.getInstruction());
        assertEquals(2, recipe.getIngredientList().size());
        assertEquals("Ingredient 1", recipe.getIngredientList().get(0).getName());
        assertEquals("Ingredient 2", recipe.getIngredientList().get(1).getName());
    }

    @Test
    public void testGetRecipeWhenNotPresent() {
        Mockito.when(recipeRepository.findById(any(Integer.class))).thenThrow(RecipeNotFoundException.class);
        Assertions.assertThrows(RecipeNotFoundException.class, () -> {
            recipesService.getRecipe(1);
        });
    }

    @Test
    public void testGetAllRecipes() {
        Mockito.when(recipeRepository.findAll()).thenReturn(Arrays.asList(getRecipeEntity(), getRecipeEntity()));
        var recipes = recipesService.getAllRecipes();
        assertEquals(2, recipes.size());
        var recipe = recipes.get(0);
        assertEquals(1, recipe.getId().intValue());
        assertEquals("nonveg", recipe.getType());
        assertEquals(4, recipe.getServingCapacity().intValue());
        assertEquals("Instruction", recipe.getInstruction());
        assertEquals(2, recipe.getIngredientList().size());
        assertEquals("Ingredient 1", recipe.getIngredientList().get(0).getName());
        assertEquals("Ingredient 2", recipe.getIngredientList().get(1).getName());
    }

    @Test
    public void testModifyRecipe() {
        var recipe = getRecipe();
        recipe.setId(1);
        Mockito.when(recipeRepository.save(any(RecipeEntity.class))).thenReturn(getRecipeEntity());
        Mockito.when(recipeRepository.findById(any(Integer.class))).thenReturn(Optional.of(getRecipeEntity()));
        var savedRecipe =recipesService.modifyExistingRecipe(recipe);

        assertEquals(1, savedRecipe.getId().intValue());
        assertEquals("nonveg", savedRecipe.getType());
        assertEquals(4, savedRecipe.getServingCapacity().intValue());
        assertEquals("Instruction", savedRecipe.getInstruction());
        assertEquals(2, savedRecipe.getIngredientList().size());
        assertEquals("Ingredient 1", savedRecipe.getIngredientList().get(0).getName());
        assertEquals("Ingredient 2", savedRecipe.getIngredientList().get(1).getName());
    }

    @Test
    public void testModifyRecipeWhenRecipeNotExist() {
        Mockito.when(recipeRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(RecipeNotFoundException.class, () -> {
            recipesService.getRecipe(1);
        });
    }

    @Test
    public void testDeleteRecipe() {
        doNothing().when(recipeRepository).deleteById(any(Integer.class));
        recipesService.deleteRecipe(1);
        verify(recipeRepository, times(1)).deleteById(any(Integer.class));
    }

    @Test
    public void testSearchRecipeWithIngredient() {
        when(recipeRepository.findAllRecipesBySearchCriteria(anyString(), any(Integer.class),
                anyString())).thenReturn(Arrays.asList(getRecipeEntity()));
        var recipes = recipesService.searchRecipe("nonveg", 4, "Ingredient 1", true,"Instruction");

        assertEquals(1, recipes.size());
        var recipe = recipes.get(0);

        assertEquals(1, recipe.getId().intValue());
        assertEquals("nonveg", recipe.getType());
        assertEquals(4, recipe.getServingCapacity().intValue());
        assertEquals("Instruction", recipe.getInstruction());
        assertEquals(2, recipe.getIngredientList().size());
        assertEquals("Ingredient 1", recipe.getIngredientList().get(0).getName());
        assertEquals("Ingredient 2", recipe.getIngredientList().get(1).getName());
    }

    @Test
    public void testSearchRecipeWithoutIngredient() {
        when(recipeRepository.findAllRecipesBySearchCriteria(anyString(), any(Integer.class)
                , anyString())).thenReturn(Arrays.asList(getRecipeEntity()));
        var recipes = recipesService.searchRecipe("nonveg", 4, "Ingredient 1", false,"Instruction");

        assertEquals(0, recipes.size());
    }

    private RecipeEntity getRecipeEntity() {
        var ingredientEntity1 = new IngredientEntity();
        ingredientEntity1.setName("Ingredient 1");
        ingredientEntity1.setId(1);

        var ingredientEntity2 = new IngredientEntity();
        ingredientEntity2.setName("Ingredient 2");
        ingredientEntity2.setId(2);

        var recipeEntity = new RecipeEntity();
        recipeEntity.setId(1);
        recipeEntity.setType("nonveg");
        recipeEntity.setServingCapacity(4);
        recipeEntity.setInstruction("Instruction");
        recipeEntity.setIngredientEntityList(Arrays.asList(ingredientEntity1, ingredientEntity2));

        return recipeEntity;
    }

    private Recipe getRecipe() {
        var ingredient = new Ingredient();
        ingredient.setName("Ingredient 1");

        var recipe = new Recipe();
        recipe.setType("nonveg");
        recipe.setServingCapacity(4);
        recipe.setInstruction("Instruction");
        recipe.setIngredientList(Arrays.asList(ingredient));

        return recipe;
    }
}
