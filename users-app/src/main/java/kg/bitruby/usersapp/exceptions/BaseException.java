package kg.bitruby.usersapp.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class BaseException extends RuntimeException {

    private List<String> payload;
    private ErrorCodeEnum errorCodeEnum;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, ErrorCodeEnum errorCode) {
        super(message);
        errorCodeEnum = errorCode;
    }

    public BaseException(String message, Throwable throwable) {
        super(message, throwable);
        payload = new ArrayList<>();
    }


    public BaseException(String message, String value) {
        super(message);
        payload = new ArrayList<>();
        payload.add(value);
    }

    public abstract HttpStatus httpStatusCode();


}
