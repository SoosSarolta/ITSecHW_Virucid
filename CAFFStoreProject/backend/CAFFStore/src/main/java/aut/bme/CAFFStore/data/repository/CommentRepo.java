package aut.bme.CAFFStore.data.repository;

import aut.bme.CAFFStore.data.entity.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepo extends CrudRepository<Comment, String> {
}
