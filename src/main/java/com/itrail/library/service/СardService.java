package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.itrail.library.domain.Book;
import com.itrail.library.domain.Card;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.repository.CardRecordRepository;
import com.itrail.library.repository.CardRepository;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.response.BookResponse;
import com.itrail.library.response.CardInfoResponse;
import com.itrail.library.response.CardResponse;
import com.itrail.library.response.RecordReponse;
import com.itrail.library.response.UserResponse;
import lombok.RequiredArgsConstructor;
/**
 * Сервис для работы с картами пользователй
 */
@CacheConfig(cacheNames={"cards"})
@Service
@RequiredArgsConstructor
public class СardService {
    
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardRecordRepository cardRecordRepository;
    private final BookRepository bookRepository;
    /**
     * Добавление карты
     * @param idUser - Ид пользователя
     * @return Card
     */
    public Card saveCard( Long idUser){
        if( userRepository.findById( idUser ).isEmpty() ) throw new IllegalArgumentException("Нет такого пользователя!");
        if( cardRepository.findByUser( idUser ).isPresent() ) throw  new IllegalArgumentException("У пользователя есть уже карта!");
        Card card = new Card();
             card.setCreateDate( LocalDateTime.now() );
             card.setLuDate( LocalDateTime.now() );
        return cardRepository.save( card );
    }
    /**
     * Получение информции о пользователе и его карте, с его записями
     * @param user - Логин, почта или номер телефона
     * @param page - страница
     * @param size - размер
     * @return CardInfoResponse
     */
    @CachePut
    public CardInfoResponse getFullInfoCardAndRecord( String user, int page, int size ){
        Optional<Card> card = cardRepository.findCardByUser( user );
        if( card.isEmpty() ) throw new NoSuchElementException( "По даному запросу ничего не найдено!");
        return new CardInfoResponse( card.stream()
                                         .map( cardUser -> {
                                            //инф. о пользователе
                                            return new UserResponse( cardUser.getUser().getLogin(),
                                                                     cardUser.getUser().getFirstName() + " " + cardUser.getUser().getSecondName() + " " + cardUser.getUser().getMiddleName(),
                                                                     cardUser.getUser().getEmail(),
                                                                     cardUser.getUser().getPhone(), 
                                                                     null, 
                                                                     null,
                                                                     card.stream()
                                                                         .map( c -> {
                                                                            //инф о карте
                                                                            return new CardResponse( c.getCreateDate(),
                                                                                                     c.getFinishDate(),
                                                                                                     c.getIsopen(), 
                                                                                                     cardRecordRepository.findRecordsCurrent( card.orElseThrow().getId(), PageRequest.of( page - 1, size ) )
                                                                                                                         .stream()
                                                                                                                         .map( cr ->{
                                                                                                                            //инф о записях
                                                                                                                            Book book = bookRepository.findById( cr.getBookId() ).orElse( null );
                                                                                                                            return new RecordReponse(null,
                                                                                                                                                     cr.getCreateDate(),
                                                                                                                                                     cr.getFinishDate(),
                                                                                                                                                     //инф о книге
                                                                                                                                                     new BookResponse(null,
                                                                                                                                                                       book.getNameBook(), 
                                                                                                                                                                       null, 
                                                                                                                                                                       book.getBookNumber(), 
                                                                                                                                                                       null ));}).toList());
                                                                                }).findFirst().orElseThrow() );
                                            }).findFirst().orElseThrow() );

    }


}
