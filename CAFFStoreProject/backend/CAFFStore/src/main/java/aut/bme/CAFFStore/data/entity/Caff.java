package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "caff")
public class Caff {

    @Id
    @GenericGenerator(name = "sequence_uuid", strategy = "aut.bme.CAFFStore.data.util.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sequence_uuid")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Getter
    @Setter
    private String id;

    @Column(name = "original_file_name", nullable = false)
    @Getter
    @Setter
    private String originalFileName;

    @Column(name = "creator_id", nullable = false)
    @Getter
    @Setter
    private String creatorId;

    @ElementCollection
    @OneToMany(orphanRemoval = true)
    @Column(name = "comments")
    @Getter
    @Setter
    private List<Comment> comments;

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }
}
