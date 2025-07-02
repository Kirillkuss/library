package com.itrail.library.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookResponse( LocalDateTime luDate,
                            String nameBook,
                            String descriptionBook,
                            Long bookNumber,
                            Long pages ) implements Serializable{}
