package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.utility.stats.EndpointHitsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaStatsServerRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.ewm.utility.stats.EndpointHitsDto(eh.app, eh.uri, count(eh.ip)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.created BETWEEN :start AND :end " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY count(eh.ip) desc")
    public List<EndpointHitsDto> findHitsWithNoUrisList(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.utility.stats.EndpointHitsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit AS eh " +
            "WHERE eh.created BETWEEN :start AND :end " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY count(distinct eh.ip) desc")
    public List<EndpointHitsDto> findDistinctHitsWithNoUrisList(@Param("start") LocalDateTime start,
                                                                @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.utility.stats.EndpointHitsDto(eh.app, eh.uri, count(eh.ip)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.created BETWEEN :start AND :end AND " +
            "eh.uri IN :uris " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY count(eh.ip) desc")
    public List<EndpointHitsDto> findHitsWithUrisList(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end,
                                                      @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.utility.stats.EndpointHitsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.created BETWEEN :start AND :end AND " +
            "eh.uri IN :uris " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY count(distinct eh.ip) desc")
    public List<EndpointHitsDto> findDistinctHitsWithUrisList(@Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end,
                                                              @Param("uris") List<String> uris);
}
