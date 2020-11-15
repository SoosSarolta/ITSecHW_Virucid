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

import static aut.bme.CAFFStore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class CaffDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private byte[] bitmapFile;

    @JsonCreator
    public CaffDTO(@JsonProperty("id") String id,
                   @JsonProperty("bitmapFile") byte[] bitmapFile) {
        this.id = id;
        this.bitmapFile = bitmapFile;
    }

    public static CaffDTO createCaffDTO(Caff caff) {
        return new CaffDTO(
                caff.getId(),
                getFileBytes(caff.getId(), ".bmp"));
    }

    public static List<CaffDTO> createCaffDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDTO::createCaffDTO).collect(Collectors.toList());
    }
}
