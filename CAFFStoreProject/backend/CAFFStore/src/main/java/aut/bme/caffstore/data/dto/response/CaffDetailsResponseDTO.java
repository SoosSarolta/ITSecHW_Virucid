package aut.bme.caffstore.data.dto.response;

import aut.bme.caffstore.data.entity.Caff;
import aut.bme.caffstore.data.entity.Comment;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static aut.bme.caffstore.Constants.getGifFilePath;
import static aut.bme.caffstore.service.CaffService.getFileBytes;

@EqualsAndHashCode
@ToString
public class CaffDetailsResponseDTO {

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
    private List<CommentResponseDTO> comments;

    @JsonCreator
    public CaffDetailsResponseDTO(@JsonProperty("id") String id,
                                  @JsonProperty("originalFileName") String originalFileName,
                                  @JsonProperty("creatorId") String creatorId,
                                  @JsonProperty("gifFile") byte[] gifFile,
                                  @JsonProperty("comments") List<CommentResponseDTO> comments) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.creatorId = creatorId;
        this.gifFile = gifFile;
        this.comments = comments;
    }

    public static CaffDetailsResponseDTO createCaffDetailsDTO(Caff caff) {
        return new CaffDetailsResponseDTO(
                caff.getId(),
                caff.getOriginalFileName(),
                caff.getCreatorId(),
                getFileBytes(getGifFilePath(caff.getId())),
                caff.getComments()
                        .stream()
                        .sorted(Comparator.comparing(Comment::getTimeStamp))
                        .map(CommentResponseDTO::createCommentDTO)
                        .collect(Collectors.toList()));
    }

    public static List<CaffDetailsResponseDTO> createCaffDetailsDTOs(List<Caff> caffs) {
        return caffs.stream().map(CaffDetailsResponseDTO::createCaffDetailsDTO).collect(Collectors.toList());
    }
}
