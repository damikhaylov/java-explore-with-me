package ru.practicum.ewm.service.event;

import ru.practicum.ewm.service.category.Category;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.dto.NewEventDto;
import ru.practicum.ewm.service.user.User;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toNewEvent(NewEventDto newEventDto, Category category, User initiator) {
        return new Event(
                null,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                category,
                newEventDto.getEventDate(),
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon(),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration(),
                initiator,
                EventState.PENDING,
                LocalDateTime.now(),
                null
        );
    }

    public static EventFullDto toFullDto(Event event, long confirmedRequests, long views) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                new EventFullDto.CategoryDto(
                        event.getCategory().getId(),
                        event.getCategory().getName()),
                event.getEventDate(),
                new EventFullDto.LocationDto(
                        event.getLatitude(),
                        event.getLongitude()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getRequestModeration(),
                new EventFullDto.UserShortDto(
                        event.getInitiator().getId(),
                        event.getInitiator().getName()),
                event.getState(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                confirmedRequests,
                views
        );
    }

    public static EventShortDto toShortDto(Event event, long confirmedRequests, long views) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                new EventShortDto.CategoryDto(
                        event.getCategory().getId(),
                        event.getCategory().getName()),
                event.getEventDate(),
                event.getPaid(),
                new EventShortDto.UserShortDto(
                        event.getInitiator().getId(),
                        event.getInitiator().getName()),
                confirmedRequests,
                views
        );
    }

}
