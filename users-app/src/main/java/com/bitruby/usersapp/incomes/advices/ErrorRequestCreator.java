package com.bitruby.usersapp.incomes.advices;

import com.bitruby.usersapp.exceptions.ErrorCodeEnum;
import com.bitruby.usersapp.api.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorRequestCreator {

    public static ResponseEntity<Error> formErrorResponse(ErrorCodeEnum code, String message, List<String> payload, HttpStatus httpStatus) {
        Error error = new Error();
        error.setCode(code.getEnumCode());
        error.setMessage(message);

        ArrayList<String> messages = new ArrayList<>();

        messages.add("Error code: " + code);

        if (payload != null) {
            messages.addAll(payload);
        }

        error.setPayload(messages.stream().limit(10).collect(Collectors.toList()));

        return new ResponseEntity<>(error, httpStatus);
    }
}
