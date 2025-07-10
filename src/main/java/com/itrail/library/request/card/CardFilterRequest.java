package com.itrail.library.request.card;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * Входной запрос на получение информауии о карте пользозователя и его книгах
 */
public record CardFilterRequest(
    @Schema(description = "Логин, почта или телефон", example = "Test1234!") String user,
    @Schema(description = "Страница",                 example = "1")         int page,
    @Schema(description = "Размер",                   example = "10")        int size
) {}
