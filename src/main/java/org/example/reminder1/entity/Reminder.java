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
    private Long id;

    private String title;
	private String description;
	private LocalDateTime remind;

    @Column(nullable = false)
    private Boolean notified = false;

    @ManyToOne //(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
	private User user;

}
