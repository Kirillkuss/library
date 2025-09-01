package com.itrail.library.rest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import com.itrail.library.request.CreateUserRequest;
import static io.restassured.RestAssured.given;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Disabled
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование АПИ - UserController")
@DisplayName("Тестирование АПИ - UserController")
public class RestUserTest {

    public final String PATH     = "http://localhost:8094";
    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";
    public final String error    = "Ошибка: ";
    public final String leadTime = "Время выполнения: ";


    @Description("Получение списка пользователей (GET)")
    @DisplayName("Получение списка пользователей (GET)")
    @ParameterizedTest
    @CsvSource({"1, 5", "3, 10"})
    public void getLazyUsersTest( int page, int size ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .when()
                                       .contentType( ContentType.JSON )
                                       .get("/library/users/lazy/{page}/{size}", page, size);
                     response.then().statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    @Test
    @Description("Получение количества пользователей (GET)")
    @DisplayName("Получение количества пользователей (GET)")
    public void getCountUserTest(){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().when()
                                       .contentType( ContentType.JSON )
                                       .get( "/library/users/counts");
                     response.then().statusCode( 200 ).log().all();
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    @Description("Получение списка пользователей по параметрам (GET)")
    @DisplayName("Получение списка пользователей по параметрам (GET)")
    @ParameterizedTest
    @CsvSource({"Adm, 1, 5", "User, 1, 10"})
    public void getLazyTest( String param, int page, int size ){
        try{
            RestAssured.baseURI = PATH; 
            Response response = given().queryParam("page", param)
                                       .queryParam("page", page)
                                       .queryParam("size", size)
                                       .contentType(ContentType.JSON)
                                       //.when().log().all()
                                       .when().log().all()
                                       .get("/library/users/{param}/{page}/{size}", param, page, size);
            
            //response.then().statusCode(200).log().all();
            response.then().statusCode(200);             
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }


    @Test
    @Description("Получение списка сессий в Redis(GET)")
    @DisplayName("Получение списка сессий в Redi (GET)")
    public void getSessionsTest(){
        try{
            RestAssured.baseURI = PATH; 
            Response response = given().contentType(ContentType.JSON)
                                       //.when().log().all()
                                       .when()
                                       .get("/library/users/sessions");
            //response.then().statusCode(200).log().all();
            response.then().statusCode(200);         
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    public static Stream<Arguments> getCreateUserRequest(){
        return Stream.of( Arguments.of( new CreateUserRequest("Login12648@j45", 
                                                            "Logi^#12648@j4535!", 
                                                            "Loginb", 
                                                            "Loginb", 
                                                            "Loginb", 
                                                                "Loginb4546723@gmail.com", 
                                                                "+375225463456", 
                                                                    new HashSet<>( Arrays.asList( "ADMIN"))) ));
    }



    @Description("Создание нового пользователя")
    @DisplayName("Создание нового пользователя")
    @ParameterizedTest
    @MethodSource("getCreateUserRequest")
    public void testCreateUserTest( CreateUserRequest createUserRequest ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().log()
                                       .all()
                                       .when()
                                       .contentType(ContentType.JSON)
                                       .body( createUserRequest )
                                       .post("/library/users/create");
                     response.then()
                             .log()
                             .body()
                             .statusCode( 201);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }



    
}
