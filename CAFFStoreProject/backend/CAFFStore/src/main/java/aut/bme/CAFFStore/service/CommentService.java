package aut.bme.CAFFStore.service;

import aut.bme.CAFFStore.controller.CommentController;
import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.dto.request.CommentRequestDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.Comment;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.CommentRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommentService {

    private final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private CaffRepo caffRepo;

    public ResponseEntity<StringResponseDTO> saveComment(CommentRequestDTO commentRequestDTO, String userId, String caffId) {
        Comment comment = new Comment(commentRequestDTO);
        commentRepo.save(comment);

        Optional<User> user = userRepo.findById(userId);
        Optional<Caff> caff = caffRepo.findById(caffId);

        if (user.isEmpty()) {
            return new ResponseEntity<>(new StringResponseDTO("User does not exist."), HttpStatus.BAD_REQUEST);
        }
        if (caff.isEmpty()) {
            return new ResponseEntity<>(new StringResponseDTO("Caff does not exist."), HttpStatus.BAD_REQUEST);
        }

        user.get().addComment(comment);
        userRepo.save(user.get());

        caff.get().addComment(comment);
        caffRepo.save(caff.get());

        return new ResponseEntity<>(new StringResponseDTO("Comment added successfully."), HttpStatus.OK);
    }
}
