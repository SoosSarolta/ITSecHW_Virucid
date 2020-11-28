package aut.bme.caffstore.data.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class StringResponseDTO {

    @Getter
    @Setter
    private String response;

    @JsonCreator
    public StringResponseDTO(@JsonProperty("response") String response) {
        this.response = response;
    }
}
