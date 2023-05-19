package com.example.javaspringsecuritytest.user;

import com.example.javaspringsecuritytest.exception.UserAlreadyExistAuthenticationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder encoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder encoder, ModelMapper modelMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " does not exist");
        }
        else{
            return user;
        }
    }

    public void register(UserDTO user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new UserAlreadyExistAuthenticationException("User with username " + user.getUsername() + " already exist");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userBD = this.modelMapper.map(user, User.class);
        userRepository.save(userBD);
    }

    public String getToken(User localUser) {
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
