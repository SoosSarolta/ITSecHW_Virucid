package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.data.entity.User;
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
public class UserDetailsDTO {

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
    private List<CommentDTO> comments;

    @Getter
    @Setter
    private List<String> caffFileNames;

    @JsonCreator
    public UserDetailsDTO(@JsonProperty("id") String id,
                          @JsonProperty("username") String username,
                          @JsonProperty("email") String email,
                          @JsonProperty("comments") List<CommentDTO> comments,
                          @JsonProperty("caffFileNames") List<String> caffFileNames) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.comments = comments;
    }

    public static UserDetailsDTO createUserDetailsDTO(User user) {
        return new UserDetailsDTO(
                user.getId(),
                user.getPersonName(),
                user.getEmail(),
                user.getComments().stream().map(CommentDTO::createCommentDTO).collect(Collectors.toList()),
                user.getCaffFileNames());
    }

    public static List<UserDetailsDTO> createUserDetailsDTOs(List<User> users) {
        return users.stream().map(UserDetailsDTO::createUserDetailsDTO).collect(Collectors.toList());
    }
}
