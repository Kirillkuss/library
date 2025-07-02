package com.itrail.library.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AuthResponse( LocalDateTime luDate,
                            String firstName,
                            String secondName,
                            String middleName,
                            String country ) implements Serializable{}
