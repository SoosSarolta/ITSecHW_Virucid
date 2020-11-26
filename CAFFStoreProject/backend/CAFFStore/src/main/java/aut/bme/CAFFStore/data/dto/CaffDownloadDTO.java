package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.service.ByteArraySerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static aut.bme.CAFFStore.Constants.CAFF_FILES_PATH;
import static aut.bme.CAFFStore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class CaffDownloadDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String originalFileName;

    private byte[] caffFile;

    @JsonCreator
    public CaffDownloadDTO(@JsonProperty("id") String id,
                           @JsonProperty("originalFileName") String originalFileName,
                           @JsonProperty("caffFile") byte[] caffFile) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.caffFile = caffFile;
    }

    public static CaffDownloadDTO createCaffDownloadDTO(Caff caff) {
        return new CaffDownloadDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                getFileBytes(caff.getId(), ".caff", CAFF_FILES_PATH));
    }

    public static List<CaffDownloadDTO> createCaffDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDownloadDTO::createCaffDownloadDTO).collect(Collectors.toList());
    }

    @JsonSerialize(using = ByteArraySerializer.class)
    public byte[] getCaffFile() {
        return caffFile;
    }

    public void setCaffFile(byte[] caffFile) {
        this.caffFile = caffFile;
    }
}
