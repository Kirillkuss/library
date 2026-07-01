package com.itrail.library.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
import com.itrail.library.domain.Author;
import com.itrail.library.domain.Book;
import com.itrail.library.repository.AuthorRepository;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.request.book.BookFilterRequest;
import com.itrail.library.request.book.FreeBooksRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.BookFilterResponse;
import com.itrail.library.response.BookResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Disabled
@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - BookService")
@DisplayName("Тестирование сервиса - BookService")
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock private BookRepository   bookRepository;
    @Mock private AuthorRepository authorRepository;

    @InjectMocks private BookService bookService;

    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";


    @Test
    @DisplayName("Поиск книг по автору")
    public void getBooksByAuthorTest(){
        BookFilterRequest bookFilterRequest = new BookFilterRequest("F", 1, 10);
        Allure.parameter( "bookFilterRequest", bookFilterRequest );
        List<Author> authors = List.of(new Author(1L, LocalDateTime.now(), "F", "S", "M", "UK"));
        List<Book> books = List.of( new Book(1L, LocalDateTime.now(), "123", "123", 12345L, 345L, 1L),
                                    new Book(2L, LocalDateTime.now(), "234", "234", 54321L, 123L, 1L) );
        Mockito.when(authorRepository.findAuthorsByFio(bookFilterRequest.fio())).thenReturn(authors);
        Mockito.when(bookRepository.findBooksByAuthor(1L,  PageRequest.of(bookFilterRequest.page() - 1, bookFilterRequest.size()))).thenReturn(books);
        BaseResponse<BookFilterResponse> bookFilterResponse = bookService.getBooksByAuthor(bookFilterRequest);
        Mockito.verify(authorRepository).findAuthorsByFio(bookFilterRequest.fio());
        Mockito.verify(bookRepository).findBooksByAuthor(1L,  PageRequest.of(bookFilterRequest.page() - 1, bookFilterRequest.size()));
        Allure.addAttachment( rezult, TYPE, bookFilterResponse.toString() );
    }

    //@Test
    @DisplayName("Поиск книг по автору с указанием неправной страницы")
    public void getBooksByAuthorErrorPageTest(){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getBooksByAuthor(new BookFilterRequest("F", 0, 10)));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage());
    }

   // @Test
    @DisplayName("Поиск книг по автору с указанием непрального размера страницы")
    public void getBooksByAuthorErrorSizeTest(){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getBooksByAuthor(new BookFilterRequest("F", 1, 0)));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage());
    }

  //  @Test
    @DisplayName("Поиск книг по автору с ошибкой, что ничего не найдено")
    public void getBooksByAuthorErrorNotFoundTest(){
        NoSuchElementException exception = Assertions.assertThrows( NoSuchElementException.class, () -> bookService.getBooksByAuthor(new BookFilterRequest("F", 1, 10)));
        Assertions.assertEquals("По данному запросу ничего не найдено", exception.getMessage());
    }


    @Test
    @DisplayName("Поиск книг по разным параметрам")
    public void getFreeBooksTest(){
        FreeBooksRequest request1 = new FreeBooksRequest( 0, "nameBook", "author", 12345, 1, 10 );
        Allure.parameter( "request1", request1 );
        FreeBooksRequest request2 = new FreeBooksRequest( 1, "nameBook", "author", 12345, 1, 10 );
        Allure.parameter( "request2", request2 );
        FreeBooksRequest request3 = new FreeBooksRequest( 2, "nameBook", "author", 12345, 1, 10 );
        Allure.parameter( "request3", request3 );
        FreeBooksRequest request4 = new FreeBooksRequest( 3, "nameBook", "author", 12345, 1, 10 );
        Allure.parameter( "request4", request4 );
        PageRequest page = PageRequest.of( request1.page() - 1, request1.size() );
        List<Book> books = List.of( new Book(1L, LocalDateTime.now(), "123", "123", 12345L, 345L, 1L),
                                    new Book(2L, LocalDateTime.now(), "234", "234", 54321L, 123L, 1L) );
        Mockito.when( bookRepository.getFreeBooksWithoutParam( page )).thenReturn( books );
        Mockito.when( bookRepository.getFreeBooks( request2.nameBook(), null, null, page )).thenReturn( books );
        Mockito.when( bookRepository.getFreeBooks( null, request3.author(), null, page )).thenReturn( books );
        Mockito.when( bookRepository.getFreeBooks( null, null, request4.number(), page )).thenReturn( books );
        BaseResponse<List<BookResponse>> booksResponse1 = bookService.getFreeBooks( request1 );
        BaseResponse<List<BookResponse>> booksResponse2 = bookService.getFreeBooks( request2 );
        BaseResponse<List<BookResponse>> booksResponse3 = bookService.getFreeBooks( request3 );
        BaseResponse<List<BookResponse>> booksResponse4 = bookService.getFreeBooks( request4 );
        Mockito.verify(bookRepository).getFreeBooksWithoutParam(page);
        Mockito.verify(bookRepository).getFreeBooks( request2.nameBook(), null, null, page );
        Mockito.verify(bookRepository).getFreeBooks( null, request3.author(), null, page );
        Mockito.verify(bookRepository).getFreeBooks( null, null, request4.number(), page );
        Allure.addAttachment( rezult, TYPE, booksResponse1.toString() );
        Allure.addAttachment( rezult, TYPE, booksResponse2.toString() );
        Allure.addAttachment( rezult, TYPE, booksResponse3.toString() );
        Allure.addAttachment( rezult, TYPE, booksResponse4.toString() );
    }

   // @Test
    @DisplayName("Поиск книг по разным параметрам - неправильная нумерация страницы")
    public void getFreeBooksErrorPageTest(){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getFreeBooks( new FreeBooksRequest( 0, "nameBook", "author", 12345, 0, 10 )));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage());
    }

    //@Test
    @DisplayName("Поиск книг по разным параметрам - пеправильный размер страницы")
    public void getFreeBooksErrorSizeTest(){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getFreeBooks( new FreeBooksRequest( 0, "nameBook", "author", 12345, 1, 0 )));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage());
    }

    //@Test
    @DisplayName("Поиск книг по разным параметрам - неправильный код")
    public void  getFreeBooksErrorNotFoundTest(){
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getFreeBooks( new FreeBooksRequest( 23, "nameBook", "author", 12345, 1, 10 )));
        Assertions.assertEquals("Неверный код задачи!", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"1,10"})
    @DisplayName("Ленивая загрузка книг")
    public void getAllBooksTest( int page, int size ){
        List<Book> books = List.of( new Book( 1L, LocalDateTime.now(),"", "", 12345L, 345L, 1L ),
                                    new Book(2L, LocalDateTime.now(),"", "", 54321L, 123L, 2L )); 
        Page<Book> bookPage = new PageImpl<>( books );
        Mockito.when( bookRepository.findAll( PageRequest.of(page - 1, size))).thenReturn(bookPage);
        List<BookResponse> result = bookService.getAllBooks(page, size);
        Mockito.verify(bookRepository).findAll(PageRequest.of(page - 1, size));
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    //@ParameterizedTest
    @CsvSource({"0,5"})
    @DisplayName("Проверка на корректность ввода страницы в методе getAllBooks")
    public void getAllBooksErrorPageTest( int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getAllBooks(page, size));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage());
    }

    //@ParameterizedTest
    @CsvSource({"1,0"})
    @DisplayName("Проверка на корректность ввода размера страницы в методе getAllBooks ")
    public void getAllBooksErrorSizeTest( int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.getAllBooks(page, size));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage());
    }

    @Test
    @DisplayName("Сохранение книги")
    public void saveBookTest(){
        Long idAuthor = 1L;
        Optional<Book> book = Optional.of(new Book( 1L, LocalDateTime.now(),"", "", 12345L, 345L, 1L ));
        Optional<Author> author = Optional.of( new Author(1L, LocalDateTime.now(), "F", "S", "M", "UK"));
        Allure.parameter( "idAuthor", idAuthor );
        Allure.parameter( "book", book.orElseThrow() );
        Mockito.when( authorRepository.findById( idAuthor )).thenReturn( author );
        Mockito.when( bookRepository.findBookByNumber( book.orElseThrow().getBookNumber() )).thenReturn( Optional.empty() );
        Mockito.when( bookRepository.save( Mockito.any(Book.class))).thenReturn(  book.orElseThrow() );
        Book reponse = bookService.saveBook( book.orElseThrow(), idAuthor );
        Allure.addAttachment( rezult, TYPE, reponse.toString() );
    }

    @Test
    @DisplayName("Сохранение книги - Ошибка по поиску автора")
    public void saveBookErrorAuthorTest(){
        Optional<Book> book = Optional.of(new Book( 1L, LocalDateTime.now(),"", "", 12345L, 345L, 1L ));
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.saveBook( book.orElseThrow(), 2345L ));
        Assertions.assertEquals("Нет такого автора!", exception.getMessage());
    }

    @Test
    @DisplayName("Сохранение книги - Ошибка по уникальности номера книги")
    public void saveBookErrorBookNumberTest(){
        Book book = new Book( 1L, LocalDateTime.now(),"", "", 12345L, 345L, 1L );
        Author author =  new Author(1L, LocalDateTime.now(), "F", "S", "M", "UK");
        Mockito.when( authorRepository.findById( 1L )).thenReturn( Optional.of( author) );
        Mockito.when( bookRepository.findBookByNumber( book.getBookNumber() )).thenReturn( Optional.of( book ));
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> bookService.saveBook( book, 1L ));
        Assertions.assertEquals("Номер книги не уникальный!", exception.getMessage());
    }
    
}
