package org.example.reminder1.util;

import lombok.RequiredArgsConstructor;
import org.example.reminder1.entity.User;
import org.example.reminder1.service.UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2UserResolver {

    private final UserService userService;

    public User resolve(OAuth2User oauth2User) {
        String googleId = oauth2User.getAttribute("sub");
        return userService.getUserByGoogleId(googleId);
    }
}
