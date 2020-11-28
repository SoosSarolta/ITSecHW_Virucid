package aut.bme.caffstore.controller;

import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.data.dto.request.CommentRequestDTO;
import aut.bme.caffstore.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<StringResponseDTO> addComment(@RequestBody CommentRequestDTO commentRequestDTO,
                                                        @RequestParam String userId,
                                                        @RequestParam String caffId) {
        logger.info("Saving comment.");
        return commentService.saveComment(commentRequestDTO, userId, caffId);
    }
}
