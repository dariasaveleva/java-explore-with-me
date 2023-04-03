package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor_= @Autowired)
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService service;

    @GetMapping
    public List<RequestDto> findRequests(@Positive @PathVariable Long userId) {
        return service.findByRequesterId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequests(@Positive @PathVariable Long userId,
                                     @Positive @RequestParam Long eventId) {
        return service.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@Positive @PathVariable Long userId,
                                    @Positive @PathVariable Long requestId) {
        return service.cancelRequest(userId, requestId);
    }


}
