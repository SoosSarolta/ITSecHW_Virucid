package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.service.CaffService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@EqualsAndHashCode
@ToString
public class UserDetailsDTO {

    @Autowired
    CaffService caffService;

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
    private List<CaffDTO> caffFilesWithoutBitmap;

    public UserDetailsDTO() {
    }

    @JsonCreator
    public UserDetailsDTO(@JsonProperty("id") String id,
                          @JsonProperty("username") String username,
                          @JsonProperty("email") String email,
                          @JsonProperty("comments") List<CommentResponseDTO> comments,
                          @JsonProperty("caffFilesWithoutBitmap") List<CaffDTO> caffFilesWithoutBitmap) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.comments = comments;
        this.caffFilesWithoutBitmap = caffFilesWithoutBitmap;
    }
}
