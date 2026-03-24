package io.spring.application.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateUserValidatorTest {

  private UpdateUserValidator validator;
  private UserRepository userRepository;
  private ConstraintValidatorContext context;
  private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
  private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
      nodeBuilder;

  @BeforeEach
  public void setUp() throws Exception {
    userRepository = mock(UserRepository.class);
    context = mock(ConstraintValidatorContext.class);
    violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    nodeBuilder =
        mock(
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
                .class);

    when(context.buildConstraintViolationWithTemplate(org.mockito.ArgumentMatchers.anyString()))
        .thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode(org.mockito.ArgumentMatchers.anyString()))
        .thenReturn(nodeBuilder);

    validator = new UpdateUserValidator();

    Field field = UpdateUserValidator.class.getDeclaredField("userRepository");
    field.setAccessible(true);
    field.set(validator, userRepository);
  }

  @Test
  public void should_return_true_when_email_and_username_not_taken() {
    User targetUser = new User("user@example.com", "username", "pass", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("new@example.com").username("newuser").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    Assertions.assertTrue(result);
    verify(context, never()).disableDefaultConstraintViolation();
  }

  @Test
  public void should_return_false_when_email_taken_by_another_user() {
    User targetUser = new User("user@example.com", "username", "pass", "", "");
    User anotherUser = new User("another@example.com", "another", "pass", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("another@example.com").username("newuser").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    boolean result = validator.isValid(command, context);

    Assertions.assertFalse(result);
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  public void should_return_false_when_username_taken_by_another_user() {
    User targetUser = new User("user@example.com", "username", "pass", "", "");
    User anotherUser = new User("another@example.com", "another", "pass", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("new@example.com").username("another").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("another")).thenReturn(Optional.of(anotherUser));

    boolean result = validator.isValid(command, context);

    Assertions.assertFalse(result);
    verify(context).disableDefaultConstraintViolation();
  }

  @Test
  public void should_return_true_when_same_user_owns_email_and_username() {
    User targetUser = new User("user@example.com", "username", "pass", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("user@example.com").username("username").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername("username")).thenReturn(Optional.of(targetUser));

    boolean result = validator.isValid(command, context);

    Assertions.assertTrue(result);
  }
}
