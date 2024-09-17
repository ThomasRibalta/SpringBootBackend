package dev.thomasrba.ulib.controller;

import dev.thomasrba.ulib.config.JwtService;
import dev.thomasrba.ulib.dto.LoginUser;
import dev.thomasrba.ulib.dto.RegisterUser;
import dev.thomasrba.ulib.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/auth")
public class AuthController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;


    @PostMapping(path = "register")
    public void register(@RequestBody RegisterUser registerUser) {
        userService.registerUser(registerUser);;
    }

    @PostMapping(path = "validation")
    public void validation(@RequestBody Map<String, String> activation) {
        userService.validation(activation);
    }

    @PostMapping(path = "login")
    public Map<String, String> login(@RequestBody LoginUser loginUser) {
       final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
               loginUser.email(),
               loginUser.password()
       ));
        if (authenticate.isAuthenticated()) {
            return jwtService.generate(loginUser.email());
        }
        return null;
    }
}
