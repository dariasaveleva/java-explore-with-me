package ru.practicum.ewn.dto;

import lombok.*;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {
    Long id;
    @NotNull
    @NotBlank
    String app;
    @NotNull
    @NotBlank
    String uri;
    @NotBlank
    String ip;
    @NotNull
    LocalDateTime timestamp;
}
