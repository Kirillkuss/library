package com.itrail.library.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование АПИ - AuthorController")
@DisplayName("Тестирование АПИ - AuthorController")
public class RestAuthorTest {

    public final String TYPE     = "application/json";
    public final String rezult   = "Response body: ";
    public final String leadTime = "Response time: ";
    public final String error    = "Ошибка: ";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8094";
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON)
                                                                   .setAccept(ContentType.JSON)
                                                                   .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().expectContentType(ContentType.JSON)
                                                                     .expectResponseTime(lessThan(2_000L))
                                                                     .build();
    }

    @ParameterizedTest
    @CsvSource({"Gerald", "Alan"})
    @DisplayName("Поиск автора по фио")
    public void getAuthorByFioTest( String fio ){
        try{
            //RestAssured.baseURI = PATH;
            /**Response response = given().queryParam("fio", fio)
                                       .contentType( ContentType.JSON )
                                       .get("/library/authors/{fio}", fio);
                     response.then()
                             .log().all()
                             .statusCode(200);*/
            Response response = given().pathParam("fio", fio)
                                       .when()
                                       .get("/library/authors/{fio}")
                                       .then()
                                       .log().ifValidationFails()
                                       .statusCode(200)
                                       .body("fio", equalTo(fio))
                                       .body(matchesJsonSchemaInClasspath("schemas/author.json"))
                                       .extract().response();

            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, "text/plain", String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    @ParameterizedTest
    @CsvSource({"1,10", "2,14"})
    @DisplayName("Ленивая загрузка для авторов")
    public void getLazyAuthorsTest( int page, int size ){
        try{
           // RestAssured.baseURI = PATH;
            /**Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .contentType( ContentType.JSON )
                                       .get("/library/authors/lazy/{page}/{size}", page, size );
                     response.then()
                             .log().all()
                             .statusCode(200);*/
            Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .when()
                                       .get("/library/authors/lazy/{page}/{size}")
                                       .then()
                                       .log().ifValidationFails()
                                       .statusCode(200)
                                       .body("page", equalTo(page))
                                       .body("size", equalTo(size))
                                       .body(matchesJsonSchemaInClasspath("schemas/author.json"))
                                       .extract().response();

            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }
    
}
