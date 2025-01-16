package faang.school.urlshortenerservice.entiity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "url")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_value", referencedColumnName = "value", nullable = false)
    private Hash hash;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "visits_count")
    private Long visitsCount;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        expiresAt = createdAt.plusYears(1);
        visitsCount = 0L;
    }

    public Url() {}

    public Url(Hash hash, String originalUrl) {
        this.hash = hash;
        this.originalUrl = originalUrl;
    }
}