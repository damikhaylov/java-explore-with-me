package ru.practicum.ewm.service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.service.category.Category;
import ru.practicum.ewm.service.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 3, max = 120)
    @Column(nullable = false, length = 120)
    private String title;
    @NotNull
    @Size(min = 20, max = 2000)
    @Column(nullable = false, length = 2000)
    private String annotation;
    @NotNull
    @Size(min = 20, max = 7000)
    @Column(nullable = false, length = 7000)
    private String description;
    @ManyToOne
    @JoinColumn(nullable = false, name = "category_id", referencedColumnName = "id")
    private Category category;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    private float latitude;
    private float longitude;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Long participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @ManyToOne
    @JoinColumn(nullable = false, name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "created")
    private LocalDateTime createdOn;
    @Column(name = "published")
    private LocalDateTime publishedOn;
}
