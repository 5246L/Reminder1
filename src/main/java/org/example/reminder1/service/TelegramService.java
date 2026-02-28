package org.example.reminder1.service;

import lombok.extern.slf4j.Slf4j;
import org.example.reminder1.dto.TelegramSendMessageRequest;
import org.example.reminder1.entity.Reminder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TelegramService {

    private final String botToken;
    private final RestTemplate restTemplate;

    public TelegramService(
            @Value("${telegram.bot.token}") String botToken,
            RestTemplate restTemplate) {
        this.botToken = botToken;
        this.restTemplate = restTemplate;
    }

    public void sendReminder(Long chatId, String title, String description) {
        if (chatId == null) {
            log.warn("chat_id is null, пропускаем отправку");
            return;
        }

        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            String text = "🔔 " + title + "\n\n" + description;

            restTemplate.postForEntity(
                    url,
                    new TelegramSendMessageRequest(chatId, text),
                    String.class
            );

            log.info("Успешно отправлено на Telegram {}", chatId);

        } catch (Exception e) {
            log.error("Ошибка отправки Telegram: {}", e.getMessage());
            throw new RuntimeException("Не удалось отправить Telegram", e);
        }
    }

    public boolean sendReminderIfPossible(Reminder reminder) {
        if (reminder.getUser().getTelegramChatId() == null) return false;
        try {
            sendReminder(
                    reminder.getUser().getTelegramChatId(),
                    reminder.getTitle(),
                    reminder.getDescription()
            );
            log.info("Telegram отправлен: {}", reminder.getUser().getTelegramChatId());
            return true;
        } catch (Exception e) {
            log.error("Ошибка Email для ID {}: {}", reminder.getId(), e.getMessage());
            return false;
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
