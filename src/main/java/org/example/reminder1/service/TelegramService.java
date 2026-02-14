package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate;

    public void sendReminder(Long chatId, String title, String description) {
        try {
            String text = "🔔 " + title + "\n\n" + description;
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%d&text=%s",
                    botToken,
                    chatId,
                    encodedText  // ← Закодированный текст
            );
            restTemplate.getForObject(url, String.class);

            log.info("Успешно отправлено на Telegram {}", chatId);

        } catch (Exception e) {
            log.error("Ошибка отправки Telegram: {}", e.getMessage());
            throw new RuntimeException("Не удалось отправить Telegram", e);
        }
    }

    public String getUpdates() {
        String url = String.format(
          "https://api.telegram.org/bot%s/getUpdates",
          botToken
        );

        return restTemplate.getForObject(url, String.class);
    }
}
