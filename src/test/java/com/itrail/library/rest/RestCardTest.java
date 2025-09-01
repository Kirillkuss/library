package com.itrail.library.rest;

import static io.restassured.RestAssured.given;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import com.itrail.library.request.card.CardFilterRequest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Disabled
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование АПИ - CardController")
@DisplayName("Тестирование АПИ - CardController")
public class RestCardTest {

    public final String PATH     = "http://localhost:8094";
    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";
    public final String error    = "Ошибка: ";
    public final String leadTime = "Время выполнения: ";

    @ParameterizedTest
    @CsvSource({"1,10", "2,5"})
    @DisplayName("Ленивая загрузка карт пользователей")
    public void getLazyCardsTest( int page, int size ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .contentType( ContentType.JSON )
                                       .get("/library/cards/lazy/{page}/{size}", page, size );
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    public static Stream<Arguments> getCardFilterRequest(){
        return Stream.of( Arguments.of( new CardFilterRequest( "User999", 1, 10 )),
                          Arguments.of( new CardFilterRequest("Admin999",  1, 10 )),
                          Arguments.of( new CardFilterRequest("Test1234!", 1, 10 )));
    }

    @Description("Получение инфо о действующих записях пользователя и его карты ")
    @DisplayName("Получение инфо о действующих записях пользователя и его карты ")
    @ParameterizedTest
    @MethodSource("getCardFilterRequest")
    public void getCardInfoByLoginTest( CardFilterRequest cardFilterRequest ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().contentType(ContentType.JSON)
                                        .when()
                                        .body(cardFilterRequest)
                                        .post("/library/cards/card");
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
