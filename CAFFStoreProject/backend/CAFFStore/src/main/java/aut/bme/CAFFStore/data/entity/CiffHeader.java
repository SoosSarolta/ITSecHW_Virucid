package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ciff_header")
public class CiffHeader {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "magic")
    @Getter
    @Setter
    private String magic;

    @Column(name = "header_size")
    @Getter
    @Setter
    private int headerSize;

    @Column(name = "content_size")
    @Getter
    @Setter
    private int contentSize;

    @Column(name = "width")
    @Getter
    @Setter
    private int width;

    @Column(name = "height")
    @Getter
    @Setter
    private int height;

    @Column(name = "caption")
    @Getter
    @Setter
    private String caption;

    @ElementCollection
    @Getter
    @Setter
    private List<String> tags;
}
