package kg.bitruby.usersservice.incomes.rest.advices;

import kg.bitruby.commonmodule.exceptions.ErrorCodeEnum;
import kg.bitruby.usersservice.api.model.Error;
import kg.bitruby.usersservice.common.AppContextHolder;
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
