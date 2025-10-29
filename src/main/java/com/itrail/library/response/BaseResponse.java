package com.itrail.library.response;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable{

    private int status;
    private String message;
    private String error;
    private T data;

    public BaseResponse( int status, String message, String error, T data ){
        this.status = status;
        this.message = message;
        this.error = error;
        this.data = data;
    }

    public BaseResponse( int status, String message){
        this.status = status;
        this.message = message;
    }

    public BaseResponse(){
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "success", null, data );
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(200, "success" );
    }
    
    public static <T> BaseResponse<T> error( int status, String error) {
        return new BaseResponse<>( status, null, error, null );
    }


}

