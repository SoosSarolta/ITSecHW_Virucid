package aut.bme.CAFFStore.unittests;

import aut.bme.CAFFStore.data.dto.CommentRequestDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.CommentRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
import aut.bme.CAFFStore.service.CommentService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

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
    void testSaveCommentWithNonExistingUser(){
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("mycomment");
        String userId = "MyUserId";
        String caffId = "MyCaffId";

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = commentService.saveComment(commentRequestDTO, userId, caffId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("User does not exist.", responseEntity.getBody());
    }

    @Test
    void testSaveCommentWithNonExistingCaff(){
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("mycomment");
        String userId = "MyUserId";
        String caffId = "MyCaffId";

        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(caffRepo.findById(caffId)).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = commentService.saveComment(commentRequestDTO, userId, caffId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Caff does not exist.", responseEntity.getBody());
    }

    @Test
    void testSaveComment(){
        CommentRequestDTO commentRequestDTO = new CommentRequestDTO("mycomment");
        String userId = "MyUserId";
        String caffId = "MyCaffId";

        when(userRepo.findById(userId)).thenReturn(Optional.of(new User()));
        when(caffRepo.findById(caffId)).thenReturn(Optional.of(new Caff()));

        ResponseEntity<String> responseEntity = commentService.saveComment(commentRequestDTO, userId, caffId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Successful save.", responseEntity.getBody());
    }
}
