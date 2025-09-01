package com.itrail.library.rest;

import static io.restassured.RestAssured.given;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import com.itrail.library.request.record.CardRecordRequest;
import com.itrail.library.request.record.CreateCardRecordRequest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Disabled
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование АПИ - RecordController")
@DisplayName("Тестирование АПИ - RecordController")
public class RestRecordTest {

    public final String PATH     = "http://localhost:8094";
    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";
    public final String error    = "Ошибка: ";
    public final String leadTime = "Время выполнения: ";


    @ParameterizedTest
    @CsvSource({"1,4", "2,5"})
    @DisplayName("Ленивая загрузка всех записей")
    public void getLazyRecordsTest( int page, int size ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().queryParam("page", page)
                                       .queryParam("size", size)
                                       .contentType( ContentType.JSON )
                                       .get("/library/records/lazy/{page}/{size}", page, size );
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    public static Stream<Arguments> getCardRecordRequest(){
        return Stream.of( Arguments.of( new CardRecordRequest( "Admin123", LocalDateTime.now().minusMonths(23), LocalDateTime.now(), 1, 10 )),
                          Arguments.of( new CardRecordRequest("User123",LocalDateTime.now().minusMonths(54), LocalDateTime.now(),  1, 10 )),
                          Arguments.of( new CardRecordRequest("Test1234!",LocalDateTime.now().minusMonths(12), LocalDateTime.now(), 1, 10 )));
    }

    @Description("Получение списка записей по промежуток времени для пользователя ")
    @DisplayName("Получение списка записей по промежуток времени для пользователя ")
    @ParameterizedTest
    @MethodSource("getCardRecordRequest")
    public void getRecordsByUserTest( CardRecordRequest cardRecordRequest ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().contentType(ContentType.JSON)
                                        .when()
                                        .body(cardRecordRequest)
                                        .post("/library/records/users");
                     response.then()
                             .log().all()
                             .statusCode(200);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }

    public static Stream<Arguments> getCreateCardRecordRequest(){
        return Stream.of( Arguments.of( new CreateCardRecordRequest( 23235567L, 12L )));
    }

    @Description("Выдача книги пользователю")
    @DisplayName("Выдача книги пользователю")
    @ParameterizedTest
    @MethodSource("getCreateCardRecordRequest")
    public void createRecordTest( CreateCardRecordRequest createCardRecordRequest ){
        try{
            RestAssured.baseURI = PATH;
            Response response = given().contentType(ContentType.JSON)
                                        .when()
                                        .body(createCardRecordRequest)
                                        .post("/library/records/create");
                     response.then()
                             .log().all()
                             .statusCode(201);
            Allure.addAttachment( rezult, TYPE, response.andReturn().asString() );
            Allure.addAttachment( leadTime, TYPE, String.valueOf( response.time() + " ms."));
        }catch( Exception ex ){
            Allure.addAttachment( error, TYPE, ex.getMessage() );
        }
    }
    
    
}
