package aut.bme.caffstore.data.dto.response;

import aut.bme.caffstore.data.entity.Caff;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static aut.bme.caffstore.Constants.getBitmapFilePath;
import static aut.bme.caffstore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class BitmapResponseDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String originalFileName;

    @Getter
    @Setter
    private byte[] bitmapFile;

    @JsonCreator
    public BitmapResponseDTO(@JsonProperty("id") String id,
                             @JsonProperty("originalFileName") String originalFileName,
                             @JsonProperty("bitmapFile") byte[] bitmapFile) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.bitmapFile = bitmapFile;
    }

    public static BitmapResponseDTO createCaffDTO(Caff caff) {
        return new BitmapResponseDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                getFileBytes(getBitmapFilePath(caff.getId())));
    }

    public static BitmapResponseDTO createCaffDTOWithoutBitmap(Caff caff) {
        return new BitmapResponseDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                null);
    }

    public static List<BitmapResponseDTO> createCaffDTOs(List<Caff> caffs) {
        return caffs.stream().map(BitmapResponseDTO::createCaffDTO).collect(Collectors.toList());
    }

    public static List<BitmapResponseDTO> createCaffDTOsWithoutBitmap(List<Caff> caffs) {
        return caffs.stream().map(BitmapResponseDTO::createCaffDTOWithoutBitmap).collect(Collectors.toList());
    }
}
