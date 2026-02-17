package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reminder1.dto.TelegramSendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final RestTemplate restTemplate;

    public void sendReminder(Long chatId, String title, String description) {
        if (chatId == null) {
            log.warn("chat_id is null, пропускаем отправку");
            return;
        }

        try {
            String text = "🔔 " + title + "\n\n" + description;

            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage",
                    botToken
            );

            TelegramSendMessageRequest request = new TelegramSendMessageRequest(chatId, text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TelegramSendMessageRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

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
