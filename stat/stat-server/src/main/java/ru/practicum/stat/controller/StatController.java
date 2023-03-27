package ru.practicum.stat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewn.dto.EndpointHitDto;
import ru.practicum.ewn.dto.ViewStatDto;
import ru.practicum.stat.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
public class StatController {
    private final StatService service;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/stat")
    public List<ViewStatDto> getStat(@RequestParam(required = false) String start,
                                     @RequestParam(required = false) String end,
                                     @RequestParam List<String> uris,
                                     @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStat(LocalDateTime.parse(start, DATE_FORMAT),
                               LocalDateTime.parse(end, DATE_FORMAT), uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto create(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        return service.create(endpointHitDto);
    }

}
