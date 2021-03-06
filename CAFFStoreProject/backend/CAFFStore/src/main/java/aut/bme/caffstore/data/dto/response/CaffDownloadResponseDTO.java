package aut.bme.caffstore.data.dto.response;

import aut.bme.caffstore.data.entity.Caff;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static aut.bme.caffstore.Constants.getCaffFilePath;
import static aut.bme.caffstore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class CaffDownloadResponseDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String originalFileName;

    @Getter
    @Setter
    private String caffFile;

    @JsonCreator
    public CaffDownloadResponseDTO(@JsonProperty("id") String id,
                                   @JsonProperty("originalFileName") String originalFileName,
                                   @JsonProperty("caffFile") String caffFile) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.caffFile = caffFile;
    }

    public static CaffDownloadResponseDTO createCaffDownloadDTO(Caff caff) {
        return new CaffDownloadResponseDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                new String(getFileBytes(getCaffFilePath(caff.getId())), StandardCharsets.ISO_8859_1));
    }

    public static List<CaffDownloadResponseDTO> createCaffDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDownloadResponseDTO::createCaffDownloadDTO).collect(Collectors.toList());
    }
}
