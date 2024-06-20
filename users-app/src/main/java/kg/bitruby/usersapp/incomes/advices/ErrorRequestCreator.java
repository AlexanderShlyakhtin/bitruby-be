package kg.bitruby.usersapp.incomes.advices;

import kg.bitruby.usersapp.api.model.Error;
import kg.bitruby.usersapp.common.AppContextHolder;
import kg.bitruby.commonmodule.exceptions.ErrorCodeEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ErrorRequestCreator {

    public ResponseEntity<Error> formErrorResponse(ErrorCodeEnum code, String message, List<String> payload, HttpStatus httpStatus) {
        Error error = new Error(false, OffsetDateTime.now(), code.getEnumCode(), message, payload != null ? payload.stream().limit(10).collect(Collectors.toList()) : new ArrayList<>());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-request-id", AppContextHolder.getContextRequestId().toString());
        return new ResponseEntity<>(error, httpHeaders, httpStatus);
    }
}
