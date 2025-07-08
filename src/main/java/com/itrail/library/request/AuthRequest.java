package com.itrail.library.request;
/**
 * Входной запрос на авторизацию
 */
public record AuthRequest( String username,
                           String password ) {  
}
