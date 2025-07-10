package com.itrail.library.request.record;
/**
 * Добалвение книги в карту пользователя
 */
public record CreateCardRecordRequest(
    Long bookNumber,
    Long idCard
) {}
