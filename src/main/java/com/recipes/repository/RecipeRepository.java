package com.recipes.repository;

import com.recipes.entities.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer> {

    @Query(nativeQuery = true , value = "SELECT DISTINCT R.* FROM " +
            "RECIPE R " +
            "WHERE (:type is null OR R.TYPE = :type) " +
            "and (:servingCapacity is null OR R.SERVING_CAPACITY = :servingCapacity) " +
            "and (:text is null OR R.INSTRUCTION like %:text%)")
    List<RecipeEntity> findAllRecipesBySearchCriteria(String type, Integer servingCapacity,
                                                      String text);
}
