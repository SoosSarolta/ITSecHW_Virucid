package aut.bme.CAFFStore.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CommentDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String comment;

    @JsonCreator
    public CommentDTO(@JsonProperty("id") String id,
                      @JsonProperty("comment") String comment) {
        this.id = id;
        this.comment = comment;
    }
}
