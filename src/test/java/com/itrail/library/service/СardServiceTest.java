package com.itrail.library.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.itrail.library.domain.Book;
import com.itrail.library.domain.Card;
import com.itrail.library.domain.CardRecord;
import com.itrail.library.domain.User;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.repository.CardRecordRepository;
import com.itrail.library.repository.CardRepository;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.CardInfoResponse;
import com.itrail.library.response.CardResponseLazy;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - СardService")
@DisplayName("Тестирование сервиса - СardService")
@ExtendWith(MockitoExtension.class)
public class СardServiceTest {

    @Mock private CardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private CardRecordRepository cardRecordRepository;
    @Mock private BookRepository bookRepository;

    @InjectMocks private СardService сardService;

    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";

    @Test
    @DisplayName( "Сохранение карты для пользователя")
    public void saveCardTest(){
        Long idUser = 1L;
        Allure.parameter( "idUser", idUser);
        Mockito.when( userRepository.findById( idUser )).thenReturn( Optional.of( new User() ) );
        Mockito.when( cardRepository.findByUser( idUser )).thenReturn( Optional.empty() );
        Card result = сardService.saveCard( idUser );
        //Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @Test
    @DisplayName( "Сохранение карты для пользователя - Ошибка - Нет такого пользователя!")
    public void saveCardErrorUserTest(){
        Long idUser = 1L;
        Mockito.when( userRepository.findById( idUser )).thenReturn( Optional.empty() );
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> сardService.saveCard( idUser ));
        Assertions.assertEquals("Нет такого пользователя!", exception.getMessage());
    }

    @Test
    @DisplayName( "Сохранение карты для пользователя - Ошибка - У пользователя уже есть карта!")
    public void saveUserErrorCardTest(){
        Long idUser = 1L;
        Mockito.when( userRepository.findById( idUser )).thenReturn( Optional.of( new User()));
        Mockito.when( cardRepository.findByUser( idUser )).thenReturn( Optional.of( new Card() ));
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> сardService.saveCard( idUser ));
        Assertions.assertEquals("У пользователя есть уже карта!", exception.getMessage());   
    }

    @ParameterizedTest
    @CsvSource({"login, 1, 10"})
    @DisplayName( "Получение информации о пользователе и его карте, с его записями")
    public void getFullInfoCardAndRecordTest(String user, int page, int size ){
        Card card = new Card( 1L, null, null, null, false, new User());
        List<CardRecord> cardRecords = List.of( new CardRecord(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), 1L, 1L),
                                                new CardRecord(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), 3L, 1L));
        Mockito.when( cardRepository.findCardByUser( user )).thenReturn( Optional.of( card ));
        Mockito.when( cardRecordRepository.findRecordsCurrent( card.getId(), PageRequest.of(page - 1, size))).thenReturn(cardRecords);
        Mockito.when( bookRepository.findById( 1L )).thenReturn( Optional.of( new Book() ));
        Mockito.when( bookRepository.findById( 3L )).thenReturn( Optional.of( new Book() ));;
        BaseResponse<CardInfoResponse> result = сardService.getFullInfoCardAndRecord( user, page, size );
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @ParameterizedTest
    @CsvSource({"login, 0, 10"})
    @DisplayName( "Получение информации о пользователе и его карте, с его записями - Ошибка: значение странице должно быть больше нуля")
    public void getFullInfoCardAndRecordErrorPageTest( String user, int page, int size ){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> сardService.getFullInfoCardAndRecord( user, page, size ));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage()); 
    }

    @ParameterizedTest
    @CsvSource({"login, 1, 0"})
    @DisplayName( "Получение информации о пользователе и его карте, с его записями Ошибка: значение размера страницы должно быть больше нуля")
    public void getFullInfoCardAndRecordErrorSizeTest( String user, int page, int size ){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> сardService.getFullInfoCardAndRecord( user, page, size ));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage()); 
    }

    //@ParameterizedTest
    @CsvSource({"login, 1, 10"})
    @DisplayName( "Получение информации о пользователе и его карте, с его записями Ошибка: карта пользователя не найдена")
    public void getFullInfoCardAndRecordErrorCardTest( String user, int page, int size ){
        Mockito.when( cardRepository.findCardByUser( user )).thenReturn( Optional.empty() );
        NoSuchElementException exception = Assertions.assertThrows( NoSuchElementException.class, () -> сardService.getFullInfoCardAndRecord( user, page, size ));
        Assertions.assertEquals("По даному запросу ничего не найдено!", exception.getMessage()); 
    }

    @ParameterizedTest
    @CsvSource({"1, 10"})
    @DisplayName("Ленивая загрузка карт пользователей")
    public void getLazyCardTest( int page, int size ){
        List<Card> card = List.of( new Card( 1L, LocalDateTime.now(), LocalDateTime.now(), null, false, new User()),
                                   new Card(2L, LocalDateTime.now(), LocalDateTime.now(), null, false, new User()));
        Page<Card> pageCard = new PageImpl<>( card );
        Mockito.when( cardRepository.findAll( PageRequest.of( page - 1, size ))).thenReturn( pageCard );
        List<CardResponseLazy> result = сardService.getLazyCard( page, size );
        assertFalse( result.isEmpty() );
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @ParameterizedTest
    @CsvSource({"0, 10"})
    @DisplayName("Ленивая загрузка карт пользователей - Ошибка: неверная нумерация страницы")
    public void getLazyCardErrorPageTest( int page, int size ){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> сardService.getLazyCard( page, size ));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage()); 
    }

    @ParameterizedTest
    @CsvSource({"1, 0"})
    @DisplayName("Ленивая загрузка карт пользователей - Ошибка: неверный размер страницы")
    public void getLazyCardErrorSizeTest( int page, int size ){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> сardService.getLazyCard( page, size ));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage()); 
    }

}
