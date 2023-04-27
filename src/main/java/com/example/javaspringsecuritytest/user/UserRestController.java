package com.example.javaspringsecuritytest.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public User login(@RequestBody User user){
        // TODO
        return new User();
    }

    @PostMapping("/register")
    public User register(@RequestBody User user){
        // TODO
        return new User();
    }

    @GetMapping("/logout")
    public User logout(@RequestBody User user){
        // TODO
        return new User();
    }

    @GetMapping("/getData")
    public User getData(@RequestBody User user){
        // TODO
        return new User();
    }
}
