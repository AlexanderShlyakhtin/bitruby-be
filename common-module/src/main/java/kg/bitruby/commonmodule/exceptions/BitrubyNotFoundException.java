package kg.bitruby.commonmodule.exceptions;

import org.springframework.http.HttpStatus;

public class BitrubyNotFoundException extends BaseException {

    public BitrubyNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BitrubyNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus httpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
