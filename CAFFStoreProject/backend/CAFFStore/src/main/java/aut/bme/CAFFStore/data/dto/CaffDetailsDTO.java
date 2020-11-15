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
public class CaffDetailsDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private byte[] gifFile;

    @Getter
    @Setter
    private byte[] caffFile;

    @Getter
    @Setter
    private List<CommentDTO> comments;

    @JsonCreator
    public CaffDetailsDTO(@JsonProperty("id") String id,
                          @JsonProperty("gifFile") byte[] gifFile,
                          @JsonProperty("caffFile") byte[] caffFile,
                          @JsonProperty("comments") List<CommentDTO> comments) {
        this.id = id;
        this.gifFile = gifFile;
        this.caffFile = caffFile;
        this.comments = comments;
    }

    public static CaffDetailsDTO createCaffDetailsDTO(Caff caff) {
        return new CaffDetailsDTO(
                caff.getId(),
                getFileBytes(caff.getId(), ".gif"),
                getFileBytes(caff.getId(), ".caff"),
                caff.getComments().stream().map(CommentDTO::createCommentDTO).collect(Collectors.toList()));
    }

    public static List<CaffDetailsDTO> createCaffDetailsDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDetailsDTO::createCaffDetailsDTO).collect(Collectors.toList());
    }
}
