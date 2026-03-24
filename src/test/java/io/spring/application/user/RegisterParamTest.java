package io.spring.application.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegisterParamTest {

  @Test
  public void should_construct_with_all_args_and_exercise_getters() {
    RegisterParam param = new RegisterParam("test@example.com", "testuser", "password123");

    Assertions.assertEquals("test@example.com", param.getEmail());
    Assertions.assertEquals("testuser", param.getUsername());
    Assertions.assertEquals("password123", param.getPassword());
  }

  @Test
  public void should_construct_with_no_args() {
    RegisterParam param = new RegisterParam();

    Assertions.assertNull(param.getEmail());
    Assertions.assertNull(param.getUsername());
    Assertions.assertNull(param.getPassword());
  }
}
