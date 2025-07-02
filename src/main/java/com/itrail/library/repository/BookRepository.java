package com.itrail.library.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.authorId = :idAuthor")
    public List<Book> findBooksByAuthor( @Param("idAuthor") Long idAuthor, Pageable pageable );

    @Query("SELECT b FROM Book b WHERE b.bookNumber = :number")
    public Optional<Book> findBookByNumber( Long number );

    String freeBooks = """
            SELECT b FROM Book b
            left join Author a on a.id = b.authorId
            WHERE NOT EXISTS (
                SELECT 1 FROM CardRecord cr
                WHERE cr.bookId = b.id AND cr.finishDate >= CURRENT_TIMESTAMP )
            AND ( b.nameBook =:nameBook
            OR b.bookNumber = :number
            OR LOWER(CONCAT( a.firstName, ' ', a.secondName, ' ', a.middleName )) LIKE LOWER(CONCAT('%', :author, '%')))
            """;

    @Query(freeBooks)
    public List<Book> getFreeBooks( @Param("nameBook") String nameBook,
                                    @Param("author")   String author,
                                    @Param("number")   Integer number,
                                    Pageable pageable );
    String freeBooksWithoutParam = """
            SELECT b FROM Book b
            left join Author a on a.id = b.authorId
            WHERE NOT EXISTS (
                SELECT 1 FROM CardRecord cr
                WHERE cr.bookId = b.id AND cr.finishDate >= CURRENT_TIMESTAMP )
            """;
    @Query(freeBooksWithoutParam)
    public List<Book> getFreeBooksWithoutParam( Pageable pageable );
    
}
