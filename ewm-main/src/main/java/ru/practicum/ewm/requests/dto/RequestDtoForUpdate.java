package ru.practicum.ewm.requests.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDtoForUpdate {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
