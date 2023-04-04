package ru.practicum.ewm.requests.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.forrequest.EventRequestStatus;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestDtoForUpdate;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.mapper.RequestsMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {

     RequestRepository requestRepository;
     UserRepository userRepository;
     EventRepository eventRepository;

    @Override
    public List<RequestDto> findByRequesterId(Long userId) {
        log.info("Найдены события для пользователя с id {}", userId);
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestsMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> findByEventIdAndInitiatorId(Long eventId, Long userId) {
        log.info("Найдены события с id {} для пользователя с id {} ", eventId, userId);
        return requestRepository.findByEventIdAndInitiatorId(eventId, userId).stream()
                .map(RequestsMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
       User user = checkUserExistence(userId);
       Event event = checkEventExistence(eventId);

       if (event.getEventState() != EventState.PUBLISHED) {
           throw new ConflictException("Данное событие не опубликовано");
       }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Невозможно отправить запрос на своё мероприятие");
        }

        int confirmedRequests = requestRepository.findByEventIdConfirmed(eventId).size();

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Нет свободных мест на данное мероприятие");
        }
        RequestStatus status = RequestStatus.PENDING;

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        Request request = new Request(null,
                LocalDateTime.now(),
                event,
                user,
                status);

        Optional<Request> check = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (check.isPresent()) throw new ConflictException("Вы уже подали запрос на это событие");
        request = requestRepository.save(request);
        log.info("Создан запрос на событие с id {}", eventId);
        return RequestsMapper.toRequestDto(request);

    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = checkRequestExistence(requestId, userId);

        request.setStatus(RequestStatus.CANCELED);
        log.info("Запрос с id {} отменен", request.getId());
        return RequestsMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDtoForUpdate changeRequestStatus(Long userId,
                                                   Long eventId,
                                                   EventRequestStatus eventRequestStatus) {
        checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("У вас нет события с id {}" + eventId);
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Подтверждение не требуется");
        }

        RequestDtoForUpdate requestDtoForUpdate = new RequestDtoForUpdate(new ArrayList<>(), new ArrayList<>());

        Integer confirmedRequests = requestRepository.findByEventIdConfirmed(eventId).size();

        List<Request> requests = requestRepository
                .findByEventIdAndRequestsId(eventId, eventRequestStatus.getRequestIds());

        if (eventRequestStatus.getStatus().equals(RequestStatus.CONFIRMED.name())
            && confirmedRequests + requests.size() > event.getParticipantLimit()) {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            List<RequestDto> requestDtos = requests.stream()
                    .map(RequestsMapper::toRequestDto).collect(Collectors.toList());
            requestDtoForUpdate.setRejectedRequests(requestDtos);

            requestRepository.saveAll(requests);
            throw new ConflictException("Превышено максимальное количество запросов");
        }

        if (eventRequestStatus.getStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            requests.forEach(r -> {
                if (r.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new ConflictException("Невозможно отклонить уже подтвержденные заявки");
                }
                r.setStatus(RequestStatus.REJECTED);
            });

            List<RequestDto> requestDtos = requests.stream().map(RequestsMapper::toRequestDto).collect(Collectors.toList());
            requestDtoForUpdate.setRejectedRequests(requestDtos);
            requestRepository.saveAll(requests);
        } else if (eventRequestStatus.getStatus().equalsIgnoreCase(RequestStatus.CONFIRMED.name())
                && eventRequestStatus.getRequestIds().size() <= event.getParticipantLimit() - confirmedRequests) {
            requests.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));

            List<RequestDto> requestDtos = requests.stream().map(RequestsMapper::toRequestDto).collect(Collectors.toList());
            requestDtoForUpdate.setConfirmedRequests(requestDtos);
            requestRepository.saveAll(requests);
        }
        return requestDtoForUpdate;
    }

    public User checkUserExistence(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь, по которому запрашиваются события, не существует");
        });
        return user;
    }

    public Event checkEventExistence(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Событие не существует");
        });
        return event;
    }

    public Request checkRequestExistence(Long requestId, Long userId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> {
            throw new NotFoundException("Запрос не существует");
        });
        return request;
    }
}
