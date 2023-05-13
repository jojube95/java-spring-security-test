package com.example.javaspringsecuritytest.user;

import com.example.javaspringsecuritytest.config.SecurityConfig;
import com.example.javaspringsecuritytest.exception.UserAlreadyExistAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
@Import(SecurityConfig.class)
class UserRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void registerValidUserTest() throws Exception {
        UserDTO user = new UserDTO("username", "password", "email@email.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((content().string("Username register successfully!")));

        verify(userService, times(1)).register(user);
    }

    @Test
    void registerValidUserExistsTest() throws Exception {
        UserDTO user = new UserDTO("username", "password", "email@email.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        doThrow(new UserAlreadyExistAuthenticationException("User with username " + user.getUsername() + " already exist")).when(userService).register(user);

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect((content().string("An error occurred: User with username username already exist")));

        verify(userService, times(1)).register(user);
    }

    @Test
    void registerInvalidUserTest() throws Exception {
        UserDTO user = new UserDTO(null, "pass", "email");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect((content().string(containsString("Invalid Username: Null username"))))
                .andExpect((content().string(containsString("Invalid Username: Empty username"))))
                .andExpect((content().string(containsString("Invalid Password: Password length between 8-16 characters"))))
                .andExpect((content().string(containsString("Invalid Email: Invalid email"))));

        verify(userService, times(0)).register(user);
    }

    @Test
    @WithMockUser(username="username")
    void getDataAuthenticatedUserTest() throws Exception {
        User user = new User("username", "password", "email@email.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/getData");
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((content().string(containsString("\"username\":\"username\",\"password\":\"password\",\"email\":\"email@email.com\""))));

        verify(userService, times(1)).loadUserByUsername(user.getUsername());
    }

    @Test
    @WithMockUser(username="username")
    void getDataAuthenticatedUserNotExistTest() throws Exception {
        User user = new User("username", "password", "email@email.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/getData");
        when(userService.loadUserByUsername(user.getUsername())).thenThrow(new UsernameNotFoundException("Username " + user.getUsername() + " does not exist"));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect((content().string("An error occurred: Username username does not exist")));

        verify(userService, times(1)).loadUserByUsername(user.getUsername());
    }

    @Test
    void getDataUnauthenticatedUserTest() throws Exception {
        User user = new User("username", "password", "email@email.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/getData");
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is(401));

        verify(userService, times(0)).loadUserByUsername(user.getUsername());
    }

    @Test
    void loginWrongCredentialsTest() throws Exception {
        User user = new User("username", "password", "email@email.com");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()))).thenThrow(BadCredentialsException.class);
        when(userService.loadUserByUsername(user.getUsername())).thenThrow(new UsernameNotFoundException("Username " + user.getUsername() + " does not exist"));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect((content().string("An error occurred: Wrong credentials")));
    }

    @Test
    void loginInvalidUserTest() throws Exception {
        User user = new User(null, "pass", "email");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect((content().string(containsString("Invalid Username: Null username"))))
                .andExpect((content().string(containsString("Invalid Username: Empty username"))))
                .andExpect((content().string(containsString("Invalid Password: Password length between 8-16 characters"))))
                .andExpect((content().string(containsString("Invalid Email: Invalid email"))));
    }

    @Test
    void loginAuthenticatedTest() throws Exception {
        User user = new User("username", "password", "email@email.com");
        Authentication auth = Mockito.mock(Authentication.class);
        when(userService.getToken(user)).thenReturn("token");
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        this.mockMvc.perform(mockRequest)
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect((content().string("{\"accessToken\":\"token\"}")));
    }
}
