# Recipe Web Service
Recipe Web Service contains ReST APIs in order to Create, Get, Update, Search and Delete recipe from the database and render the requested details as JSON response to consumer.

### Prerequisites
* JDK 11
* Apache Maven 3.8.*
* Oracle 19
* Git

### Software Used

RESTful API | Spring Boot Web<br/>
Object Relationship Mapping | Spring Data JPA<br/>
Exception Handling | RestControllerAdvice<br/>
Logging | SLF4J<br/>
Unit Tests | Junit 5<br/>
Integration Test | Spring Boot Test<br/>
Liquibase | Database Table Creation<br/>
Swagger | Spring Boot Open API


###  Design
Recipe Web Service is microservice based layered architectured RESTful Web Service.
- API Layer  - Controller layer, which is main interface available for integration and interaction end user to consume APIs

- Service Layer
    - Service layer is responsible for handling all the business logic and mapping between the database Entities and Response Objects. 
    - This layer is responsible for calling the Data Access layer and transform the data and send it to APi layer.
    
- Data Access Layer
    - Responsible to provide Object Relationship Mapping (ORM) between higher level recipe Java objects and persistence layer tables
   
- Persistence Layer
    - This layer is responsible for storing the recipes data onto database tables
    - Two physical tables - `recipes` and `ingredient` are used to store the recipes data for the service
    - Oracle is configured to be used as database service
    - For testing purposes, the Embedded H2 Database provided by Spring Boot framework is also utilized 

### Steps to build Web Service
* Download code zip / `git clone https://github.com/Akash-Sethiya/Recipe-Assessment`
* Move to `Recipe-Assessment` and run maven build command `mvn clean install`
* To build by skipping unit tests run maven command `mvn clean install -DskipTests`
* On build completion, one should have web service jar in `target` directory named as `recipe-assessment-1.0.0.jar`


### How to execute Web Service
* **Execution on Test profile with Embedded H2 Database**
    - In Development Mode, by default web service uses Embedded H2 database for persisting and retrieving recipes details.
    - Command to execute:
   ```
        java -jar target/recipe-assessment-1.0.0.jar --spring.profiles.active=test --logging.level.root=INFO
   ```
    - On successfull start, one should notice log message on console `Tomcat started on port(s): 8080 (http)` and have web service listening for web requests at port 8080
  

* **Execution on Higher Environment with Oracle Database**
    - Specify required Oracle configuration parameters in `application-oracle.properties` file as given below:
        -  spring.datasource.url=${oracle_database_connection_url}
        -  spring.datasource.username=
        -  spring.datasource.password=
        -  spring.datasource.driver-class-name=
    - - Command to execute with `oracle` profile:
  ```
  java -jar target/recipe-assessment-1.0.0.jar --spring.profiles.active=oracle --logging.level.root=INFO
  ```
    - On successful start, one should notice log message on console `Tomcat started on port(s): 8080 (http)` and have web service listening for web requests at port 8080
  

* **Liquibase for database table creation**
    - Liquibase is used to create and modify database tables
    - Below properties are set to use liquibase: 
      - spring.liquibase.enabled=true
      - spring.liquibase.change-log=classpath:/db/changelog-master.xml
      - logging.level.liquibase=INFO
    

* **Swagger is used for API documentation**
    - API documentation can be accessed using http://localhost:8080//swagger-ui-custom.html for development environment
    -  - API documentation can be accessed using http://{domain_name:port}}//swagger-ui-custom.html for higher environment

### Web Service ReST API End Points
Below table lists and describes on the implemented ReST APIs:<br/>
**Note: With all given below api end points request, make sure to include header `Content-Type as application/json`**<br/>
API End Point | Method | Purpose <br/>
------------ | ------------- | ------------- | ------------ | -------------<br/>
/api/recipe | POST | Create a new recipe <br/>
/api/recipe/{id} | GET | Get an existing recipe <br/>
/api/recipes | GET | Gel all existing recipes as list <br/>
/api/recipe | PUT | Update an existing recipe <br/>
/api/recipe/{id} | DELETE | Delete an existing recipe <br/>
/api/search/recipe/ | GET | Search an existing recipe <br/>

