package aut.bme.CAFFStore.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "caff_credits")
public class CaffCredits {

    @Id
    @Column(name = "id")
    @Getter
    @Setter
    private Long id;

    @Column(name = "creation_year")
    @Getter
    @Setter
    private int creationYear;

    @Column(name = "creation_month")
    @Getter
    @Setter
    private int creationMonth;

    @Column(name = "creation_day")
    @Getter
    @Setter
    private int creationDay;

    @Column(name = "creation_hour")
    @Getter
    @Setter
    private int creationHour;

    @Column(name = "creation_minute")
    @Getter
    @Setter
    private int creationMinute;

    @Column(name = "creator_len")
    @Getter
    @Setter
    private int creatorLen;

    @Column(name = "creator")
    @Getter
    @Setter
    private String creator;
}
