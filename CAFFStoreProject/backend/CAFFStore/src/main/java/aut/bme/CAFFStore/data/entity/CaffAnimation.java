package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Embeddable
@Table(name = "caff_animation")
public class CaffAnimation {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "duration")
    @Getter
    @Setter
    private int duration;

    @OneToMany
    @ElementCollection
    private List<Ciff> ciffs;
    
}
