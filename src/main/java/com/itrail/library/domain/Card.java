package com.itrail.library.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Table(name = "lib_cards")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Card implements Serializable{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY) private Long id;
    @Column( name = "lu_date" )                          private LocalDateTime luDate;
    @Column( name = "create_date" )                      private LocalDateTime createDate;
    @Column( name = "finish_date" )                      private LocalDateTime finishDate;
    @Column( name = "isopen")                            private Boolean isopen;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
                                                         private User user;
    
}
