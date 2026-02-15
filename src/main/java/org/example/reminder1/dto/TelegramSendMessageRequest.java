package org.example.reminder1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TelegramSendMessageRequest {
    @JsonProperty("chat_id")
    private Long char_id;
    private String text;
}
