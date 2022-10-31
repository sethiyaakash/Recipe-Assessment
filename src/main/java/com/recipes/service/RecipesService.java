package com.recipes.service;

import com.recipes.entities.RecipeEntity;
import com.recipes.exception.ErrorConstants;
import com.recipes.exception.RecipeNotFoundException;
import com.recipes.pojos.Recipe;
import com.recipes.repository.RecipeRepository;
import com.recipes.utils.RecipeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecipesService {

    private static final RecipeMapper RECIPE_MAPPER = RecipeMapper.INSTANCE;

    @Autowired
    private RecipeRepository recipeRepository;

    @Transactional
    public Integer createRecipe(Recipe recipe) {
        var recipeEntity = RECIPE_MAPPER.recipeToRecipeEntity(recipe);

        recipeEntity.getIngredientEntityList().forEach(ingredientEntity -> ingredientEntity.setRecipeEntity(recipeEntity));

       var savedEntity =  recipeRepository.save(recipeEntity);

        return savedEntity.getId();
    }

    public Recipe getRecipe(Integer id) {
        var optRecipe = recipeRepository.findById(id);
        if(optRecipe.isPresent())
            return RECIPE_MAPPER.recipeEntityToRecipe(optRecipe.get());
        else
            throw new RecipeNotFoundException(ErrorConstants.RECIPE_NOT_FOUND);
    }

    public List<Recipe> getAllRecipes(){
        var retrievedRecipes = recipeRepository.findAll();
        log.debug("Number of retrieved recipes from DB: " + retrievedRecipes.size());
        var recipesList = new ArrayList<Recipe>(retrievedRecipes.size());
        retrievedRecipes.forEach(recipeEntity -> recipesList.add(RECIPE_MAPPER.recipeEntityToRecipe(recipeEntity)));

        return recipesList;
    }

    public Recipe modifyExistingRecipe(Recipe recipe) {
        return saveRecipeToRepository(recipe);
    }

    public Recipe saveRecipeToRepository(Recipe newRecipe) {
        var optRecipe = recipeRepository.findById(newRecipe.getId());
        if(optRecipe.isPresent()) {
            var recipeEntity = RECIPE_MAPPER.recipeToRecipeEntity(newRecipe);
            recipeEntity.getIngredientEntityList().forEach(ingredientEntity -> ingredientEntity.setRecipeEntity(recipeEntity));
            return RECIPE_MAPPER.recipeEntityToRecipe(recipeRepository.save(recipeEntity));
        } else {
            throw new RecipeNotFoundException(ErrorConstants.RECIPE_NOT_FOUND);
        }
    }

    public void deleteRecipe(Integer id) {
        try {
            recipeRepository.deleteById(id);
        } catch(Exception ex) {
            throw new RecipeNotFoundException(ErrorConstants.RECIPE_NOT_FOUND);
        }
    }

    public List<Recipe> searchRecipe( String dishType, Integer numberOfServing,
                              String ingredient, Boolean ingredientIncluded,
                              String instructionSearch) {

        var recipeEntities = recipeRepository.findAllRecipesBySearchCriteria(dishType, numberOfServing,
                instructionSearch);
        List<RecipeEntity> filteredRecipe;
        log.debug("Number of retrieved recipes from DB: " + recipeEntities.size());


        if(ingredientIncluded) {
            filteredRecipe = recipeEntities.stream()
                    .filter(recipeEntity -> recipeEntity.getIngredientEntityList().stream()
                            .anyMatch(ingredientEntity -> ingredient.equalsIgnoreCase(ingredientEntity.getName())))
                    .collect(Collectors.toList());

        } else {
            filteredRecipe = recipeEntities.stream()
                    .filter(recipeEntity -> recipeEntity.getIngredientEntityList().stream()
                            .noneMatch(ingredientEntity -> ingredient.equalsIgnoreCase(ingredientEntity.getName())))
                    .collect(Collectors.toList());
        }

        return filteredRecipe.stream().map(RECIPE_MAPPER::recipeEntityToRecipe).collect(Collectors.toList());
    }

}
