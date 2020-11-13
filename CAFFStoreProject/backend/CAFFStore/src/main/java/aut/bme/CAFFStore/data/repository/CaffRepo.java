package aut.bme.CAFFStore.data.repository;

import aut.bme.CAFFStore.data.entity.Caff;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CaffRepo extends CrudRepository<Caff, String> {
    List<Caff> findAll();
}
