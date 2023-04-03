package ru.practicum.ewm.events.dto.forrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.requests.dto.RequestDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestResult {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
