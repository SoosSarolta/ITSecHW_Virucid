package aut.bme.CAFFStore.data.entity;

import aut.bme.CAFFStore.data.util.RGB;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ciff_content")
public class CiffContent {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @ElementCollection(targetClass = RGB.class)
    @CollectionTable
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private List<List<RGB>> pixels;
}
