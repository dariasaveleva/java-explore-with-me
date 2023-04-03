package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.events.dto.forrequest.EventRequestStatus;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestDtoForUpdate;

import java.util.List;

public interface RequestService {
    List<RequestDto> findByRequesterId(Long userId);

    List<RequestDto> findByEventIdAndInitiatorId(Long eventId, Long userId);

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    RequestDtoForUpdate changeRequestStatus(Long userId, Long eventId, EventRequestStatus eventRequestStatus);


}
