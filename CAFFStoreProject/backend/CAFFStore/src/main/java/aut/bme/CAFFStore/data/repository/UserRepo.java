package aut.bme.CAFFStore.data.repository;

import aut.bme.CAFFStore.data.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends CrudRepository<User, String> {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPersonName(String personName);

    List<User> findAll();

    boolean existsByEmail(String email);
}