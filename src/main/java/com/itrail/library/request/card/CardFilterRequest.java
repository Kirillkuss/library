package com.itrail.library.request.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
/**
 * Входной запрос на получение информауии о карте пользозователя и его книгах
 */
public record CardFilterRequest(
    @Schema(description = "Логин, почта или телефон", example = "Test1234!")        String user,
    @Min(value = 1, message = "Значение страницы должно быть больше нуля!")
    @Schema(description = "Страница",                 example = "1")                int page,
    @Min(value = 1, message = "Значение размера страницы должно быть больше нуля!")
    @Schema(description = "Размер",                   example = "10")               int size
) {}
