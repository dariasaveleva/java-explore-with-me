package ru.practicum.ewm.requests.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @Enumerated(EnumType.STRING)
    RequestStatus status;
}
