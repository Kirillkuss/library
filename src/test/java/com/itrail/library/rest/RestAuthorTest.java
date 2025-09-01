package com.itrail.library.rest;

import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Disabled
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование АПИ - AuthorController")
@DisplayName("Тестирование АПИ - AuthorController")
public class RestAuthorTest {

    public final String PATH     = "http://localhost:8094";
    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";
    public final String error    = "Ошибка: ";
    public final String leadTime = "Время выполнения: ";

    @ParameterizedTest
    @CsvSource({"Gerald", "Alan"})
    @DisplayName("Поиск автора по фио")
    public void getAuthorByFioTest( String fio ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().queryParam("fio", fio)
                                       .contentType( ContentType.JSON )
                                       .get("/library/authors/{fio}", fio);
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    @ParameterizedTest
    @CsvSource({"1,4", "2,5"})
    @DisplayName("Ленивая загрузка для авторов")
    public void getLazyAuthorsTest( int page, int size ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .contentType( ContentType.JSON )
                                       .get("/library/authors/lazy/{page}/{size}", page, size );
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }
    
}
