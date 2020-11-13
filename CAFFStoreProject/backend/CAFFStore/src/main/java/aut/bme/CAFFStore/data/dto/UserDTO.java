package aut.bme.CAFFStore.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UserDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;

    @JsonCreator
    public UserDTO(@JsonProperty("id") String id,
                   @JsonProperty("username") String username) {

        this.id = id;
        this.username = username;
    }
}
