package ru.practicum.ewm.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.service.event.Event;
import ru.practicum.ewm.service.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false, name = "event_id", referencedColumnName = "id")
    Event event;
    @ManyToOne
    @JoinColumn(nullable = false, name = "requester_id", referencedColumnName = "id")
    User requester;
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
