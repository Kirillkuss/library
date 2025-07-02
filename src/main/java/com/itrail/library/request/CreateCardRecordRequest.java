package com.itrail.library.request;

public record CreateCardRecordRequest(
    Long bookNumber,
    Long idCard
) {}
