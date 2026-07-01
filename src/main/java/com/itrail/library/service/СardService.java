package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itrail.library.aspect.metrics.TrackMetrics;
import com.itrail.library.domain.Book;
import com.itrail.library.domain.Card;
import com.itrail.library.domain.User;
import com.itrail.library.repository.BookRepository;
import com.itrail.library.repository.CardRecordRepository;
import com.itrail.library.repository.CardRepository;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.BookResponse;
import com.itrail.library.response.CardInfoResponse;
import com.itrail.library.response.CardResponse;
import com.itrail.library.response.CardResponseLazy;
import com.itrail.library.response.RecordResponse;
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
    @TrackMetrics(layer = "service", tags = "operation=saveCard")
    @Transactional
    public BaseResponse<Card> saveCard( Long idUser){
        Optional<User> user = userRepository.findById( idUser );
        if( user.isEmpty() ){
            return BaseResponse.error( 400, "Нет такого пользователя!" );
        } 
        if( cardRepository.findByUser( idUser ).isPresent() ){
            return BaseResponse.error( 400, "У пользователя есть уже карта!" );
        }
        Card card = new Card();
             card.setCreateDate( LocalDateTime.now() );
             card.setLuDate( LocalDateTime.now() );
             card.setUser( user.get() );
             card.setIsopen( true );
        return BaseResponse.success( cardRepository.save( card ));
    }
    /**
     * Получение информции о пользователе и его карте, с его записями
     * @param user - Логин, почта или номер телефона
     * @param page - страница
     * @param size - размер
     * @return CardInfoResponse
     */
    @TrackMetrics(layer = "service", tags = "operation=getFullInfoCardAndRecord")
    @CachePut
    public BaseResponse<CardInfoResponse> getFullInfoCardAndRecord( String user, int page, int size ){
        Optional<Card> card = cardRepository.findCardByUser( user );
        if( card.isEmpty() ){
            return BaseResponse.error( 400, "По даному запросу ничего не найдено!" );
        }else{
            return BaseResponse.success( new CardInfoResponse( card.stream()
                                         .map( cardUser -> {
                                            //инф. о пользователе 
                                            return new UserResponse( cardUser.getUser().getLogin(),
                                                                     cardUser.getUser().getLastName() + " " + cardUser.getUser().getFirstName() + " " + cardUser.getUser().getMiddleName(),
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
                                                                                                                            return new RecordResponse(null,
                                                                                                                                                     cr.getCreateDate(),
                                                                                                                                                     cr.getFinishDate(),
                                                                                                                                                     //инф о книге
                                                                                                                                                     new BookResponse(null,
                                                                                                                                                                       book.getNameBook(), 
                                                                                                                                                                       null, 
                                                                                                                                                                       book.getBookNumber(), 
                                                                                                                                                                       null ));}).toList());
                                                                                }).findFirst().orElseThrow() );
                                            }).findFirst().orElseThrow() ));
        }


    }

    @TrackMetrics(layer = "service", tags = "operation=getLazyCard")
    public List<CardResponseLazy> getLazyCard( int page, int size ){
        return cardRepository.findAll( PageRequest.of( page - 1, size ))
                             .stream().map( card -> {
                                return new CardResponseLazy( card.getId(),
                                                             card.getCreateDate(),
                                                             card.getFinishDate(),
                                                             card.getIsopen(),
                                                             card.getUser().getLastName() + " " + card.getUser().getFirstName() + " " + card.getUser().getMiddleName(),
                                                             card.getUser().getLogin(), 
                                                             card.getUser().getPhone(),
                                                             card.getUser().getEmail() );
                             }).toList();
    }

}
