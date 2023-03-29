package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.model.Stat(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from EndpointHit hit " +
            "where hit.timestamp >= :start " +
            "and hit.timestamp <= :end " +
            "and hit.uri IN (:uris) " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<Stat> getUniqueStat(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             @Param("uris") List<String> uris);

    @Query("select new ru.practicum.ewm.model.Stat(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHit hit " +
            "where hit.timestamp >= :start " +
            "and hit.timestamp <= :end " +
            "and hit.uri IN (:uris) " +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc")
    List<Stat> getNotUniqueStat(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end,
                                @Param("uris") List<String> uris);

}