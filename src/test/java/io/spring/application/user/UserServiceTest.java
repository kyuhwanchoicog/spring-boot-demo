package io.spring.application.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    userRepository = Mockito.mock(UserRepository.class);
    passwordEncoder = Mockito.mock(PasswordEncoder.class);
    userService = new UserService(userRepository, "https://default-image.png", passwordEncoder);
  }

  @Test
  public void should_create_user_with_encoded_password() {
    when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

    RegisterParam registerParam =
        new RegisterParam("user@example.com", "username", "plainPassword");

    User user = userService.createUser(registerParam);

    Assertions.assertEquals("user@example.com", user.getEmail());
    Assertions.assertEquals("username", user.getUsername());
    Assertions.assertEquals("encodedPassword", user.getPassword());
    Assertions.assertEquals("", user.getBio());
    Assertions.assertEquals("https://default-image.png", user.getImage());
    verify(userRepository).save(any(User.class));
  }

  @Test
  public void should_update_user_fields() {
    User targetUser = new User("old@example.com", "olduser", "oldpass", "old bio", "old.png");

    UpdateUserParam updateParam =
        UpdateUserParam.builder()
            .email("new@example.com")
            .username("newuser")
            .password("newpass")
            .bio("new bio")
            .image("new.png")
            .build();

    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateParam);

    userService.updateUser(command);

    Assertions.assertEquals("new@example.com", targetUser.getEmail());
    Assertions.assertEquals("newuser", targetUser.getUsername());
    Assertions.assertEquals("newpass", targetUser.getPassword());
    Assertions.assertEquals("new bio", targetUser.getBio());
    Assertions.assertEquals("new.png", targetUser.getImage());
    verify(userRepository).save(targetUser);
  }
}
