package com.itrail.library.service;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
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
import com.itrail.library.request.record.CardRecordRequest;
import com.itrail.library.request.record.CreateCardRecordRequest;
import com.itrail.library.response.CardRecordResponse;
import com.itrail.library.response.RecordResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - CardRecordService")
@DisplayName("Тестирование сервиса - CardRecordService")
@ExtendWith(MockitoExtension.class)
public class CardRecordServiceTest {

    @Mock private CardRecordRepository cardRecordRepository;
    @Mock private BookRepository       bookRepository;
    @Mock private CardRepository       cardRepository;

    @InjectMocks private CardRecordService cardRecordService;

    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";

    @ParameterizedTest
    @CsvSource({"1,2"})
    @DisplayName("Ленивая загрузка записей")
    public void getAllRecordTest( int page, int size ){
        List<CardRecord> cardRecords = List.of( new CardRecord( 1L, LocalDateTime.now(), null, 1L, 1L),
                                                new CardRecord(2L, LocalDateTime.now(), null,  2L, 2L )); 
        Page<CardRecord> cardRecordPage = new PageImpl<>( cardRecords );
        Card card = new Card( 1L, LocalDateTime.now(), LocalDateTime.now().minusMonths(1), null, false, new User() );
        Book book = new Book( 1L, LocalDateTime.now(), "", "", 123L, 45L, 1L ); 
        Mockito.when( cardRecordRepository.findAll(PageRequest.of(page - 1, size))).thenReturn( cardRecordPage );
        Mockito.when( cardRepository.findById( 1L )).thenReturn( Optional.of( card ));
        Mockito.when( cardRepository.findById( 2L )).thenReturn( Optional.of( card ));
        Mockito.when( bookRepository.findById( 1L)).thenReturn( Optional.of( book ));
        Mockito.when( bookRepository.findById( 2L)).thenReturn( Optional.of( book ));
        List<RecordResponse> result = cardRecordService.getAllRecord( page, size );
        Allure.addAttachment( rezult, TYPE, result.toString() ); 
    }

