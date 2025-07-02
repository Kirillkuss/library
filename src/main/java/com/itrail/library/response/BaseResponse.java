package com.itrail.library.response;

import java.io.Serializable;

public record BaseResponse( int status,
                            String message) implements Serializable{}
