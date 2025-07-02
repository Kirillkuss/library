package com.itrail.library.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecordReponse( String user,
                             LocalDateTime createDate,
                             LocalDateTime finishDate,
                             BookResponse book) implements Serializable {
    
}
