package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.requests.dto.RequestStatus;
import ru.practicum.ewm.requests.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.requester.id = :userId")
    Optional<Request> findByEventIdAndRequesterId(@Param("eventId") Long eventId,
                                                  @Param("userId") Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.event.initiator.id = :initiatorId")
    Optional<Request> findByEventIdAndInitiatorId(@Param("eventId") Long eventId,
                                                  @Param("initiatorId") Long initiatorId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.status = 'CONFIRMED'")
    List<Request> findByEventIdConfirmed(@Param("eventId") Long eventId);

    @Query("select request from Request request " +
            "where request.status = 'CONFIRMED' " +
            "and request.event.id IN (:events)")
    List<Request> findConfirmedToListEvents(@Param("events") List<Long> events);

    @Query("select request from Request request " +
            "where request.event.id = :event " +
            "and request.id IN (:requestIds)")
    List<Request> findByEventIdAndRequestsId(@Param("event") Long eventId,
                                             @Param("requestIds") List<Long> requestIds);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

}
