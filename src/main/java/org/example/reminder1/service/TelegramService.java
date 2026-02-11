package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendReminder(Long chatId, String title, String description) {
        try {
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%d&text=%s",
                    botToken,
                    chatId,
                    "🔔 " + title + "\n\n" + description
            );

            restTemplate.getForObject(url, String.class);

            log.info("Успешно отправлено на Telegram {}", chatId);

        } catch (Exception e) {
            log.error("Ошибка отправки Telegram: {}", e.getMessage());
            throw new RuntimeException("Не удалось отправить Telegram", e);
        }
    }
}