    @ParameterizedTest
    @CsvSource({"0,2"})
    @DisplayName("Ленивая загрузка записей - Ошибка страницы")
    public void getAllRecordErrorPageTest( int page, int size ){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.getAllRecord( page, size ));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage()); 
    }

    @ParameterizedTest
    @CsvSource({"1,0"})
    @DisplayName("Ленивая загрузка записей - Ошибка размера страницы")
    public void getAllRecordErrorSizeTest( int page, int size ){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.getAllRecord( page, size ));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage()); 
    }

    @Test
    @DisplayName("Получение списка записей за промежуток времени и по пользователю( логин, почта или тел) с пагинацией")
    public void getRecordByCardTest(){
        CardRecordRequest cardRecordRequest = new CardRecordRequest( "Login", LocalDateTime.now().minusDays(10 ),  LocalDateTime.now(), 1, 10 );
        List<CardRecord> cardRecords = List.of( new CardRecord( 1L, LocalDateTime.now(), null, 1L, 1L),
                                                new CardRecord(2L, LocalDateTime.now(), null,  2L, 2L ));
        Card card = new Card( 1L, LocalDateTime.now(), LocalDateTime.now().minusMonths(1), null, false, new User() );
        Book book = new Book( 1L, LocalDateTime.now(), "", "", 123L, 45L, 1L ); 
        Allure.parameter("cardRecordRequest", cardRecordRequest); 
        Mockito.when( cardRecordRepository.getRecordsByPeriodAndCard( cardRecordRequest.user(),
                                                                      cardRecordRequest.start(),
                                                                      cardRecordRequest.finish(),
                                                                      PageRequest.of( cardRecordRequest.page() - 1, cardRecordRequest.size() ))).thenReturn( cardRecords );
        Mockito.when( cardRepository.findById( 1L )).thenReturn( Optional.of( card ));
        Mockito.when( cardRepository.findById( 2L )).thenReturn( Optional.of( card )); 
        Mockito.when( bookRepository.findById( 1L )).thenReturn( Optional.of( book ));
        Mockito.when( bookRepository.findById( 2L )).thenReturn( Optional.of( book ));                                                                   
        CardRecordResponse result = cardRecordService.getRecordByCard( cardRecordRequest );
        Allure.addAttachment( rezult, TYPE, result.toString() ); 
    }


    @Test
    @DisplayName("Ленивая загрузка записей - Ошибка нумерации страницы")
    public void getRecordByCardErrorPageTest(){
        CardRecordRequest cardRecordRequest = new CardRecordRequest( "Login", LocalDateTime.now().minusDays(10 ),  LocalDateTime.now(), 0, 10 );
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.getRecordByCard( cardRecordRequest ));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage()); 
    }

    @Test
    @DisplayName("Ленивая загрузка записей - Ошибка размера страницы")
    public void getRecordByCardErrorSizeTest(){
        CardRecordRequest cardRecordRequest = new CardRecordRequest( "Login", LocalDateTime.now().minusDays(10 ),  LocalDateTime.now(), 1, 0 );
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.getRecordByCard( cardRecordRequest ));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage()); 
    }

    @Test
    @DisplayName("Создание новой выдачи")
    public void createCardRecord(){
            CreateCardRecordRequest createCardRecordRequest = new CreateCardRecordRequest(1245L, 1L );
            User user             = new User(1L, LocalDateTime.now(), "Ivan", "", "","","","", false, "","", null);
            Card card             = new Card(1L, LocalDateTime.now(), LocalDateTime.now().minusMonths(1), null, false, user);
            Book book             = new Book(1L, LocalDateTime.now(), "Test Book", "Test Author", 1245L, 45L, 1L);
            CardRecord cardRecord = new CardRecord(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), 1L, 1L);
            Allure.parameter("createCardRecordRequest", createCardRecordRequest );
            Mockito.when( bookRepository.findBookByNumber( book.getBookNumber() )).thenReturn(Optional.of(book));
            Mockito.when( cardRepository.findById( card.getId() )).thenReturn(Optional.of(card));
            Mockito.when( cardRecordRepository.findRecordByBook( book.getId() )).thenReturn(Optional.empty());
            Mockito.when( cardRepository.findById( card.getId() )).thenReturn(Optional.of(card)); 
            Mockito.when( bookRepository.findById( book.getId() )).thenReturn(Optional.of(book)); 
            Mockito.when( cardRecordRepository.save( Mockito.any( CardRecord.class ))).thenReturn( cardRecord );
            RecordResponse result = cardRecordService.createCardRecord(createCardRecordRequest);
            Allure.addAttachment( rezult, TYPE, result.toString() ); 
    }

    @Test
    @DisplayName("Создание новой выдачи - Ошибка с номером книги")
    public void createCardRecordErrorBook( ){
        CreateCardRecordRequest createCardRecordRequest = new CreateCardRecordRequest( 1245L, 1L );
        Mockito.when( bookRepository.findBookByNumber( 1245L )).thenReturn(Optional.empty());
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.createCardRecord(createCardRecordRequest));
        Assertions.assertEquals("Такой книги не существует!", exception.getMessage()); 
    }

    @Test
    @DisplayName("Создание новой выдачи - Ошибка с номером карты пользователя")
    public void createCardRecordErrorCard( ){
        CreateCardRecordRequest createCardRecordRequest = new CreateCardRecordRequest( 1245L, 1L );
        Mockito.when( bookRepository.findBookByNumber( 1245L )).thenReturn(Optional.of( new Book()));
        Mockito.when( cardRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.createCardRecord(createCardRecordRequest));
        Assertions.assertEquals("Такой карты не существует!", exception.getMessage()); 
    }

    @Test
    @DisplayName("Создание новой выдачи - Ошибка, книга уже выдана")
    public void createCardRecordErrorCardRecord( ){
        CreateCardRecordRequest createCardRecordRequest = new CreateCardRecordRequest(1245L, 1L );
        Card card             = new Card(1L, LocalDateTime.now(), LocalDateTime.now().minusMonths(1), null, false, null );
        Book book             = new Book(1L, LocalDateTime.now(), "Test Book", "Test Author", 1245L, 45L, 1L);
        CardRecord cardRecord = new CardRecord(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2), 1L, 1L);
        Mockito.when( bookRepository.findBookByNumber( 1245L )).thenReturn(Optional.of( book ));
        Mockito.when( cardRepository.findById(1L)).thenReturn(Optional.of( card));
        Mockito.when( cardRecordRepository.findRecordByBook( 1L )).thenReturn(Optional.of( cardRecord ));
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> cardRecordService.createCardRecord(createCardRecordRequest));
        Assertions.assertEquals("Данная книга уже выдана!", exception.getMessage()); 
    }
}
