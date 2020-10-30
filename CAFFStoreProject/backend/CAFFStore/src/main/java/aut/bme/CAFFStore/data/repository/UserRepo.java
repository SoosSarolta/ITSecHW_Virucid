package aut.bme.CAFFStore.data.repository;

import aut.bme.CAFFStore.data.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, String> {
    Optional<User> findById(String id);
}