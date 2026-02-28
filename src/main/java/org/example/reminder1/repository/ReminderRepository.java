package org.example.reminder1.repository;

import org.example.reminder1.entity.Reminder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long>, JpaSpecificationExecutor<Reminder> {
    List<Reminder> findAllByUserId(Long userId);
    List<Reminder> findAllByUserId(Long userId, Sort sort);
    List<Reminder> findByUserIdAndRemindBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<Reminder> findByUserIdAndRemindAfter(Long userId, LocalDateTime date);
    List<Reminder> findByUserIdAndRemindBefore(Long userId, LocalDateTime date);
    List<Reminder> findAllByRemindBeforeAndNotifiedFalse(LocalDateTime remind);

    @Query("SELECT r FROM Reminder r WHERE r.user.id = : userId " +
            "AND (:from IS NULL OR r.remind >= :from) " +
            "AND (:to IS NULL OR r.remind <= :to)"
    )
    List<Reminder> findByUserIdAndOptionalDateRange(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
