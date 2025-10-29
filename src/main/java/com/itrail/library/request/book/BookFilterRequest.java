package com.itrail.library.request.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record BookFilterRequest(
    @Schema(description = "ФИО",      example = "Gerald 6")                         String fio,
    @Min(value = 1, message = "Значение страницы должно быть больше нуля!")
    @Schema(description = "Страница", example = "1")                                int page,
    @Min(value = 1, message = "Значение размера страницы должно быть больше нуля!")
    @Schema(description = "Размер",   example = "10")                               int size
) {}
