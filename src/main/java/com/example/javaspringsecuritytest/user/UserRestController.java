package com.example.javaspringsecuritytest.user;

import com.example.javaspringsecuritytest.authentication.JwtAuthenticationResponse;
import com.example.javaspringsecuritytest.exception.UserAlreadyExistAuthenticationException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class UserRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder encoder;

    public UserRestController(UserService userService, AuthenticationManager authenticationManager, JwtEncoder encoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody @Valid User user){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User localUser = (User) authentication.getPrincipal();
        String jwt = getToken(localUser);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid User user){
        try {
            userService.register(user);
        } catch (UserAlreadyExistAuthenticationException e) {
            return ResponseEntity.badRequest().body("Username already in use!");
        }

        return ResponseEntity.ok().body("Username register successfully!");
    }

    @GetMapping("/getData")
    public ResponseEntity<?> getData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            User user = userService.loadUserByUsername(authentication.getName());
            return ResponseEntity.ok().body(user);
        } catch (UsernameNotFoundException e) {
            return (ResponseEntity<?>) ResponseEntity.badRequest();
        }
    }

    private String getToken(User localUser) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = localUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(localUser.getUsername())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
