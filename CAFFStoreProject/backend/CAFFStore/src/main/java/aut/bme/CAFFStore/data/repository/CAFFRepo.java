package aut.bme.CAFFStore.data.repository;

import aut.bme.CAFFStore.data.entity.Caff;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CAFFRepo extends CrudRepository<Caff, String> {
    Optional<Caff> findAllById(Long id);
}
