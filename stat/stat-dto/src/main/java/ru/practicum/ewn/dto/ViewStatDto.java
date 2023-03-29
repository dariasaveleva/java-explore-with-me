package ru.practicum.ewn.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatDto {
    String app;
    String uri;
    Long hits;
}
