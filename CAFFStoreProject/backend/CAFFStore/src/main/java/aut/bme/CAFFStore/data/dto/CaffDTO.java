package aut.bme.CAFFStore.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CaffDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String bitmapFileName;

    @JsonCreator
    public CaffDTO(@JsonProperty("id") String id,
                   @JsonProperty("bitmapFileName") String bitmapFileName) {
        this.id = id;
        this.bitmapFileName = bitmapFileName;
    }
}
