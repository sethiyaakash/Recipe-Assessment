package integrationtest.com.ing;

import com.recipes.RecipesApplication;
import com.recipes.pojos.Ingredient;
import com.recipes.pojos.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = RecipesApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RecipeControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    private String buildApiPath(String endPoint) {
        return  "http://localhost:"+ port + endPoint;
    }



    @Test
    public void testCreateAndGetAndModifyRecipeEndPoint() {
        var newRecipe =buildRecipe(1, "Cake", "eg", 5);
        var createdResponseEntity = createRecipe(newRecipe);
        assertEquals(HttpStatus.CREATED, createdResponseEntity.getStatusCode());
        assertNotNull(createdResponseEntity.getBody());
        var recipeId = createdResponseEntity.getBody();

        var retrievedRecipeResponseEntity = testGetRecipe(recipeId);
        assertEquals(HttpStatus.OK, retrievedRecipeResponseEntity.getStatusCode());

        //Validate the retrieved recipe instance fields are same as saved recipe fields
        var retrievedRecipe = retrievedRecipeResponseEntity.getBody();
        assertNotNull(retrievedRecipe);
        assertEquals(recipeId, retrievedRecipe.getId());
        assertEquals(newRecipe.getName(),retrievedRecipe.getName());
        assertEquals(newRecipe.getIngredientList().size(), retrievedRecipe.getIngredientList().size());

        var modifyRecipe = buildModifiedRecipe(retrievedRecipe);
        var modifiedRecipe = modifyRecipe(modifyRecipe);
        assertNotNull(modifiedRecipe);
        assertEquals(HttpStatus.OK, modifiedRecipe.getStatusCode());
        assertNotNull(modifiedRecipe.getBody());
        var recipeIdInResponse = modifiedRecipe.getBody().getId();

        var retrievedModRecipeResponseEntity = testGetRecipe(recipeId);
        var retrievedModifiedRecipe = retrievedModRecipeResponseEntity.getBody();
        //Validate the retrieved recipe instance fields are same as saved recipe fields
        assertEquals(recipeIdInResponse, retrievedModifiedRecipe.getId());
        assertEquals(modifyRecipe.getName(),retrievedModifiedRecipe.getName());
        assertEquals(modifyRecipe.getIngredientList().size(), retrievedModifiedRecipe.getIngredientList().size());

    }

    @Test
    public void testDeleteExistingRecipeEndPoint() {
        var newRecipe =buildRecipe(1, "Cake", "eg", 5);
        var createdResponseEntity = createRecipe(newRecipe);

        var deleteResponseEntity = deleteRecipe(createdResponseEntity.getBody());

        //Validate modify Recipe Call
        assertEquals(HttpStatus.OK, deleteResponseEntity.getStatusCode());
        assertEquals("Requested recipe deleted from DB", deleteResponseEntity.getBody());

        var recipeResponseEntity = testGetRecipe(createdResponseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, recipeResponseEntity.getStatusCode());
    }

    @Test
    public void testSearchRecipe() {
        var createdResponseEntity = createRecipe(buildRecipe(1, "Cake", "veg", 5));
        var recipesResponseEntity = searchRecipe("veg", 5, "Ingredient1" , true, null);

        assertEquals(HttpStatus.OK, recipesResponseEntity.getStatusCode());
        assertNotNull(recipesResponseEntity.getBody());
        assertEquals(1, recipesResponseEntity.getBody().size());
        deleteRecipe(createdResponseEntity.getBody());
    }

    @Test
    public void testSearchRecipeWhenNotFound() {
        var createdResponseEntity = createRecipe(buildRecipe(1, "Cake", "veg", 5));
        var recipesResponseEntity = searchRecipe("noneg", 5, "Ingredient1" , false, null);

        assertEquals(HttpStatus.OK, recipesResponseEntity.getStatusCode());
        assertNotNull(recipesResponseEntity.getBody());
        assertEquals(0, recipesResponseEntity.getBody().size());
        deleteRecipe(createdResponseEntity.getBody());
    }

    @Test
    public void testGetNonExistingRecipe() {
        var recipeResponseEntity = testGetRecipe(5);
        assertEquals(HttpStatus.NOT_FOUND, recipeResponseEntity.getStatusCode());
    }

    @Test
    public void testGetAllRecipesWhenExist() {
        var createdResponseEntity = createRecipe(buildRecipe(1, "Cake", "eg", 5));
        var recipesResponseEntity = testAllRecipe();

        assertEquals(HttpStatus.OK, recipesResponseEntity.getStatusCode());
        assertNotNull(recipesResponseEntity.getBody());
        assertEquals(1, recipesResponseEntity.getBody().size());
        deleteRecipe(createdResponseEntity.getBody());
    }

    @Test
    public void testGetAllRecipesWhenNoRecipeExist() {
        var recipesResponseEntity = testAllRecipe();

        assertEquals(HttpStatus.OK, recipesResponseEntity.getStatusCode());
        assertNotNull(recipesResponseEntity.getBody());
        assertEquals(0, recipesResponseEntity.getBody().size());
    }

    public ResponseEntity<Integer> createRecipe(Recipe recipe) {
        String apiPath = buildApiPath("/api/recipe");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Recipe> request = new HttpEntity<>(recipe, headers);

        return restTemplate.postForEntity(apiPath, request, Integer.class);
    }

    private ResponseEntity<Recipe> testGetRecipe(Integer recipeId) {
        String apiPath = buildApiPath("/api/recipe/" + recipeId);
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Recipe> request = new HttpEntity<>(new Recipe(),headers);

        return restTemplate.exchange(apiPath, HttpMethod.GET, request, Recipe.class);
    }

    private ResponseEntity<List<Recipe>> testAllRecipe() {
        String apiPath = buildApiPath("/api/recipes/");
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Recipe> request = new HttpEntity<>(headers);

        return restTemplate.exchange(apiPath, HttpMethod.GET, request, new ParameterizedTypeReference<List<Recipe>>() {}, new HashMap<>());
    }

    public ResponseEntity<Recipe> modifyRecipe(Recipe modifiedRecipe) {
        String apiPath = buildApiPath("/api/recipe");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Recipe> request = new HttpEntity<>(modifiedRecipe, headers);

        return restTemplate.exchange(apiPath, HttpMethod.PUT, request, Recipe.class);
    }

    public ResponseEntity<String> deleteRecipe(Integer recipeId) {
        String apiPath = buildApiPath("/api/recipe/" + recipeId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Recipe> request = new HttpEntity<>(headers);

        return restTemplate.exchange(apiPath, HttpMethod.DELETE, request, String.class);
    }

    public ResponseEntity<List<Recipe>> searchRecipe(String type, Integer serving, String ingredient, Boolean included, String instruction) {
        String apiPath = buildApiPath("/api/search/recipe/");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(apiPath)
                .queryParam("dishType", type)
                .queryParam("numberOfServing", serving.toString())
                .queryParam("ingredient", ingredient)
                .queryParam("ingredientIncluded", included.toString())
                .queryParam("instructionSearch", instruction).build();

        HttpEntity<Recipe> request = new HttpEntity<>(headers);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, new ParameterizedTypeReference<List<Recipe>>() {});
    }

    public static List<Ingredient> buildIngredients(){
        List<Ingredient> ingredientsList = new ArrayList<>();

        Ingredient ing = new Ingredient(1, "Ingredient1","1 tbsp");ingredientsList.add(ing);
        ing = new Ingredient(2, "Ingredient2","1 nos");ingredientsList.add(ing);
        ing = new Ingredient(3, "Ingredient3","1 kgs");ingredientsList.add(ing);
        ing = new Ingredient(4, "Ingredient4","1 ml");ingredientsList.add(ing);
        return ingredientsList;
    }

    public static String buildInstructions() {
        return  "1. Step-A: \n2. Step-B: \n3. Step-C: \n 4. Step-D: \n";
    }

    public static Recipe buildRecipe(Integer id,String name,String type,Integer capacity) {
        Recipe newRecipe = new Recipe();
        newRecipe.setId(id);
        newRecipe.setName(name);
        newRecipe.setType(type);
        newRecipe.setServingCapacity(capacity);
        newRecipe.setIngredientList(buildIngredients());
        newRecipe.setInstruction(buildInstructions());
        return newRecipe;
    }

    public static Recipe buildModifiedRecipe(Recipe recipe) {
        Recipe newRecipe = new Recipe();
        newRecipe.setId(recipe.getId());
        newRecipe.setName(recipe.getName());
        newRecipe.setType("nonveg");
        newRecipe.setServingCapacity(recipe.getServingCapacity());
        newRecipe.setIngredientList(recipe.getIngredientList());
        newRecipe.setInstruction(buildInstructions());
        return newRecipe;
    }
}
