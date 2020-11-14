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
public class UserDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String token;

    @JsonCreator
    public UserDTO(@JsonProperty("id") String id,
                   @JsonProperty("username") String username,
                   @JsonProperty("token") String token) {

        this.id = id;
        this.username = username;
        this.token = token;
    }

    public static UserDTO createUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getPersonName(),
                null);
    }

    public static List<UserDTO> createUserDTOs(List<User> users) {
        return users.stream().map(UserDTO::createUserDTO).collect(Collectors.toList());
    }
}
