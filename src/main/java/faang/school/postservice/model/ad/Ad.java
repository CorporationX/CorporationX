package faang.school.postservice.model.ad;

import faang.school.postservice.model.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "post_ad")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "buyer_id", nullable = false)
    private long buyerId;

    @Column(name = "appearances_left", nullable = false)
    private long appearancesLeft;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endDate;
}
