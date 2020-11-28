package aut.bme.caffstore.data.repository;

import aut.bme.caffstore.data.entity.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepo extends CrudRepository<Comment, String> {
}
