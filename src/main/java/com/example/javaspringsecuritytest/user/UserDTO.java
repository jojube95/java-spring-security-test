package com.example.javaspringsecuritytest.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserDTO {
    @NotBlank(message = "Invalid Username: Empty username")
    @NotNull(message = "Invalid Username: Null username")
    @Size(min = 1, max = 32, message = "Invalid Username: Username length between 1-32 characters")
    private String username;

    @NotBlank(message = "Invalid Password: Empty password")
    @NotNull(message = "Invalid Password: Null password")
    @Size(min = 8, max = 16, message = "Invalid Password: Password length between 8-16 characters")
    private String password;

    @Email(message = "Invalid Email: Invalid email")
    @Size(max = 32, message = "Invalid Email: Password length between 0-32 characters")
    private String email;

    public UserDTO(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO userDTO)) return false;
        return Objects.equals(username, userDTO.username) && Objects.equals(password, userDTO.password) && Objects.equals(email, userDTO.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
