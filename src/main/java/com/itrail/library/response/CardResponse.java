package com.itrail.library.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CardResponse( LocalDateTime createDate,
                            LocalDateTime finishDate,
                            Boolean isopen,
                            List<RecordReponse> records) implements Serializable {
}
