package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

class CustomizeExceptionHandlerTest {

  private CustomizeExceptionHandler handler;
  private WebRequest webRequest;

  @BeforeEach
  void setUp() {
    handler = new CustomizeExceptionHandler();
    webRequest = mock(WebRequest.class);
  }

  @Test
  void should_handle_invalid_request_with_field_errors() {
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");
    bindingResult.addError(new FieldError("user", "email", "duplicated email"));
    InvalidRequestException exception = new InvalidRequestException(bindingResult);

    ResponseEntity<Object> response = handler.handleInvalidRequest(exception, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertInstanceOf(ErrorResource.class, response.getBody());
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertEquals(1, errorResource.getFieldErrors().size());
    assertEquals("email", errorResource.getFieldErrors().get(0).getField());
    assertEquals("duplicated email", errorResource.getFieldErrors().get(0).getMessage());
  }

  @Test
  void should_handle_invalid_request_with_multiple_field_errors() {
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "user");
    bindingResult.addError(new FieldError("user", "email", "duplicated email"));
    bindingResult.addError(new FieldError("user", "username", "can't be empty"));
    InvalidRequestException exception = new InvalidRequestException(bindingResult);

    ResponseEntity<Object> response = handler.handleInvalidRequest(exception, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertEquals(2, errorResource.getFieldErrors().size());
  }

  @Test
  void should_handle_invalid_authentication() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();

    ResponseEntity<Object> response = handler.handleInvalidAuthentication(exception, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    @SuppressWarnings("unchecked")
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("invalid email or password", body.get("message"));
  }

  @Test
  void should_handle_method_argument_not_valid() throws Exception {
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "article");
    bindingResult.addError(new FieldError("article", "title", "must not be blank"));
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<Object> response =
        handler.handleMethodArgumentNotValid(
            exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertInstanceOf(ErrorResource.class, response.getBody());
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertEquals(1, errorResource.getFieldErrors().size());
    assertEquals("title", errorResource.getFieldErrors().get(0).getField());
    assertEquals("must not be blank", errorResource.getFieldErrors().get(0).getMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  void should_handle_constraint_violation() {
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    Path path = mock(Path.class);
    ConstraintDescriptor<?> descriptor = mock(ConstraintDescriptor.class);

    when(path.toString()).thenReturn("method.param.field");
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getMessage()).thenReturn("must not be blank");
    Annotation annotation = mock(Annotation.class);
    when((Class) annotation.annotationType()).thenReturn(Override.class);
    doReturn(annotation).when(descriptor).getAnnotation();
    doReturn(descriptor).when(violation).getConstraintDescriptor();

    ConstraintViolationException exception =
        new ConstraintViolationException(new HashSet<>(Collections.singletonList(violation)));

    ErrorResource result = handler.handleConstraintViolation(exception, webRequest);

    assertNotNull(result);
    assertEquals(1, result.getFieldErrors().size());
    assertEquals("field", result.getFieldErrors().get(0).getField());
    assertEquals("must not be blank", result.getFieldErrors().get(0).getMessage());
  }

  @Test
  @SuppressWarnings("unchecked")
  void should_handle_constraint_violation_with_single_path() {
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    Path path = mock(Path.class);
    ConstraintDescriptor<?> descriptor = mock(ConstraintDescriptor.class);

    when(path.toString()).thenReturn("fieldName");
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getMessage()).thenReturn("is required");
    Annotation annotation = mock(Annotation.class);
    when((Class) annotation.annotationType()).thenReturn(Override.class);
    doReturn(annotation).when(descriptor).getAnnotation();
    doReturn(descriptor).when(violation).getConstraintDescriptor();

    ConstraintViolationException exception =
        new ConstraintViolationException(new HashSet<>(Collections.singletonList(violation)));

    ErrorResource result = handler.handleConstraintViolation(exception, webRequest);

    assertNotNull(result);
    assertEquals(1, result.getFieldErrors().size());
    assertEquals("fieldName", result.getFieldErrors().get(0).getField());
  }

  @Test
  @SuppressWarnings("unchecked")
  void should_handle_constraint_violation_with_deep_path() {
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    Path path = mock(Path.class);
    ConstraintDescriptor<?> descriptor = mock(ConstraintDescriptor.class);

    when(path.toString()).thenReturn("controller.method.nested.field");
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getRootBeanClass()).thenReturn((Class) String.class);
    when(violation.getMessage()).thenReturn("invalid");
    Annotation annotation = mock(Annotation.class);
    when((Class) annotation.annotationType()).thenReturn(Override.class);
    doReturn(annotation).when(descriptor).getAnnotation();
    doReturn(descriptor).when(violation).getConstraintDescriptor();

    ConstraintViolationException exception =
        new ConstraintViolationException(new HashSet<>(Collections.singletonList(violation)));

    ErrorResource result = handler.handleConstraintViolation(exception, webRequest);

    assertNotNull(result);
    assertEquals(1, result.getFieldErrors().size());
    assertEquals("nested.field", result.getFieldErrors().get(0).getField());
  }
}
