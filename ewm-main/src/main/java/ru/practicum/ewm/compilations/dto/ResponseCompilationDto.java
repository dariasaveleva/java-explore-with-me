package ru.practicum.ewm.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.dto.ShortEventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseCompilationDto {
    Long id;
    List<ShortEventDto> events;
    Boolean pinned;
    String title;
}
