package com.itrail.library.response;

import java.io.Serializable;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse( @Schema(description = "Логин")   String login,
                            @Schema(description = "ФИО")     String fio,
                            @Schema(description = "Почта")   String email,
                            @Schema(description = "Телефон") String phone,
                            @Schema(description = "Статус")  Boolean status,
                            @Schema(description = "Роли")    Set<String> roles,
                            @Schema(description = "карта")   CardResponse card ) implements Serializable{}
