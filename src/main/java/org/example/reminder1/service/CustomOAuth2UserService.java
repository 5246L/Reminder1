package org.example.reminder1.service;


import lombok.RequiredArgsConstructor;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String ATTR_SUB = "sub";
    private static final String ATTR_EMAIL = "email";

    private final UserRepository userRepository;
    private final UserService userService;

    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        String googleId = oAuth2User.getAttribute(ATTR_SUB);
        String email = oAuth2User.getAttribute(ATTR_EMAIL);

        Optional<User> userOptional = userRepository.findByGoogleId(googleId);

        if (userOptional.isEmpty()) {
            User user = new User();
            user.setGoogleId(googleId);
            user.setEmail(email);
            userService.createUser(user);
        }

        return oAuth2User;
    }
}
