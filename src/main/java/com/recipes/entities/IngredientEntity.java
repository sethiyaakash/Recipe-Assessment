package com.recipes.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "INGREDIENT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "QUANTITY")
    private String quantity;

    @ManyToOne
    @JoinColumn(name = "RECIPE_ID")
    private RecipeEntity recipeEntity;

}
