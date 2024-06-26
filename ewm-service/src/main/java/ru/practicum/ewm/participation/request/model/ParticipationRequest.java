package ru.practicum.ewm.participation.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.participation.request.model.enums.ParticipationStatus;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "participation_requests")
@Getter
@Setter
@NoArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    @ToString.Exclude
    private User requester;
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequest that = (ParticipationRequest) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
