package aut.bme.caffstore.data.repository;

import aut.bme.caffstore.data.entity.Caff;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CaffRepo extends CrudRepository<Caff, String> {
    List<Caff> findAll();
}
