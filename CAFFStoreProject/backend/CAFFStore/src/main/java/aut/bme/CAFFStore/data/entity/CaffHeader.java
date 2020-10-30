package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "caff_header")
public class CaffHeader {

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

    @Column(name = "num_anim")
    @Getter
    @Setter
    private int numAnim;
}
