package com.itrail.library.request.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FreeBooksRequest( 
    @Schema(description = "Код", example = "1")                                     int code,
    @Schema(description = "Название книги", example = "Second4")                    String nameBook,
    @Schema(description = "Автор", example = "Gerald")                              String author,
    @Max( value = 2147483647, message = "Номер книги не должен быть больше {value}")
    @Schema(description = "Номер книги", example = "4534524")                       Integer number,
    @Schema(description = "Страница", example = "1")
    @Min(value = 1, message = "Значение страницы должно быть больше нуля!")         int page,
    @Schema(description = "Размер",   example = "10")            
    @Min(value = 1, message = "Значение размера страницы должно быть больше нуля!") int size ) {
    
}


     
