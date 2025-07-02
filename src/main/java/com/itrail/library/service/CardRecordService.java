package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.itrail.library.aspect.logger.ExecuteMethodLog;
import com.itrail.library.domain.Book;
import com.itrail.library.domain.CardRecord;
import com.itrail.library.domain.User;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.repository.CardRecordRepository;
import com.itrail.library.repository.CardRepository;
import com.itrail.library.request.CardRecordRequest;
import com.itrail.library.request.CreateCardRecordRequest;
import com.itrail.library.response.BookResponse;
import com.itrail.library.response.CardRecordResponse;
import com.itrail.library.response.RecordReponse;
import lombok.RequiredArgsConstructor;
/*
 * Сервис для работы с записями
*/
@Service
@RequiredArgsConstructor
public class CardRecordService {

    private final CardRecordRepository cardRecordRepository;
    private final BookRepository       bookRepository;
    private final CardRepository       cardRepository;

    /**
     * Добавление записи 
     * @param idBook - Ид книги
     * @param idCard - Ид карты
     * @return CardRecord
     */
    public CardRecord saveRecord( Long bookNumber, Long idCard ){
        Optional<Book> book = bookRepository.findBookByNumber( bookNumber );
        if( book.isEmpty() ) throw new IllegalArgumentException("Такой книги не существует!");
        if( cardRepository.findById( idCard ).isEmpty() ) throw new IllegalArgumentException("Такой карты не существует!");
        if( cardRecordRepository.findRecordByBook( book.orElseThrow().getId() ).isPresent() ) throw new IllegalArgumentException("Данная книга уже выдана!" );
        CardRecord cardRecord = new CardRecord();
                   cardRecord.setCreateDate( LocalDateTime.now() );
                   cardRecord.setFinishDate( LocalDateTime.now().plusDays( 2 ));
                   cardRecord.setBookId( book.orElseThrow().getId() );
                   cardRecord.setCardId( idCard );
        return cardRecordRepository.save( cardRecord );
    }
    /**
     * Добавление записи
     * @param createCardRecordRequest - входной запрос
     * @return RecordReponse
     */
    public RecordReponse createCardRecord( CreateCardRecordRequest createCardRecordRequest ){
        CardRecord cardRecord = saveRecord( createCardRecordRequest.bookNumber(), createCardRecordRequest.idCard() );
        User user = cardRepository.findById( cardRecord.getCardId() ).orElseThrow().getUser();
        Book book = bookRepository.findById( cardRecord.getBookId() ).orElseThrow();
        return new RecordReponse( user.getFirstName() + ' ' + user.getSecondName() + ' ' + user.getMiddleName() ,
                                  cardRecord.getCreateDate(),
                                  cardRecord.getFinishDate(),
                                  new BookResponse( null, 
                                                    book.getNameBook(),
                                                    book.getDescriptionBook(),
                                                    book.getBookNumber(),
                                                    book.getPageBook() )); 
    }


    /**
     * Получение списка записей по карте пользователя за промежуток времени 
     * @param cardRecordRequest - входной запрос
     * @return CardRecordResponse
     */
    @ExecuteMethodLog
    public CardRecordResponse getRecordByCard( CardRecordRequest cardRecordRequest ){
        if( cardRecordRequest.page() <= 0 ) throw new IllegalArgumentException("Значение страницы должно быть больше нуля!");
        if( cardRecordRequest.page() <= 0 ) throw new IllegalArgumentException("Значение размера страницы должно быть больше нуля!");
        return new CardRecordResponse( cardRecordRepository.getRecordsByPeriodAndCard( cardRecordRequest.user(),
                                                                                       cardRecordRequest.start(),
                                                                                       cardRecordRequest.finish(),
                                                                                       PageRequest.of( cardRecordRequest.page() - 1, cardRecordRequest.size() ))
                                                            .stream()
                                                            .map( recordCard -> {
                                                                    User user = cardRepository.findById(recordCard.getCardId()).orElseThrow().getUser();
                                                                    return new RecordReponse( user.getFirstName() + ' ' + user.getSecondName() + ' ' + user.getMiddleName(),
                                                                                              recordCard.getCreateDate(),
                                                                                              recordCard.getFinishDate(),
                                                                                              bookRepository.findById( recordCard.getBookId() )
                                                                                                            .map(  book ->{
                                                                                                                return new BookResponse(null,
                                                                                                                                         book.getNameBook(),
                                                                                                                                         book.getDescriptionBook(),
                                                                                                                                         book.getBookNumber(),
                                                                                                                                         book.getPageBook() );
                                                                                                            }).orElseThrow());
                                                            }).toList());
    }
}
