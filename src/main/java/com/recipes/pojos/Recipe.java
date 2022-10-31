package com.recipes.pojos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    private Integer id;
    @NotBlank(message = "Recipe Name can not be null or empty")
    private String name;
    @NotBlank(message = "Recipe Type can not be null or empty")
    private String type;
    private Integer servingCapacity;
    private List<Ingredient> ingredientList = new ArrayList<>();
    @NotBlank(message = "Instructions can not be null or empty")
    private String instruction;

}
