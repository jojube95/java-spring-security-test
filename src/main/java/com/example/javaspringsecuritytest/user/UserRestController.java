package com.example.javaspringsecuritytest.user;

import com.example.javaspringsecuritytest.authentication.JwtAuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserRestController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody @Valid User user){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User localUser = (User) authentication.getPrincipal();
        String jwt = userService.getToken(localUser);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid User user){
        userService.register(user);
        return ResponseEntity.ok().body("Username register successfully!");
    }

    @GetMapping("/getData")
    public ResponseEntity<?> getData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        return ResponseEntity.ok().body(user);
    }
}
