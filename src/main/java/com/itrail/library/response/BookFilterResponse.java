package com.itrail.library.response;

import java.io.Serializable;
import java.util.List;

public record BookFilterResponse ( AuthResponse author,
                                   List<BookResponse> books ) implements Serializable{}
