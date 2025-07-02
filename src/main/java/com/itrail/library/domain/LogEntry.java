package com.itrail.library.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "lib_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)                         private Long id;
    @Column( name = "server_time", nullable = false, updatable = false)         private LocalDateTime serverTime;
    @Column( name = "log_level", nullable = false, length = 10)                 private String level;
    @Column( name = "logger", length = 255)                                     private String logger;  
    @Column( name = "log_message", nullable = false, columnDefinition = "TEXT") private String message;
    @Column( name = "exception", columnDefinition = "TEXT")                     private String exception;  
    @Column( name = "username", length = 100)                                   private String username;  
    @Column( name = "request_method", length = 10)                              private String requestMethod; 
    @Column( name = "request_uri", length = 255)                                private String requestUri; 
    @Column( name = "response_status")                                          private Integer responseStatus; 
    @Column( name = "execute_time")                                             private Long executeTime;
}
