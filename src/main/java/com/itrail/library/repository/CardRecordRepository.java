package com.itrail.library.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.CardRecord;

@Repository
public interface CardRecordRepository extends JpaRepository<CardRecord, Long> {

    String requestCard = """
                        SELECT cr FROM CardRecord cr
                        LEFT JOIN Card c on c.id = cr.cardId
                        WHERE LOWER(CONCAT( c.user.lastName, ' ', c.user.firstName, ' ', c.user.middleName )) LIKE LOWER(CONCAT('%', :user, '%'))
                        AND cr.createDate BETWEEN :start AND :finish 
                        """;

    String requestCardSecond = """
                        SELECT cr FROM CardRecord cr
                        LEFT JOIN Card c on c.id = cr.cardId
                        WHERE c.user.login = :user OR c.user.phone =:user OR c.user.email = :user
                        AND cr.createDate BETWEEN :start AND :finish 
                        """;
    /**
     * Получение действующей записи для книги
     * @param idBook - ИД книги
     * @return Optional СardRecord
     */
    @Query( "SELECT cr FROM CardRecord cr WHERE cr.bookId = :idBook and cr.finishDate > CURRENT_TIMESTAMP" )
    Optional<CardRecord> findRecordByBook(  Long idBook );

    /**
     * Получение списка записей по ФИО пользователя за промежуток времени
     * @param user - Логин, почта или номер телефона 
     * @param start - дата и время начала фильтра
     * @param finish - дата и время окончания фильтра 
     * @param pageable -пагинация
     * @return List CardRecord
     */
    @Query( requestCardSecond )
    List<CardRecord> getRecordsByPeriodAndCard( String user, LocalDateTime start, LocalDateTime finish, Pageable pageable );
    
    /**
     * Получение списка действующий записей по карте пользователя
     * @param idCard - ИД карты пользователя
     * @return List CardRecord
     */
    @Query( "SELECT cr FROM CardRecord cr WHERE cr.cardId = :idCard and cr.finishDate > CURRENT_TIMESTAMP" )
    List<CardRecord> findRecordsCurrent( Long idCard, Pageable pageable );
}
