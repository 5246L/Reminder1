package org.example.reminder1.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/success")
    public Map<String, Object> loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        return Map.of(
                "message", "Авторизация успешна!",
                "name", oauth2User.getAttribute("name"),
                "email", oauth2User.getAttribute("email")
        );
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User oauth2User) {
        return Map.of(
                "name", oauth2User.getAttribute("name"),
                "email", oauth2User.getAttribute("email"),
                "picture", oauth2User.getAttribute("picture")
        );
    }
}
