package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    @Column(name = "gif_file_name")
    @Getter
    @Setter
    private String gifFileName;

    @Column(name = "caff_file_name")
    @Getter
    @Setter
    private String caffFileName;

    @ElementCollection
    @Column(name = "bitmap_file_names")
    @Getter
    @Setter
    private List<String> bitmapFileNames;

    @ElementCollection
    @OneToMany
    @Column(name = "comments")
    @Getter
    @Setter
    private List<Comment> comments;
}
