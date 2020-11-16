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
public class CommentResponseDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String comment;

    @JsonCreator
    public CommentResponseDTO(@JsonProperty("id") String id,
                              @JsonProperty("comment") String comment) {
        this.id = id;
        this.comment = comment;
    }

    public static CommentResponseDTO createCommentDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getComment());
    }

    public static List<CommentResponseDTO> createCommentDTOs(List<Comment> comments) {
        return comments.stream().map(CommentResponseDTO::createCommentDTO).collect(Collectors.toList());
    }
}
