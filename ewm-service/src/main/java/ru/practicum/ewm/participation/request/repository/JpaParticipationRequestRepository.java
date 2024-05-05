package ru.practicum.ewm.user.participation.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.participation.request.model.ParticipationRequest;

public interface JpaParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
}
