package aut.bme.CAFFStore.web.controller;

import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.repository.CAFFRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Optional;

@RestController
public class CAFFController {

    @Autowired
    private CAFFRepo caffRepo;

    @RequestMapping(value = "/caff", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllCaffFiles() {
        return new ResponseEntity<>(caffRepo.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/caff/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getCaffById(@PathParam("id") Long id) {
        Optional<Caff> caff = caffRepo.findAllById(id);
        if (caff.isPresent()) {
            return new ResponseEntity<>(caffRepo.findAllById(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
