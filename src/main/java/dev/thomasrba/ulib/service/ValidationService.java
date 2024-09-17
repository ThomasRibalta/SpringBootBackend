package dev.thomasrba.ulib.service;

import dev.thomasrba.ulib.entity.User;
import dev.thomasrba.ulib.entity.Validation;
import dev.thomasrba.ulib.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class ValidationService {

    private final ValidationRepository validationRepository;
    private final MailService mailService;

    public void createValidation(User user) {
        Date now = new Date();
        Date expired = new Date(now.getTime() + 1000 * 60 * 10);
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        Validation validation = new Validation();
        validation.setUser(user);
        validation.setExpire_at(expired);
        validation.setCode(String.valueOf(code));
        validationRepository.save(validation);
        mailService.sendCode(code, user.getEmail());
    }

    public Validation activate(String code) {
        return validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Invalid code"));
    }
}
