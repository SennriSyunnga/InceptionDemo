package cn.sennri.inception.model.vo;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBody<T> {
    private int code;
    private T data;
    private String msg;

    public static <T> ResponseBody<T> createNewResponse(T data, HttpStatus status) {
        return createNewResponse(data, status.value(), status.getReasonPhrase());
    }

    public static <T> ResponseBody<T> createNewResponse(HttpStatus status) {
        return createNewResponse(null, status.value(), status.getReasonPhrase());
    }

    public static <T> ResponseBody<T> createNewResponse(HttpStatus status, String selfDefinedMsg) {
        return createNewResponse(null, status.value(), selfDefinedMsg);
    }


    public static <T> ResponseBody<T> createNewResponse(T data, int code, String msg) {
        ResponseBody<T> responseBody = new ResponseBody<>();
        responseBody.setCode(code);
        responseBody.setMsg(msg);
        responseBody.setData(data);
        return responseBody;
    }

    public static <T> ResponseBody<T> createNewResponse(T data, HttpStatus status, String selfDefinedMsg) {
        return createNewResponse(data, status.value(),  selfDefinedMsg);
    }


    public static ResponseBody<?> error(String message, int code) {
        return ResponseBody.builder().code(code).msg(message).build();
    }
}
