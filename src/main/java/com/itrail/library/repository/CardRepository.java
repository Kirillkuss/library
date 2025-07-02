package com.itrail.library.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c WHERE c.user.id = :id")
    Optional<Card> findByUser( Long id );
    /**
     * Поиск карты по пользователю( логин, телефон или почта )
     * @param user - пользователь
     * @return Optional Card
    */
    @Query( "SELECT c FROM Card c WHERE c.user.login = :user OR c.user.phone =:user OR c.user.email = :user")
    public Optional<Card> findCardByUser( String user );

    
}
