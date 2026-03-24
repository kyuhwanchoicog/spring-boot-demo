package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

class InvalidRequestExceptionTest {

  @Test
  void should_store_errors() {
    Errors errors = mock(Errors.class);
    InvalidRequestException exception = new InvalidRequestException(errors);
    assertSame(errors, exception.getErrors());
  }

  @Test
  void should_extend_runtime_exception() {
    Errors errors = mock(Errors.class);
    InvalidRequestException exception = new InvalidRequestException(errors);
    assertInstanceOf(RuntimeException.class, exception);
    assertEquals("", exception.getMessage());
  }
}
