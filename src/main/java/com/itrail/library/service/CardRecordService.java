package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itrail.library.aspect.logger.ExecuteMethodLog;
import com.itrail.library.domain.Book;
import com.itrail.library.domain.CardRecord;
import com.itrail.library.domain.User;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.repository.CardRecordRepository;
import com.itrail.library.repository.CardRepository;
import com.itrail.library.request.record.CardRecordRequest;
import com.itrail.library.request.record.CreateCardRecordRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.BookResponse;
import com.itrail.library.response.CardRecordResponse;
import com.itrail.library.response.RecordResponse;
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

    public List<RecordResponse> getAllRecord( int page, int size ){
        return cardRecordRepository.findAll( PageRequest.of( page - 1, size ))
                              .stream()
                              .map( recordCard -> {
                                    User user = cardRepository.findById(recordCard.getCardId()).orElseThrow().getUser();
                                    return new RecordResponse(  user.getLastName() + " " + user.getFirstName()+ " " + user.getMiddleName(),
                                                               recordCard.getCreateDate(),
                                                               recordCard.getFinishDate(),
                                                               bookRepository.findById( recordCard.getBookId() )
                                                                             .map(  book ->{
                                                                                    return new BookResponse(null,
                                                                                                                book.getNameBook(),
                                                                                                                null,
                                                                                                                book.getBookNumber(),
                                                                                                                null );
                                                                                                            }).orElseThrow());
                                                            }).toList();
    }

    /**
     * Добавление записи 
     * @param idBook - Ид книги
     * @param idCard - Ид карты
     * @return CardRecord
     */
    @Transactional
    private BaseResponse<CardRecord> saveRecord( Long bookNumber, Long idCard ){
        Optional<Book> book = bookRepository.findBookByNumber( bookNumber );
        BaseResponse response = validateSaveRecord( book, idCard );
        if( response.getStatus() != 200 ){
            return BaseResponse.error( response.getStatus(), response.getError() );
        }else{
            CardRecord cardRecord = new CardRecord();
                       cardRecord.setCreateDate( LocalDateTime.now() );
                       cardRecord.setFinishDate( LocalDateTime.now().plusDays( 2 ));
                       cardRecord.setBookId( book.orElseThrow().getId() );
                       cardRecord.setCardId( idCard );
            return BaseResponse.success( cardRecordRepository.save( cardRecord ));
        }
    }
    /**
     * Проверка на добавление записи
     * @param book - Книга
     * @param idCard - Ид карта пользователя
     * @return BaseResponse
     */
    private BaseResponse<?> validateSaveRecord( Optional<Book> book, Long idCard ) {
        if (book.isEmpty()) {
            return BaseResponse.error(400, "Такой книги не существует!");
        }
        if (cardRepository.findById(idCard).isEmpty()) {
            return BaseResponse.error(400, "Такой карты не существует!");
        }
        if (cardRecordRepository.findRecordByBook(book.get().getId()).isPresent()) {
            return BaseResponse.error(400, "Данная книга уже выдана!");
        }
        return BaseResponse.success();
    }


    /**
     * Добавление записи
     * @param createCardRecordRequest - входной запрос
     * @return RecordReponse
     */
    @Transactional
    public BaseResponse<RecordResponse> createCardRecord( CreateCardRecordRequest createCardRecordRequest ){
        BaseResponse<CardRecord> cardRecord = saveRecord( createCardRecordRequest.bookNumber(), createCardRecordRequest.idCard() );
        if( cardRecord.getData() != null ){
            User user = cardRepository.findById( cardRecord.getData().getCardId() ).orElseThrow().getUser();
            Book book = bookRepository.findById( cardRecord.getData().getBookId() ).orElseThrow();
            return BaseResponse.success( new RecordResponse(  user.getLastName() + " " + user.getFirstName()+ " " + user.getMiddleName() ,
                                    cardRecord.getData().getCreateDate(),
                                    cardRecord.getData().getFinishDate(),
                                    new BookResponse( null, 
                                                        book.getNameBook(),
                                                        book.getDescriptionBook(),
                                                        book.getBookNumber(),
                                                        book.getPageBook() ))); 
        }else{
           return BaseResponse.error( cardRecord.getStatus(), cardRecord.getError() );
        }

    }


    /**
     * Получение списка записей по карте пользователя за промежуток времени 
     * @param cardRecordRequest - входной запрос
     * @return CardRecordResponse
     */
    @ExecuteMethodLog
    public CardRecordResponse getRecordByCard( CardRecordRequest cardRecordRequest ){
        return new CardRecordResponse( cardRecordRepository.getRecordsByPeriodAndCard( cardRecordRequest.user(),
                                                                                       cardRecordRequest.start(),
                                                                                       cardRecordRequest.finish(),
                                                                                       PageRequest.of( cardRecordRequest.page() - 1, cardRecordRequest.size() ))
                                                            .stream()
                                                            .map( recordCard -> {
                                                                    User user = cardRepository.findById(recordCard.getCardId()).orElseThrow().getUser();
                                                                    return new RecordResponse(  user.getLastName() + " " + user.getFirstName()+ " " + user.getMiddleName(),
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
