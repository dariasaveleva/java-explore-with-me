package ru.practicum.ewm.events.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.dto.EndpointHitDtoCreation;
import ru.practicum.ewm.events.LocationRepository;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventDtoCreation;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.ShortEventDto;
import ru.practicum.ewm.events.dto.action.AdminAction;
import ru.practicum.ewm.events.dto.action.UserAction;
import ru.practicum.ewm.events.dto.forrequest.EventUpdateRequestDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.CriteriaApiEvent;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    final StatClient statClient;
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final LocationRepository locationRepository;
    final RequestRepository requestRepository;
    final CriteriaApiEvent criteriaApiEvent;

    @Override
    public List<ShortEventDto> getEventByUser(Long id, PageRequest page) {
        return eventRepository.findAllByInitiatorId(id, page).stream()
                .map(EventMapper::toShortEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto createEvent(Long id, EventDtoCreation eventDtoCreation) {
        if (eventDtoCreation.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Неверная дата");
        }
        User user = checkUserExistence(id);
        Category category = checkCategoryExistence(eventDtoCreation.getCategory());
        eventDtoCreation.setLocation(locationRepository.save(eventDtoCreation.getLocation()));
        Event event = eventRepository.save(EventMapper.toEvent(user, category, eventDtoCreation));
        return EventMapper.toEventDto(event);
    }

    @Override
    public EventDto getEventByIdByUser(Long userId, Long eventId) {
        checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        return EventMapper.toEventDto(event);
    }

    @Override
    @Transactional
    public EventDto updateEventByUser(Long userId, Long eventId, EventUpdateRequestDto eventUpdate) {
        User user = checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Невозможно обновить чужое событие");
        }
        if (eventUpdate.getEventDate() != null) {
            LocalDateTime time = LocalDateTime.parse(eventUpdate.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (LocalDateTime.now().isAfter(time.minusHours(2))) {
                throw new ConflictException("Невозможно создать событие, до начало которого менее 2 часов");
            }
        }
        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Невозможно обновить опубликованное событие");
        }

        if (eventUpdate.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdate.getLocation()));
        }

        if (eventUpdate.getAnnotation() != null) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        if (eventUpdate.getCategory() != null) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        if (eventUpdate.getTitle() != null) {
            event.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getDescription() != null) {
            event.setDescription(eventUpdate.getDescription());
        }

        if (eventUpdate.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventUpdate.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdate.getParticipantLimit());
        }

        if (eventUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdate.getRequestModeration());
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }
        if (eventUpdate.getStateAction().equals(UserAction.SEND_TO_REVIEW.name())) {
            event.setEventState(EventState.PENDING);
        }

        if (eventUpdate.getStateAction().equals(UserAction.CANCEL_REVIEW.name())) {
            event.setEventState(EventState.CANCELED);
        }

        return EventMapper.toEventDto(eventRepository.save(event));

    }

    @Override
    public List<EventDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                            String rangeStart, String rangeEnd,
                                            Integer from,Integer size) {
        return criteriaApiEvent.findEvents(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(EventMapper::toEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto updateEventByAdmin(Long eventId, EventUpdateRequestDto eventUpdate) {
        log.info("Получен запрос на событие с id {}", eventId);
        Event event = checkEventExistence(eventId);
        if (eventUpdate.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdate.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Дата не может быть в прошлом");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdate.getEventDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }

        if (eventUpdate.getAnnotation() != null) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getDescription() != null) {
            event.setDescription(eventUpdate.getDescription());
        }

        if (eventUpdate.getTitle() != null) {
            event.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdate.getParticipantLimit());
        }

        if (eventUpdate.getStateAction() != null) {
            event.setEventState(defineEventState(event, eventUpdate));
        }
        if (eventUpdate.getCategory() != null) {
            event.setCategory(checkCategoryExistence(eventUpdate.getCategory()));
        }

        if (eventUpdate.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdate.getLocation()));
        }

        return EventMapper.toEventDto(eventRepository.save(event));
    }

    @Override
    public List<ShortEventDto> findEvents(String text, List<Long> categories, Boolean paid,
                                             String rangeStart, String rangeEnd,
                                             Boolean onlyAvailable, String sort, Pageable page, HttpServletRequest request) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (text == null) text = "";
        log.info("Найден список событий");
        sendStatistics(request);
        return eventRepository.findByParamsOrderByDate(text.toLowerCase(), List.of(EventState.PUBLISHED),
                categories, paid, start, end, page).stream()
                .map(EventMapper::toShortEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto findEventById(Long id, HttpServletRequest request) {
        Event event = checkEventExistence(id);
        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setConfirmedRequests(requestRepository
                .findAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size());
        sendStatistics(request);
        log.info("Событие найдено");
        return eventDto;
    }

    private EventState defineEventState(Event event, EventUpdateRequestDto eventUpdate) {
        EventState eventState = EventState.PENDING;
        if (event.getEventState() == EventState.PUBLISHED &&
                eventUpdate.getStateAction().equalsIgnoreCase(AdminAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Событие уже опубликовано");
        }

        if (event.getEventState() == EventState.CANCELED &&
                eventUpdate.getStateAction().equalsIgnoreCase(AdminAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Событие отменено");
        }

        if (event.getEventState() == EventState.PUBLISHED &&
                eventUpdate.getStateAction().equalsIgnoreCase(AdminAction.REJECT_EVENT.name())) {
            throw new ConflictException("Невозможно отменить опубликованное событие");
        }

            if (eventUpdate.getStateAction().equals(AdminAction.PUBLISH_EVENT.name())) {
                eventState = EventState.PUBLISHED;
            } else if (eventUpdate.getStateAction().equals(AdminAction.REJECT_EVENT.name())
                    && event.getEventState() != EventState.PUBLISHED) {
                eventState = EventState.CANCELED;
            }

        return eventState;
    }

    private User checkUserExistence(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь, по которому запрашиваются события, не существует");
        });
        return user;
    }

    private Event checkEventExistence(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не существует");
        });
        return event;
    }

    private Category checkCategoryExistence(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            throw new NotFoundException("Категория не существует");
        });
        return category;
    }

    private void sendStatistics(HttpServletRequest request) {
        statClient.post(
                new EndpointHitDtoCreation(
                "ewm",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }
}
