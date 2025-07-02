package com.itrail.library.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lib_books")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Serializable {

    @Id
    @Hidden
    @GeneratedValue( strategy = GenerationType.IDENTITY) private Long id;
    @Column( name = "lu_date" )                          private LocalDateTime luDate;
    @Column( name = "name_book")                         private String nameBook;
    @Column( name = "description_book")                  private String descriptionBook;
    @Column( name = "book_number")                       private Long bookNumber;
    @Column( name = "page_book")                         private Long pageBook;
    @Hidden
    @Column( name = "author_id")                         private Long authorId;
    
}
