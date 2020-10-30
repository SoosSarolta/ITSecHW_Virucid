package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ciff")
public class Ciff {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @OneToOne
    @Getter
    @Setter
    private CiffHeader ciff_header;

    @OneToOne
    @Getter
    @Setter
    private CiffContent ciff_content;
}
