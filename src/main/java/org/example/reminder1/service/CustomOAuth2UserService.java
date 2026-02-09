package org.example.reminder1.service;


import lombok.RequiredArgsConstructor;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2IntrospectionException {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");

        Optional<User> userOptional = userRepository.findByGoogleId(googleId);

        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
        }else {
            user = new User();
            user.setGoogleId(googleId);
            user.setEmail(oAuth2User.getAttribute("email"));
            user = userRepository.save(user);
        }

        return oAuth2User;
    }
}
