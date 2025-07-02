package com.itrail.library.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lib_authors")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Author implements Serializable {

    @Hidden
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY) private Long id;
    @Hidden
    @Column( name = "lu_date" )                          private LocalDateTime luDate;
    @Column( name = "first_name")                        private String firstName;
    @Column( name = "second_name" )                      private String secondName;
    @Column( name = "middle_name" )                      private String middleName;
    @Column( name = "country" )                          private String country;
    
}
