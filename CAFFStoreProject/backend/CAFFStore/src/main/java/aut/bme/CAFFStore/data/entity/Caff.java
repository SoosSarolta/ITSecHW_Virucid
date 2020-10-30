package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "caff")
public class Caff {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @OneToOne
    @Getter
    @Setter
    private CaffHeader caffHeader;
    
    @OneToOne
    @Getter
    @Setter
    private CaffCredits caffCredits;

    @OneToOne
    @Getter
    @Setter
    private CaffAnimation caffAnimation;

}
