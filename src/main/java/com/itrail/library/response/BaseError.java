package com.itrail.library.response;

import java.io.Serializable;

public record BaseError( int code,
                         String error ) implements Serializable{}
