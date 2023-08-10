package com.example.asyncmultithread.files.controller.support;

import com.example.asyncmultithread.files.controller.response.ApiResponse;
import com.example.asyncmultithread.files.controller.support.exception.UncaughtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerExceptionHandler {

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(UncaughtException.class)
  public ApiResponse<Void> NotFoundEntityExceptionHandler(final UncaughtException e) {
    return ApiResponse.<Void>builder().message(e.getMessage()).build();
  }
}
