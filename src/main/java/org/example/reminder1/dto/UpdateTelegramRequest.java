package org.example.reminder1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTelegramRequest {

    @NotNull(message = "Telegram ID обязателен")
    private Long telegramChatId;
}
