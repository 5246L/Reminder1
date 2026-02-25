package org.example.reminder1.mapper;

import org.example.reminder1.dto.UserProfileResponse;
import org.example.reminder1.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getTelegramChatId()
        );
    }
}
