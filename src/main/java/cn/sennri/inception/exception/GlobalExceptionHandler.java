package cn.sennri.inception.exception;

import cn.sennri.inception.model.vo.ResponseBodyImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author Sennri
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class.getName());

    /** 运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseBodyImpl<Object>> runtimeExceptionHandler(RuntimeException ex) {
        logger.warn("运行时异常：{}", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBodyImpl.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg("运行时异常:" + ex.getMessage())
                .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** 空指针异常 */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseBodyImpl<Object>> nullPointerExceptionHandler(NullPointerException ex) {
        logger.warn("空指针异常：{} ", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBodyImpl.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg("空指针异常:" + ex.getMessage())
                .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** 类型转换异常 */
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ResponseBodyImpl<Object>> classCastExceptionHandler(ClassCastException ex) {
        logger.warn("类型转换异常：{} ", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBodyImpl.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .msg("类型转换异常:" + ex.getMessage())
                .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ResponseBodyImpl<Object>> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        logger.warn("不合法的输入参数：{} ", ex.getMessage(), ex);
        return new ResponseEntity<>(ResponseBodyImpl.builder().code(BAD_REQUEST.value())
                .msg("不合法的输入参数:" + ex.getMessage())
                .build(),
                BAD_REQUEST);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<ResponseBodyImpl<Object>> exceptionHandler(Exception ex) {
        logger.warn("fail: ", ex);
        return new ResponseEntity<>(ResponseBodyImpl.builder().code(BAD_REQUEST.value())
                .msg("其他异常:" + ex.getMessage())
                .build(),
                BAD_REQUEST);
    }
}
