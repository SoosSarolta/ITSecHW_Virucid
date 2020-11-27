package aut.bme.CAFFStore.data.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CommentRequestDTO {

    @Getter
    @Setter
    private String comment;

    @JsonCreator
    public CommentRequestDTO(@JsonProperty("comment") String comment) {
        this.comment = comment;
    }
}
