package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.data.entity.Comment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class CaffDetailsDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String gifFileName;

    @Getter
    @Setter
    private String caffFileName;

    @Getter
    @Setter
    private List<Comment> comments;

    @JsonCreator
    public CaffDetailsDTO(@JsonProperty("id") String id,
                          @JsonProperty("gifFileName") String gifFileName,
                          @JsonProperty("caffFileName") String caffFileName,
                          @JsonProperty("comments") List<Comment> comments) {
        this.id = id;
        this.gifFileName = gifFileName;
        this.caffFileName = caffFileName;
        this.comments = comments;
    }
}
