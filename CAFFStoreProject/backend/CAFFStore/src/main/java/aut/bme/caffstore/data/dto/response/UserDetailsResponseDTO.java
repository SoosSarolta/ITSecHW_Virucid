package aut.bme.caffstore.data.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public class UserDetailsResponseDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private List<CommentResponseDTO> comments;

    @Getter
    @Setter
    private List<BitmapResponseDTO> caffFilesWithoutBitmap;

    public UserDetailsResponseDTO() {
    }

    @JsonCreator
    public UserDetailsResponseDTO(@JsonProperty("id") String id,
                                  @JsonProperty("username") String username,
                                  @JsonProperty("email") String email,
                                  @JsonProperty("comments") List<CommentResponseDTO> comments,
                                  @JsonProperty("caffFilesWithoutBitmap") List<BitmapResponseDTO> caffFilesWithoutBitmap) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.comments = comments;
        this.caffFilesWithoutBitmap = caffFilesWithoutBitmap;
    }
}
