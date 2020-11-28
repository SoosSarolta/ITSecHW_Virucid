package aut.bme.caffstore.data.entity;

import aut.bme.caffstore.data.dto.request.CommentRequestDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GenericGenerator(name = "sequence_uuid", strategy = "aut.bme.caffstore.data.util.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sequence_uuid")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Getter
    private String id;

    @Column(name = "time_stamp", nullable = false)
    @Getter
    @Setter
    private Date timeStamp;

    @Column(name = "comment", nullable = false)
    @Getter
    @Setter
    private String commentStr;

    public Comment() {
    }

    public Comment(CommentRequestDTO commentRequestDTO) {
        this.commentStr = commentRequestDTO.getComment();
        this.timeStamp = new Date();
    }
}
