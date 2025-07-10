package com.itrail.library.request.book;

import io.swagger.v3.oas.annotations.media.Schema;

public record FreeBooksRequest( 
    @Schema(description = "Код", example = "1")                  int code,
    @Schema(description = "Название книги", example = "Second4") String nameBook,
    @Schema(description = "Автор", example = "Gerald")           String author,
    @Schema(description = "Номер книги", example = "4534524")    Integer number,
    @Schema(description = "Страница", example = "1")             int page,
    @Schema(description = "Размер",   example = "10")            int size) {
    
}


     
