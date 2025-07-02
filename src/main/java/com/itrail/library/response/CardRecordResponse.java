package com.itrail.library.response;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CardRecordResponse( List<RecordReponse> records ) implements Serializable {}
