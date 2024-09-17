package dev.thomasrba.ulib.service;

import dev.thomasrba.ulib.dto.RegisterUser;
import dev.thomasrba.ulib.entity.Role;
import dev.thomasrba.ulib.entity.User;
import dev.thomasrba.ulib.entity.Validation;
import dev.thomasrba.ulib.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ValidationService validationService;

    public void registerUser(RegisterUser registerUser) {
        String regexMail = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regexMail);
        Matcher matcher = pattern.matcher(registerUser.email());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
        Optional<User> userOptional = userRepository.findByEmail(registerUser.email());
        if (userOptional.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        var role = new Role();
        role.setName("ROLE_USER");

        var user = User.builder()
                .email(registerUser.email())
                .password(passwordEncoder.encode(registerUser.password()))
                .role(role)
                .active(false)
                .build();

        userRepository.save(user);
        validationService.createValidation(user);
    }

    public void validation(Map<String, String> validation) {
        Validation validation_code = validationService.activate(validation.get("token"));
        if (Instant.now().isAfter(validation_code.getExpire_at().toInstant()))
            throw new RuntimeException("Expired token");
        User user = userRepository.findByEmail(validation.get("email")).orElseThrow(() -> new RuntimeException("Invalid user"));
        if (validation.get("email") == null || !validation.get("email").equals(user.getEmail()))
            throw new RuntimeException("Invalid email");
        user.setActive(true);
        userRepository.save(user);
    }

}
