package aut.bme.CAFFStore.data.repository;

import aut.bme.CAFFStore.data.entity.Ciff;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CIFFRepo extends CrudRepository<Ciff, String> {
    Optional<Ciff> findAllById(Long id);
}
