package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventDtoCreation;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.dto.forrequest.EventRequestStatus;
import ru.practicum.ewm.events.dto.forrequest.EventUpdateRequestDto;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestDtoForUpdate;
import ru.practicum.ewm.requests.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping
public class EventController {

    private final EventService eventService;
    private final RequestService requestService;
    private final String usersEventUrl = "users/{userId}/events";
    private final String adminEventUrl = "admin/events";

    @GetMapping(usersEventUrl)
    public List<ShortEventDto> getEventByUser(@PathVariable Long userId,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventService.getEventByUser(userId, page);
    }

    @PostMapping(usersEventUrl)
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable Long userId,
                                        @Valid @RequestBody EventDtoCreation eventDtoCreation) {
        return eventService.createEvent(userId, eventDtoCreation);
    }

    @GetMapping(usersEventUrl + "/{eventId}")
    public EventDto getEventById(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return eventService.getEventByIdByUser(userId, eventId);
    }

    @PatchMapping(usersEventUrl + "/{eventId}")
    public EventDto updateEventByUser(@PathVariable Long userId,
                                                   @PathVariable Long eventId,
                                                   @Valid @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.updateEventByUser(userId, eventId, eventUpdateRequestDto);
    }

    @GetMapping(adminEventUrl)
    public List<EventDto> findEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<EventState> states,
                                            @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        return eventService.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(adminEventUrl + "/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable Long eventId,
                                               @Valid @RequestBody EventUpdateRequestDto eventDtoUpdate) {
        return eventService.updateEventByAdmin(eventId, eventDtoUpdate);
    }

     @GetMapping("/events")
     public List<ShortEventDto> findEvents(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) Boolean paid,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(required = false) Boolean onlyAvailable,
                                             @RequestParam(required = false) String sort,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @PositiveOrZero @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest request) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventService.findEvents(text,categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, page, request);
    }

    @GetMapping("/events/{id}")
    public EventDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        return eventService.findEventById(id,request);
    }

    @GetMapping(usersEventUrl + "/{eventId}/requests")
    public List<RequestDto> findEventRequests(@Positive @PathVariable Long userId,
                                              @Positive @PathVariable Long eventId) {
        return requestService.findByEventIdAndInitiatorId(eventId, userId);
    }

    @PatchMapping(usersEventUrl + "/{eventId}/requests")
    public RequestDtoForUpdate changeRequestStatus(@Positive @PathVariable Long userId,
                                                   @Positive @PathVariable Long eventId,
                                                   @Valid @RequestBody EventRequestStatus eventRequestStatus) {
        return requestService.changeRequestStatus(userId, eventId, eventRequestStatus);
    }
}
