package aut.bme.CAFFStore.web.controller;

import aut.bme.CAFFStore.data.dto.CaffDTO;
import aut.bme.CAFFStore.data.dto.CaffDetailsDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/caffs")
public class CaffController {

    Logger logger = LoggerFactory.getLogger(CaffController.class);

    @Autowired
    private CaffRepo caffRepo;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CaffDTO>> getAllCaffFiles() {
        logger.info("Getting all caff");
        return new ResponseEntity<>(CaffDTO.createCaffDTOs(caffRepo.findAll()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CaffDetailsDTO> getCaffDetailsById(@PathVariable String id) {
        logger.info("Finding caff file by id");
        Optional<Caff> caff = caffRepo.findById(id);
        if (caff.isPresent()) {
            return new ResponseEntity<>(CaffDetailsDTO.createCaffDetailsDTO(caff.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteCaff(@PathVariable String id) {
        logger.info("Deleting caff file by id");
        if (caffRepo.findById(id).isPresent()) {
            caffRepo.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}