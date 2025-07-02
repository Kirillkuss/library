package com.itrail.library.request;

import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание пользователя")
public record CreateUserRequest(
    @Schema(description = "Логин",    example = "ADMIN")          String login,
    @Schema(description = "Пароль",   example = "ADMIN2324")      String password,
    @Schema(description = "Фамилия",  example = "ADMIN1")         String firstName,
    @Schema(description = "Имя",      example = "ADMIN2")         String secondName,
    @Schema(description = "Отчество", example = "ADMIN3")         String middleName,
    @Schema(description = "Почта",    example = "ADMIN@mail.com") String email,
    @Schema(description = "Телефон",  example = "+375295734564")  String phone,
    @Schema(description = "Роли пользователя",
            example = "[\"ADMIN\", \"USER\"]",  
            type = "array" )                                      Set<String> roles
) {}
