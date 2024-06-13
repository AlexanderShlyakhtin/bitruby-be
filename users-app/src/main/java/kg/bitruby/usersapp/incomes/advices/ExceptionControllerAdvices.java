package kg.bitruby.usersapp.incomes.advices;

import kg.bitruby.usersapp.api.model.Error;
import kg.bitruby.usersapp.exceptions.BitrubyNotFoundException;
import kg.bitruby.usersapp.exceptions.BitrubyRuntimeExpection;
import kg.bitruby.usersapp.exceptions.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;



@ControllerAdvice()
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerAdvices {

    private final ErrorRequestCreator errorRequestCreator;

    @ExceptionHandler(BitrubyNotFoundException.class)
    public ResponseEntity<Error> processException(BitrubyNotFoundException e) {
        log.error(String.valueOf(e));
        e.printStackTrace();
        return errorRequestCreator.formErrorResponse(
            ErrorCodeEnum.UNSPECIFIED_ERROR,
                e.getMessage(),
                e.getPayload(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BitrubyRuntimeExpection.class)
    public ResponseEntity<Error> processException(BitrubyRuntimeExpection e) {
        log.error(String.valueOf(e));
        e.printStackTrace();
        return errorRequestCreator.formErrorResponse(
            ErrorCodeEnum.UNSPECIFIED_ERROR,
            e.getMessage(),
            e.getPayload(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> processException(Exception e) {
        log.error(String.valueOf(e));
        e.printStackTrace();
        return errorRequestCreator.formErrorResponse(
                ErrorCodeEnum.UNSPECIFIED_ERROR,
                e.getMessage(),
                new ArrayList<>(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
