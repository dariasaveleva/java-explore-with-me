package ru.practicum.ewm.events.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventDtoCreation;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.dto.forrequest.EventUpdateRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<ShortEventDto> getEventByUser(Long id, PageRequest page);

    EventDto createEvent(Long id, EventDtoCreation eventDtoCreation);

    EventDto getEventByIdByUser(Long userId, Long eventId);

    EventDto updateEventByUser(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto);

    List<EventDto> findEventsByAdmin(List<Long> users,
                                             List<EventState> states,
                                             List<Long> categories,
                                             String rangeStart,
                                             String  rangeEnd,
                                             Integer from,Integer size);

    EventDto updateEventByAdmin(Long eventId, EventUpdateRequestDto eventUpdateRequestDto);

    List<ShortEventDto> findEvents(String text, List<Long> categories,
                                      Boolean paid, String rangeStart,
                                      String rangeEnd, Boolean onlyAvailable,
                                      String sort, Pageable page);

    EventDto findEventById(Long id, HttpServletRequest request);
}
