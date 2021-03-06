package aut.bme.caffstore.data.dto.response;

import aut.bme.caffstore.data.entity.User;
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
public class UserResponseDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private String email;

    public UserResponseDTO() {
    }

    @JsonCreator
    public UserResponseDTO(@JsonProperty("id") String id,
                           @JsonProperty("username") String username,
                           @JsonProperty("token") String token,
                           @JsonProperty("role") String role,
                           @JsonProperty("email") String email) {

        this.id = id;
        this.username = username;
        this.token = token;
        this.role = role;
        this.email = email;
    }

    public static UserResponseDTO createUserDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getPersonName(),
                null,
                user.getRole().toString(),
                user.getEmail());
    }

    public static List<UserResponseDTO> createUserDTOs(List<User> users) {
        return users.stream().map(UserResponseDTO::createUserDTO).collect(Collectors.toList());
    }
}
