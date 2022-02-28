package cn.sennri.inception.model.vo;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response<T> {
    private int code;
    private T data;
    private String msg;

    public static <T> Response<T>  createNewResponse(T data, HttpStatus status) {
        Response<T> response = new Response<>();
        response.setCode(status.value());
        response.setMsg(status.getReasonPhrase());
        response.setData(data);
        return response;
    }

    public static <T> Response<T>  createNewResponse(HttpStatus status) {
        Response<T> response = new Response<>();
        response.setCode(status.value());
        response.setMsg(status.getReasonPhrase());
        return response;
    }

    public static <T> Response<T>  createNewResponse(HttpStatus status, String selfDefinedMsg) {
        Response<T> response = new Response<>();
        response.setCode(status.value());
        response.setMsg(selfDefinedMsg);
        return response;
    }


    public static <T> Response<T>  createNewResponse(T data, int selfDefinedCode, String selfDefinedMsg) {
        Response<T> response = new Response<>();
        response.setCode(selfDefinedCode);
        response.setMsg(selfDefinedMsg);
        response.setData(data);
        return response;
    }

    public static Response error(String message, int code) {
        return Response.builder().code(code).msg(message).build();
    }
}