### Web Service ReST End Points Usage and Sample Response
- **Recipe Model**
    - JSON Schema
      ```
      {
            "id": "recipeId as integer value",
            "name": "recipeName as string",
            "type": "recipeType - ng/vg/eg as string",
            "servingCapacity": "number of people the dish to be served as integer value",
            "ingredients": "list of ingredients objects with name and quantity as fields or null",
            "instructtions": "step by step procedure to prepare recipe as text or null"
      }
      ```
    - JSON Example
      ```
      {
            "name": "Tea",
            "type": "Hot Drink",
            "servingCapacity": 1,
            "ingredients": [{
                                "name": "Milk",
                                "quantity": "100 ml"
                            }, {
                                "name": "Tea Powder",
                                "quantity": "5 gms"
                            }, {
                                "name": "Sugar",
                                "quantity": "10 gms"
                            }],
             "instructtions": "1.Boil the milk \n 2.Add Sugar and Tea Powder with sugar and milk"
        }
      ```

    - GET request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/102` end point :
  ```
  	{
            "id": 101,
            "name": "Tea",
            "type": "Hot Drink",
            "servingCapacity": 1,
            "ingredients": [{
                                "name": "Milk",
                                "quantity": "100 ml"
                            }, {
                                "name": "Tea Powder",
                                "quantity": "5 gms"
                            }, {
                                "name": "Sugar",
                                "quantity": "10 gms"
                            }],
             "instructtions": "1.Boil the milk \n 2.Add Sugar and Tea Powder with sugar and milk"
        }
  ```
    - GET request to `/api/recipes` end point :
  ```
  	[
    	{
            "id":101
            "name": "Tea",
            "type": "Hot Drink",
            "servingCapacity": 1,
            "ingredients": [{
                                "name": "Milk",
                                "quantity": "100 ml"
                            }, {
                                "name": "Tea Powder",
                                "quantity": "5 gms"
                            }, {
                                "name": "Sugar",
                                "quantity": "10 gms"
                            }],
             "instructtions": "1.Boil the milk \n 2.Add Sugar and Tea Powder with sugar and milk"
         },
    	{
        		"id": 102,
        		"name": "Shake",
        		"type": "Cold",
        		"servingCapacity": 4,
        		"ingredientsList": [],
        		"instructions": "Step by Step procedure to prepare Shake"
    		}
	]
  ```
    - PUT request to `/api/recipe` end point with updated model with recipe id 101 and name renamed to `Hot Tea` and servingCapacity to 2 :
  ```
  		{
            "id": 101,
            "name": "Hot Tea",
            "type": "Hot Drink",
            "servingCapacity": 2,
            "ingredients": [{
                                "name": "Milk",
                                "quantity": "100 ml"
                            }, {
                                "name": "Tea Powder",
                                "quantity": "5 gms"
                            }, {
                                "name": "Sugar",
                                "quantity": "10 gms"
                            }],
             "instructtions": "1.Boil the milk \n 2.Add Sugar and Tea Powder with sugar and milk"
        }
  ```
    - GET request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/102`, one should notice updated fields in previous request reflected, i.e. recipe name and serving capacity:
  ```
  		{
            "id": 101,
            "name": "Hot Tea",
            "type": "Hot Drink",
            "servingCapacity": 2,
            "ingredients": [{
                                "name": "Milk",
                                "quantity": "100 ml"
                            }, {
                                "name": "Tea Powder",
                                "quantity": "5 gms"
                            }, {
                                "name": "Sugar",
                                "quantity": "10 gms"
                            }],
             "instructtions": "1.Boil the milk \n 2.Add Sugar and Tea Powder with sugar and milk"
        }
  ```
    - DELETE request to `/api/recipe/{id}` end point with path parameter as recipe id: `/api/recipe/101`, one should notice response message and status code as 200 OK :
  ```
  		Requested recipe deleted from DB
  ```

- GET request to `/api/search/recipe` end point with query parameters as: `/api/search/recipe?dishType=HotDrink&numberOfServing=2&ingredient=milk&ingredientIncluded=true&instructionSearch=Boil`, one should notice response message and status code as 200 OK :
  ```
  		[
    	{
            "id":101
            "name": "Tea",
            "type": "Hot Drink",
            "servingCapacity": 1,
            "ingredients": [{
                                "name": "Milk",
                                "quantity": "100 ml"
                            }, {
                                "name": "Tea Powder",
                                "quantity": "5 gms"
                            }, {
                                "name": "Sugar",
                                "quantity": "10 gms"
                            }],
             "instructtions": "1.Boil the milk \n 2.Add Sugar and Tea Powder with sugar and milk"
         }
	]
  ```