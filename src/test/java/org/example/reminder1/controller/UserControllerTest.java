package org.example.reminder1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.reminder1.dto.UpdateEmailRequest;
import org.example.reminder1.dto.UpdateTelegramRequest;
import org.example.reminder1.entity.User;
import org.example.reminder1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Очистить БД
        userRepository.deleteAll();

        // Создать тестового пользователя
        testUser = new User();
        testUser.setGoogleId("test-google-id-123");
        testUser.setEmail("testuser@example.com");
        testUser.setTelegramChatId(null);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testGetProfile_Success() throws Exception {
        User user = new User();
        user.setGoogleId("test-google-id");
        user.setEmail("test@example.com");
        userRepository.save(user);

        mockMvc.perform(get("/api/v1/user/profile")
                        .with(oauth2Login()
                                .attributes(attrs -> attrs.put("sub", "test-google-id"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateEmail_Success() throws Exception {
        // Тест: обновление email
        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setEmail("newemail@example.com");

        mockMvc.perform(put("/api/v1/user/profile/email")
                        .with(oauth2Login()
                                .attributes(attrs -> attrs.put("sub", "test-google-id-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));

        // Проверить что email обновился в БД
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getEmail().equals("newemail@example.com");
    }

    @Test
    void testUpdateEmail_InvalidEmail() throws Exception {
        // Тест: невалидный email
        UpdateEmailRequest request = new UpdateEmailRequest();
        request.setEmail("invalid-email"); // Нет @

        mockMvc.perform(put("/api/v1/user/profile/email")
                        .with(oauth2Login()
                                .attributes(attrs -> attrs.put("sub", "test-google-id-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Validation error
    }

    @Test
    void testUpdateTelegram_Success() throws Exception {
        // Тест: привязка Telegram
        UpdateTelegramRequest request = new UpdateTelegramRequest();
        request.setTelegramChatId(123456789L);

        mockMvc.perform(put("/api/v1/user/profile/telegram")
                        .with(oauth2Login()
                                .attributes(attrs -> attrs.put("sub", "test-google-id-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramChatId").value(123456789));

        // Проверить что telegram_chat_id сохранился в БД
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getTelegramChatId().equals(123456789L);
    }

    @Test
    void testRemoveTelegram_Success() throws Exception {
        // Сначала установить telegram_chat_id
        testUser.setTelegramChatId(987654321L);
        userRepository.save(testUser);

        // Тест: отвязка Telegram
        mockMvc.perform(delete("/api/v1/user/profile/telegram")
                        .with(oauth2Login()
                                .attributes(attrs -> attrs.put("sub", "test-google-id-123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramChatId").isEmpty());

        // Проверить что telegram_chat_id удалён из БД
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assert updatedUser.getTelegramChatId() == null;
    }
}