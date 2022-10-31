package com.recipes.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Recipe")
@Getter
@Setter
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Column (name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "SERVING_CAPACITY")
    private Integer servingCapacity;

    @Column(name = "INSTRUCTION")
    private String instruction;

    @OneToMany(mappedBy = "recipeEntity", cascade = CascadeType.ALL)
    private List<IngredientEntity> ingredientEntityList = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        creationDate = new Date();
    }

}
