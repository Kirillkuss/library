package com.itrail.library.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query( "SELECT u from User u WHERE u.login = :login")
    Optional<User> findByLogin( String login );

    @Query( "SELECT u from User u WHERE u.email = :email")
    Optional<User> findByEmail( String email );

    @Query( "SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findUserByPhone( String phone );

    @Query("SELECT u FROM User u WHERE u.login = :userinfo OR u.phone = :userinfo OR u.email = :userinfo")
    Optional<User> findByChangePassword( String userinfo );
    
}
