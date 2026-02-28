package org.example.reminder1.entity;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
	private String description;

    @Column(name = "remind")
	private LocalDateTime remind;

    @Column(name = "notified", nullable = false)
    private Boolean notified = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
	private User user;

}
