package aut.bme.caffstore.data.dto.response;

import aut.bme.caffstore.data.entity.Comment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.text.SimpleDateFormat;
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

    @Getter
    @Setter
    private String timeStamp;

    @JsonCreator
    public CommentResponseDTO(@JsonProperty("id") String id,
                              @JsonProperty("comment") String comment,
                              @JsonProperty("timeStamp") String timeStamp) {
        this.id = id;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    public static CommentResponseDTO createCommentDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getCommentStr(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comment.getTimeStamp()));
    }

    public static List<CommentResponseDTO> createCommentDTOs(List<Comment> comments) {
        return comments.stream().map(CommentResponseDTO::createCommentDTO).collect(Collectors.toList());
    }
}
