package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long id, Pageable page);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    @Query("select event from Event event " +
            "where event.id IN (:ids)")
    List<Event> findByIds(@Param("ids") List<Long> ids);

    @Query("select event from Event AS event " +
            "where (:text IS NULL " +
            "OR LOWER(event.description) like concat('%', :text, '%') " +
            "OR LOWER(event.annotation) like concat('%', :text, '%')" +
            ")" +
            "AND (:states is NULL OR event.eventState in (:states)) " +
            "AND (:categories is NULL OR event.category.id in (:categories)) " +
            "AND (:paid is NULL OR event.paid = :paid) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR event.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR event.eventDate <= :rangeEnd) " +
            "order by event.eventDate")
    List<Event> findByParamsOrderByDate(
            @Param("text") String text,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
            );
}
