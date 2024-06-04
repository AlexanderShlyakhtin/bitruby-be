package com.bitruby.usersapp.incomes.advices;

import com.bitruby.usersapp.exceptions.BitrubyNotFoundException;
import com.bitruby.usersapp.exceptions.BitrubyRuntimeExpection;
import com.bitruby.usersapp.exceptions.ErrorCodeEnum;
import com.bitruby.usersapp.api.model.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.bitruby.usersapp.incomes.advices.ErrorRequestCreator.formErrorResponse;


@ControllerAdvice()
@Slf4j
public class ExceptionControllerAdvices {

    @ExceptionHandler(BitrubyNotFoundException.class)
    public ResponseEntity<Error> processException(BitrubyNotFoundException e) {
        log.error(String.valueOf(e));
        e.printStackTrace();
        return formErrorResponse(
                e.getErrorCodeEnum(),
                e.getMessage(),
                e.getPayload(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BitrubyRuntimeExpection.class)
    public ResponseEntity<Error> processException(BitrubyRuntimeExpection e) {
        log.error(String.valueOf(e));
        e.printStackTrace();
        return formErrorResponse(
            e.getErrorCodeEnum(),
            e.getMessage(),
            e.getPayload(),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> processException(Exception e) {
        log.error(String.valueOf(e));
        e.printStackTrace();
        return formErrorResponse(
                ErrorCodeEnum.UNSPECIFIED_ERROR,
                e.getMessage(),
                null,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
