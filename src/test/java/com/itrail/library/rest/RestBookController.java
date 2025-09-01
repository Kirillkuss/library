package com.itrail.library.rest;

import static io.restassured.RestAssured.given;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import com.itrail.library.request.book.BookFilterRequest;
import com.itrail.library.request.book.FreeBooksRequest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Disabled
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование АПИ - BookController")
@DisplayName("Тестирование АПИ - BookController")
public class RestBookController {

    public final String PATH     = "http://localhost:8094";
    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";
    public final String error    = "Ошибка: ";
    public final String leadTime = "Время выполнения: ";

    @ParameterizedTest
    @CsvSource({"1,10", "2,11"})
    @DisplayName("Ленивая загрузка для книг")
    public void getLazyBooksTest( int page, int size ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .contentType( ContentType.JSON )
                                       .get("/library/books/lazy/{page}/{size}", page, size );
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    public static Stream<Arguments> getFreeBooksRequest(){
        return Stream.of( Arguments.of( new FreeBooksRequest(1, "Second4", "Gerald", 4534524, 1, 10 )),
                          Arguments.of( new FreeBooksRequest(2, "Second4", "Gerald", 4534524, 1, 10 )),
                          Arguments.of( new FreeBooksRequest(3, "Second4", "Gerald", 4534524, 1, 10 )));
    }

    @Description("Получение информации о свободных книгах")
    @DisplayName("Получение информации о свободных книгах")
    @ParameterizedTest
    @MethodSource("getFreeBooksRequest")
    public void getBooksFreeTest( FreeBooksRequest freeBooksRequest ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().contentType(ContentType.JSON)
                                        .queryParam("code", freeBooksRequest.code())
                                        .queryParam("nameBook", freeBooksRequest.nameBook())
                                        .queryParam("author", freeBooksRequest.author())
                                        .queryParam("number", freeBooksRequest.number())
                                        .queryParam("page", freeBooksRequest.page())
                                        .queryParam("size", freeBooksRequest.size())
                                        .when()
                                        .get("/library/books/free");
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    public static Stream<Arguments> getBookFilterRequest(){
        return Stream.of( Arguments.of( new BookFilterRequest( "Anderson", 1, 10 )),
                          Arguments.of( new BookFilterRequest("Gerald",  1, 10 )),
                          Arguments.of( new BookFilterRequest("Tor", 1, 10 )));
    }

    @Description("Получение списка книг по автору")
    @DisplayName("Получение списка книг по автору")
    @ParameterizedTest
    @MethodSource("getBookFilterRequest")
    public void getCurrentBook( BookFilterRequest bookFilterRequest ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().contentType(ContentType.JSON)
                                        .queryParam("fio", bookFilterRequest.fio())
                                        .queryParam("page", bookFilterRequest.page())
                                        .queryParam("size", bookFilterRequest.size())
                                        .when()
                                        .get("/library/books/current");
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
