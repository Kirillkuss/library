package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itrail.library.aspect.logger.ExecuteMethodLog;
import com.itrail.library.domain.Author;
import com.itrail.library.domain.Book;
import com.itrail.library.repository.AuthorRepository;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.request.BookFilterRequest;
import com.itrail.library.request.FreeBooksRequest;
import com.itrail.library.response.AuthResponse;
import com.itrail.library.response.BookFilterResponse;
import com.itrail.library.response.BookResponse;
import lombok.RequiredArgsConstructor;
/**
 * Сервис для работы с книгами
 */
@CacheConfig(cacheNames={"books"})
@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository   bookRepository;
    private final AuthorRepository authorRepository;

    /**
     * Получение списка книг по ФИО автора
     * @param fio - ФИО автора
     * @param page - страница
     * @param size - размер
     * @return List Book
     */
    @Cacheable
    @ExecuteMethodLog 
    public BookFilterResponse getBooksByAuthor( BookFilterRequest bookFilterRequest ){
        if( bookFilterRequest.page() <= 0 ) throw new IllegalArgumentException("Значение страницы должно быть больше нуля!");
        if( bookFilterRequest.size() <= 0 ) throw new IllegalArgumentException("Значение размера страницы должно быть больше нуля!");
        List<Author> authors = authorRepository.findAuthorsByFio( bookFilterRequest.fio() );
        if( authors.stream().count() == 1L ){
            List<Book> books = bookRepository.findBooksByAuthor( authors.stream()
                                                                        .findFirst()
                                                                        .orElseThrow()
                                                                        .getId(), PageRequest.of( bookFilterRequest.page() - 1, bookFilterRequest.size() ));

            Author author = authors.stream().findFirst().orElseThrow();                                            
            return new BookFilterResponse( new AuthResponse( author.getLuDate(),
                                                             author.getFirstName(),
                                                             author.getSecondName(),
                                                             author.getMiddleName(),
                                                             author.getCountry() ),
                                                             books.stream()
                                                                  .map( book -> {return getBookResponse(book);})
                                                                  .toList() );                                              
        }else{
            throw new NoSuchElementException( "По данному запросу ничего не найдено" );
        }
    }

    /**
     * Добавление книги
     * @param book - книга
     * @param idAuthor - Ид автора
     * @return Book
     */
    @Transactional
    public Book saveBook( Book book, Long idAuthor ){ 
        if ( authorRepository.findById( idAuthor ).isEmpty())                     throw new IllegalArgumentException("Нет такого автора!");
        if ( bookRepository.findBookByNumber( book.getBookNumber() ).isPresent()) throw new IllegalArgumentException("Номер книги не уникальный!");
            book.setLuDate( LocalDateTime.now() );
            book.setAuthorId( idAuthor );
        return bookRepository.save( book );
    }

    public List<BookResponse> getFreeBooks( FreeBooksRequest freeBooksRequest ){
        if( freeBooksRequest.page() <= 0 ) throw new IllegalArgumentException("Значение страницы должно быть больше нуля!");
        if( freeBooksRequest.size() <= 0 ) throw new IllegalArgumentException("Значение размера страницы должно быть больше нуля!");
        PageRequest page = PageRequest.of( freeBooksRequest.page() - 1, freeBooksRequest.size() );
        List<BookResponse> responses = new ArrayList<>();
        switch( freeBooksRequest.code() ){
            //все доступные
            case 0 ->{
                responses = bookRepository.getFreeBooksWithoutParam( page )
                                          .stream()
                                          .map( book -> {return getBookResponse(book);})
                                          .toList() ;
            }
            case 1 ->  {
                //Название 
                responses = bookRepository.getFreeBooks( freeBooksRequest.nameBook(),null,null, page )
                                          .stream()
                                          .map( book -> {return getBookResponse(book);})
                                          .toList() ;
            }
            case 2 ->{ 
                //автор
                responses = bookRepository.getFreeBooks( null, freeBooksRequest.author(),null, page  )
                                           .stream()
                                           .map( book -> {return getBookResponse(book);})
                                           .toList() ;
            }
            case 3 ->{
                //номер книги
                responses = bookRepository.getFreeBooks( null,null,freeBooksRequest.number(), page )
                                           .stream()
                                           .map( book -> {return getBookResponse(book);})
                                           .toList() ; 
            }
            default -> throw new IllegalArgumentException( "Неверный код задачи!");
        }
        return responses;
    }

    private BookResponse getBookResponse( Book book ){
         return new BookResponse( book.getLuDate(),
                                  book.getNameBook(),
                                  book.getDescriptionBook(),
                                  book.getBookNumber(),
                                  book.getPageBook() );
    }


}
