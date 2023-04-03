package ru.practicum.ewm.events.dto.forrequest;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventUpdateRequestDto {
    String annotation;
    Long category;
    String description;
    String eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    String title;
}
