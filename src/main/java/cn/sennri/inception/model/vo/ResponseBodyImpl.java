package cn.sennri.inception.model.vo;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBodyImpl<T> {
    private int code;
    private T data;
    private String msg;

    public static <T> ResponseBodyImpl<T> createNewResponse(T data, HttpStatus status) {
        return createNewResponse(data, status.value(), status.getReasonPhrase());
    }

    public static <T> ResponseBodyImpl<T> createNewResponse(HttpStatus status) {
        return createNewResponse(null, status.value(), status.getReasonPhrase());
    }

    public static <T> ResponseBodyImpl<T> createNewResponse(HttpStatus status, String selfDefinedMsg) {
        return createNewResponse(null, status.value(), selfDefinedMsg);
    }


    public static <T> ResponseBodyImpl<T> createNewResponse(T data, int code, String msg) {
        ResponseBodyImpl<T> responseBodyImpl = new ResponseBodyImpl<>();
        responseBodyImpl.setCode(code);
        responseBodyImpl.setMsg(msg);
        responseBodyImpl.setData(data);
        return responseBodyImpl;
    }

    public static <T> ResponseBodyImpl<T> createNewResponse(T data, HttpStatus status, String selfDefinedMsg) {
        return createNewResponse(data, status.value(),  selfDefinedMsg);
    }


    public static ResponseBodyImpl<?> error(String message, int code) {
        return ResponseBodyImpl.builder().code(code).msg(message).build();
    }
}
