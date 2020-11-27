package aut.bme.CAFFStore.data.dto;

import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.Comment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static aut.bme.CAFFStore.Constants.CAFF_FILES_PATH;
import static aut.bme.CAFFStore.Constants.ROOT_PATH;
import static aut.bme.CAFFStore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class CaffDetailsDTO {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String originalFileName;

    @Getter
    @Setter
    private String creatorId;

    @Getter
    @Setter
    private byte[] gifFile;

    @Getter
    @Setter
    private byte[] caffFile;

    @Getter
    @Setter
    private List<CommentResponseDTO> comments;

    @JsonCreator
    public CaffDetailsDTO(@JsonProperty("id") String id,
                          @JsonProperty("originalFileName") String originalFileName,
                          @JsonProperty("creatorId") String creatorId,
                          @JsonProperty("gifFile") byte[] gifFile,
                          @JsonProperty("caffFile") byte[] caffFile,
                          @JsonProperty("comments") List<CommentResponseDTO> comments) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.creatorId = creatorId;
        this.gifFile = gifFile;
        this.caffFile = caffFile;
        this.comments = comments;
    }

    public static CaffDetailsDTO createCaffDetailsDTO(Caff caff) {
        return new CaffDetailsDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                caff.getCreatorId(),
                getFileBytes(ROOT_PATH + caff.getId() + ".gif"),
                getFileBytes(CAFF_FILES_PATH + caff.getId() + ".caff"),
                caff.getComments()
                        .stream()
                        .sorted(Comparator.comparing(Comment::getTimeStamp))
                        .map(CommentResponseDTO::createCommentDTO)
                        .collect(Collectors.toList()));
    }

    public static List<CaffDetailsDTO> createCaffDetailsDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDetailsDTO::createCaffDetailsDTO).collect(Collectors.toList());
    }
}
