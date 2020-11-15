package aut.bme.CAFFStore.data.entity;

import aut.bme.CAFFStore.data.Role;
import aut.bme.CAFFStore.data.util.password.PasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Transient
    Logger logger = LoggerFactory.getLogger(User.class);

    @Id
    @GenericGenerator(name = "sequence_uuid", strategy = "aut.bme.CAFFStore.data.util.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sequence_uuid")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Getter
    @Setter
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
    @Column(name = "caff_file_names")
    @Getter
    @Setter
    private List<String> caffFileNames;

    @ElementCollection
    @OneToMany
    @Column(name = "comment_ids")
    @Getter
    @Setter
    private List<Comment> comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Getter
    @Setter
    private Role role;

    public void addCaffFile(String caffFileName) {
        caffFileNames.add(caffFileName);
    }

}
