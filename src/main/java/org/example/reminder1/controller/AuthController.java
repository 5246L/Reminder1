package org.example.reminder1.controller;


import lombok.RequiredArgsConstructor;
import org.example.reminder1.dto.AuthSuccessResponse;
import org.example.reminder1.dto.CurrentUserResponse;
import org.example.reminder1.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/success")
    public AuthSuccessResponse loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        return userService.getAuthSuccess(oauth2User);
    }

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(@AuthenticationPrincipal OAuth2User oauth2User) {
        return userService.getCurrentUser(oauth2User);
    }
}
