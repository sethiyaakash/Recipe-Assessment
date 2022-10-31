package com.recipes.controller;

import com.recipes.pojos.Recipe;
import com.recipes.service.RecipesService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@Slf4j
public class RecipesController {

    @Autowired
    private RecipesService recipesService;

    @PostMapping("/recipe")
    public ResponseEntity<Integer> createRecipe(@Valid @RequestBody Recipe recipe) {
        log.info("Processing create new recipe");
        var recipeId = recipesService.createRecipe(recipe);
        log.info("Recipe successfully saved into DB with recipeId: " + recipeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeId);
    }

    @GetMapping("/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable Integer id) {
        log.info("Processing get recipe ");
        var recipe = recipesService.getRecipe(id);
        log.info("Recipe successfully retrieved from database" + recipe.getId());
        return ResponseEntity.status(HttpStatus.OK).body(recipe);
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getAllRecipes(){
        log.info("Processing get all recipes");
        val recipeList = recipesService.getAllRecipes();
        log.info("All Recipe successfully retrieved from database");
        return ResponseEntity.status(HttpStatus.OK).body(recipeList);
        }

    @PutMapping("/recipe")
    public ResponseEntity<Recipe> modifyRecipe(@Valid @RequestBody Recipe recipe) {
        log.info("Processing modify recipe ");
        var modifiedRecipe = recipesService.modifyExistingRecipe(recipe);
        log.info("Recipe successfully modified in database with recipeId: " + modifiedRecipe.getId());
        return ResponseEntity.status(HttpStatus.OK).body(modifiedRecipe);
        }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Integer id) {
        log.info("Processing delete recipe ");
        recipesService.deleteRecipe(id);
        log.info("Recipe successfully deleted from database ");
        return ResponseEntity.status(HttpStatus.OK).body("Requested recipe deleted from DB");
        }

    @GetMapping("/search/recipe")
    public ResponseEntity<List<Recipe>> searchRecipe(@RequestParam(required = false) String dishType, @RequestParam(required = false) Integer numberOfServing,
                                               @RequestParam(required = false) String ingredient, @RequestParam(required = false, defaultValue = "true") Boolean ingredientIncluded,
                                               @RequestParam(required = false) String instructionSearch) {
        log.info("Processing search recipe with ingredientIncluded as: " + ingredientIncluded);
        var recipeList = recipesService.searchRecipe(dishType, numberOfServing, ingredient, ingredientIncluded, instructionSearch);
        log.info("Recipe List successfully retrieved from database based on search criteria");
        return ResponseEntity.status(HttpStatus.OK).body(recipeList);
    }

}
