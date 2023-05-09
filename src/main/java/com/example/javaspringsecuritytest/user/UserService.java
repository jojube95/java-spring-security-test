package com.example.javaspringsecuritytest.user;

import com.example.javaspringsecuritytest.exception.UserAlreadyExistAuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + "does not exist");
        }
        else{
            return user;
        }
    }

    public void register(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new UserAlreadyExistAuthenticationException("User with username " + user.getUsername() + " already exist");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
