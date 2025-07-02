package com.itrail.library.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name = "lib_roles")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Role implements Serializable{
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY) private Long id;
    @Column ( name = "lu_date" )                         private LocalDateTime luDate;
    @Column ( name = "name_role" )                       private String name;
}
