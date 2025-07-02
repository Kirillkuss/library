package com.itrail.library.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    @Query( "SELECT r from Role r WHERE r.name = :name")
    Optional<Role> findByName( String name );
    
}
