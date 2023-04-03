package ru.practicum.ewm.events.dto.forrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatus {
    @NotNull
    List<Long> requestIds;
    @NotNull
    String status;
}
