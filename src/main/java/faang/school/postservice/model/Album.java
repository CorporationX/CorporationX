package faang.school.postservice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Column(name = "description", nullable = false, length = 4096)
    private String description;

    @Column(name = "author_id", nullable = false)
    private long authorId;

    @ManyToMany
    @JoinTable(name = "post_album", joinColumns = @JoinColumn(name = "album_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> posts;

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlbumVisibility visibility;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "album_selected_users", joinColumns = @JoinColumn(name = "album_id"))
    @Column(name = "selected_user_id", nullable = false)
    private List<Long> selectedUserIds;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addPost(Post post) {
        posts.add(post);
    }

    public void removePost(long postId) {
        posts.removeIf(post -> post.getId() == postId);
    }
}
