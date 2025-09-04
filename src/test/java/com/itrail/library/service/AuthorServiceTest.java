package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.List;
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
import com.itrail.library.domain.Author;
import com.itrail.library.repository.AuthorRepository;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - AuthorService")
@DisplayName("Тестирование сервиса - AuthorService")
@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock        private AuthorRepository authorRepository;
    @InjectMocks private AuthorService authorService;

    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";
    public final String error    = "Ошибка: ";
    public final String leadTime = "Время выполнения: ";

    @ParameterizedTest
    @CsvSource({"First"})
    @DisplayName("Поиск автора по фио")
    public void getAuthorsByFioTest(String fio) {
        List<Author> expectedAuthors = List.of(new Author(1L, LocalDateTime.now(), "First", "Second", "Third", "UK"));
        Mockito.when(authorRepository.findAuthorsByFio(fio)).thenReturn(expectedAuthors);
        List<Author> result = authorService.getAuthors(fio);
        Assertions.assertEquals(expectedAuthors, result);
        Mockito.verify(authorRepository).findAuthorsByFio(fio);
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @ParameterizedTest
    @CsvSource({"2,3"})
    @DisplayName("Ленивая загрузка авторов")
    public void getAllAuthorsTest( int page, int size ) {
        List<Author> authors = List.of( new Author(1L, LocalDateTime.now(), "First", "Second", "Third", "UK"),
                                        new Author(2L, LocalDateTime.now(), "First2", "Second2", "Third2", "UK2"));
        Page<Author> authorPage = new PageImpl<>( authors );
        Mockito.when(authorRepository.findAll(PageRequest.of(page - 1, size))).thenReturn(authorPage);
        List<Author> result = authorService.getAllAuthors(page, size);
        Assertions.assertEquals(authors, result);
        Mockito.verify(authorRepository).findAll(PageRequest.of(page - 1, size));
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @ParameterizedTest
    @CsvSource({"0,5"})
    @DisplayName("Проверка на корректность ввода страницы в методе getAllAuthors")
    public void getAllAuthorsErrorPageTest( int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> authorService.getAllAuthors(page, size));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"1,0"})
    @DisplayName("Проверка на корректность ввода размера страницы в методе getAllAuthors ")
    public void getAllAuthorsErrorSizeTest( int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> authorService.getAllAuthors(page, size));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage());
    }
}

