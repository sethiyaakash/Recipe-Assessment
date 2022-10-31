package com.recipes.utils;

import com.recipes.entities.RecipeEntity;
import com.recipes.pojos.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RecipeMapper {

    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    @Mapping(source = "recipeEntity.ingredientEntityList", target = "ingredientList")
    Recipe recipeEntityToRecipe(RecipeEntity recipeEntity);

    @Mapping(source = "recipe.ingredientList", target = "ingredientEntityList")
    RecipeEntity recipeToRecipeEntity(Recipe recipe);

}
