package org.example.reminder1.mapper;

import org.example.reminder1.dto.ReminderCreateRequest;
import org.example.reminder1.dto.ReminderResponse;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "notified", constant = "false")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "remind", source = "request.remind")
    @Mapping(target = "user", source = "user")
    Reminder toEntity(ReminderCreateRequest request, User user);

    ReminderResponse toResponse(Reminder reminder);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "notified", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Reminder reminder, ReminderCreateRequest request);
}
