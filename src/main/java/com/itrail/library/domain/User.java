package com.itrail.library.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lib_users")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY) private Long id;
    @Column ( name = "lu_date" )                         private LocalDateTime luDate;
    @Column ( name = "login" )                           private String login;
    @Column ( name = "password" )                        private String password;
    @Column ( name = "first_name" )                      private String lastName;
    @Column ( name = "second_name" )                     private String firstName;
    @Column ( name = "middle_name" )                     private String middleName;
    @Column ( name = "email" )                           private String email;
    @Column ( name = "is_open" )                         private Boolean isOpen;
    @Column ( name = "phone" )                           private String phone;
    @Column ( name = "secret" )                          private String secret;

    @ManyToMany( fetch = FetchType.EAGER )
    @JoinTable(
        name = "lib_users_roles",
        joinColumns        = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )                                                    
                                                         private Set<Role> roles;

}
