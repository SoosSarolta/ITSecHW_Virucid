package aut.bme.caffstore.data.entity;

import aut.bme.caffstore.data.Role;
import aut.bme.caffstore.data.util.password.PasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GenericGenerator(name = "sequence_uuid", strategy = "aut.bme.caffstore.data.util.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sequence_uuid")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Getter
    private String id;

    @Column(name = "create_date", nullable = false)
    @Getter
    @Setter
    private LocalDateTime createDate;

    @Column(name = "person_name", nullable = false)
    @Getter
    @Setter
    private String personName;

    @Column(name = "email", nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(name = "password", nullable = false)
    @JsonSerialize(using = PasswordSerializer.class)
    @Getter
    @Setter
    private byte[] password;

    @Column(name = "salt", nullable = false)
    @JsonSerialize(using = PasswordSerializer.class)
    @Getter
    @Setter
    private byte[] salt;

    @ElementCollection
    @Column(name = "caffs")
    @Getter
    @Setter
    private List<Caff> caffs;

    @ElementCollection
    @Column(name = "comments")
    @Getter
    @Setter
    private List<Comment> comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Getter
    @Setter
    private Role role;

    public void addCaffFile(Caff caff) {
        if (caffs == null) {
            caffs = new ArrayList<>();
        }
        caffs.add(caff);
    }

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }

    public void removeCaff(Caff caff) {
        caffs.remove(caff);
    }

}
