package com.example.javaspringsecuritytest.user;

import com.example.javaspringsecuritytest.exception.UserAlreadyExistAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    UserDTO userDTO;
    User user;

    @BeforeEach
    public void initEach(){
        userDTO = new UserDTO("username", "password", "email");
        user = new User("username", "password", "email");
    }

    @Test
    void loadUserByUsernameCallFindUserByUsernameTest() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        userService.loadUserByUsername(user.getUsername());

        verify(userRepository, times(1)).findUserByUsername(user.getUsername());
    }

    @Test
    void loadUserByUsernameReturnsUserTest() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        assertEquals(user, userService.loadUserByUsername(user.getUsername()));
    }

    @Test()
    void loadUserByUsernameUserNotExistTest() {
        String username = user.getUsername();

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
                userService.loadUserByUsername(username);
        });

        assertEquals("Username username does not exist", exception.getMessage());
    }

    @Test
    void registerCallExistsUserByUsernameTest() {
        userService.register(userDTO);

        verify(userRepository, times(1)).existsUserByUsername(user.getUsername());
    }

    @Test
    void registerCallSaveTest() {
        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.register(userDTO);

        user.setPassword("encodedPassword");

        verify(userRepository, times(1)).save(user);
    }

    @Test()
    void registerUserAlreadyExistTest() {
        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistAuthenticationException.class, () -> {
            userService.register(userDTO);
        });

        assertEquals("User with username username already exist", exception.getMessage());
    }
}
