package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.data.entity.Caff;
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

    public static CaffDTO createCaffDTO(Caff caff) {
        return new CaffDTO(
                caff.getId(),
                caff.getBitmapFileNames().get(0));
    }

    public static List<CaffDTO> createCaffDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDTO::createCaffDTO).collect(Collectors.toList());
    }
}
