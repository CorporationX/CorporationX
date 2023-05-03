package school.faang.user_service.entity.event;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rating")
public class Rating {

    private final static int RATE_MIN_VALUE = 0;
    private final static int RATE_MAX_VALUE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "comment", length = 256, unique = true)
    private String comment;

    @Min(RATE_MIN_VALUE)
    @Max(RATE_MAX_VALUE)
    @Column(name = "rate", nullable = false)
    private int rate;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    public Event event;
}