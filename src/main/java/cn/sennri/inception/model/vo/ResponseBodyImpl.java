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
        ResponseBodyImpl<T> responseBodyImpl = createNewResponse(status);
        responseBodyImpl.setData(data);
        return responseBodyImpl;
    }

    public static <T> ResponseBodyImpl<T> createNewResponse(HttpStatus status) {
        ResponseBodyImpl<T> responseBodyImpl = new ResponseBodyImpl<>();
        responseBodyImpl.setCode(status.value());
        responseBodyImpl.setMsg(status.getReasonPhrase());
        return responseBodyImpl;
    }

    public static <T> ResponseBodyImpl<T> createNewResponse(HttpStatus status, String selfDefinedMsg) {
        ResponseBodyImpl<T> responseBodyImpl = createNewResponse(status);
        responseBodyImpl.setMsg(selfDefinedMsg);
        return responseBodyImpl;
    }


    public static <T> ResponseBodyImpl<T> createNewResponse(T data, int selfDefinedCode, String selfDefinedMsg) {
        ResponseBodyImpl<T> responseBodyImpl = new ResponseBodyImpl<>();
        responseBodyImpl.setCode(selfDefinedCode);
        responseBodyImpl.setMsg(selfDefinedMsg);
        responseBodyImpl.setData(data);
        return responseBodyImpl;
    }

    public static <T> ResponseBodyImpl<T> createNewResponse(T data, HttpStatus status, String selfDefinedMsg) {
        ResponseBodyImpl<T> response = createNewResponse(status, selfDefinedMsg);
        response.setData(data);
        return response;
    }


    public static ResponseBodyImpl<?> error(String message, int code) {
        return ResponseBodyImpl.builder().code(code).msg(message).build();
    }
}
