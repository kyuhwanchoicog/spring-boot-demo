package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FieldErrorResourceTest {

  @Test
  void should_create_and_return_all_fields() {
    FieldErrorResource resource =
        new FieldErrorResource("Article", "title", "NotBlank", "can't be empty");
    assertEquals("Article", resource.getResource());
    assertEquals("title", resource.getField());
    assertEquals("NotBlank", resource.getCode());
    assertEquals("can't be empty", resource.getMessage());
  }

  @Test
  void should_handle_null_values() {
    FieldErrorResource resource = new FieldErrorResource(null, null, null, null);
    assertNull(resource.getResource());
    assertNull(resource.getField());
    assertNull(resource.getCode());
    assertNull(resource.getMessage());
  }
}
