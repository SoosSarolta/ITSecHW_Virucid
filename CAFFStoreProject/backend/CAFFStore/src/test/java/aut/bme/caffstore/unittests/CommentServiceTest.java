package aut.bme.caffstore.unittests;

import aut.bme.caffstore.data.dto.request.CommentRequestDTO;
import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.data.entity.Caff;
import aut.bme.caffstore.data.entity.User;
import aut.bme.caffstore.data.repository.CaffRepo;
import aut.bme.caffstore.data.repository.CommentRepo;
import aut.bme.caffstore.data.repository.UserRepo;
import aut.bme.caffstore.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private CaffRepo caffRepo;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSaveCommentWithNonExistingUser() {
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("mycomment");
        String userId = "MyUserId";
        String caffId = "MyCaffId";

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<StringResponseDTO> responseEntity = commentService.saveComment(commentRequestDTO, userId, caffId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("User does not exist.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testSaveCommentWithNonExistingCaff() {
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("mycomment");
        String userId = "MyUserId";
        String caffId = "MyCaffId";

        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(caffRepo.findById(caffId)).thenReturn(Optional.empty());

        ResponseEntity<StringResponseDTO> responseEntity = commentService.saveComment(commentRequestDTO, userId, caffId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Caff does not exist.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }

    @Test
    void testSaveComment() {
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("mycomment");
        String userId = "MyUserId";
        String caffId = "MyCaffId";

        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(caffRepo.findById(caffId)).thenReturn(Optional.of(new Caff()));

        ResponseEntity<StringResponseDTO> responseEntity = commentService.saveComment(commentRequestDTO, userId, caffId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Comment added successfully.", Objects.requireNonNull(responseEntity.getBody()).getResponse());
    }
}
