package com.itrail.library.request.book;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookFilterRequest(
    @Schema(description = "ФИО",      example = "Gerald 6") String fio,
    @Schema(description = "Страница", example = "1")        int page,
    @Schema(description = "Размер",   example = "10")       int size
) {}
