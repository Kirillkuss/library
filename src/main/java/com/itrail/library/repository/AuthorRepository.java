package com.itrail.library.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    String queryFio = """
                        SELECT a FROM Author a
                        WHERE LOWER(CONCAT( a.firstName, ' ', a.secondName, ' ', a.middleName )) 
                        LIKE LOWER(CONCAT('%', :fio, '%'))
                      """;

    @Query( queryFio )
    List<Author> findAuthorsByFio( String fio );
    
}
