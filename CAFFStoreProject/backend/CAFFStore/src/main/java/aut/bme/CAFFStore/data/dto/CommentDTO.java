package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.data.entity.Comment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

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

    public static CommentDTO createCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getComment());
    }

    public static List<CommentDTO> createCommentDTOs(List<Comment> comments) {
        return comments.stream().map(CommentDTO::createCommentDTO).collect(Collectors.toList());
    }
}
