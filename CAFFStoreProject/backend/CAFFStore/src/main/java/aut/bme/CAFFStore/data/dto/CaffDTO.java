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

import static aut.bme.CAFFStore.Constants.CAFF_FILES_PATH;
import static aut.bme.CAFFStore.Constants.ROOT_PATH;
import static aut.bme.CAFFStore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class CaffDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String originalFileName;

    @Getter
    @Setter
    private byte[] caffFile;

    @JsonCreator
    public CaffDTO(@JsonProperty("id") String id,
                   @JsonProperty("originalFileName") String originalFileName,
                   @JsonProperty("caffFile") byte[] caffFile) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.caffFile = caffFile;
    }

    public static CaffDTO createCaffDTO(Caff caff) {
        return new CaffDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                getFileBytes(caff.getId(), ".caff", CAFF_FILES_PATH));
    }

    public static CaffDTO createCaffDTOWithoutBitmap(Caff caff) {
        return new CaffDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                null);
    }

    public static List<CaffDTO> createCaffDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDTO::createCaffDTO).collect(Collectors.toList());
    }

    public static List<CaffDTO> createCaffDTOsWithoutBitmap(List<Caff> caffs) {
        return caffs.stream().map(CaffDTO::createCaffDTOWithoutBitmap).collect(Collectors.toList());
    }
}
