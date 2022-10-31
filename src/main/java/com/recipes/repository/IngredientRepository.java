package com.recipes.repository;

import com.recipes.entities.IngredientEntity;
import com.recipes.entities.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Integer> {

}
