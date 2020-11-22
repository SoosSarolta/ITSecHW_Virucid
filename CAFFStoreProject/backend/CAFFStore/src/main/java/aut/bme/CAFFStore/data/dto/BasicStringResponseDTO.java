package aut.bme.CAFFStore.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class BasicStringResponseDTO {

    @Getter
    @Setter
    private String response;

    @JsonCreator
    public BasicStringResponseDTO(@JsonProperty("response") String response) {
        this.response = response;
    }
}
