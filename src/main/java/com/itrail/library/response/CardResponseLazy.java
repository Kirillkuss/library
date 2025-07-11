package com.itrail.library.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CardResponseLazy(
                            Long identifaication, 
                            LocalDateTime createDate,
                            LocalDateTime finishDate,
                            Boolean isopen,
                            String user,
                            String login,
                            String phone,
                            String email) implements Serializable{
    
}
