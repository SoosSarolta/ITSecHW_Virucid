package aut.bme.CAFFStore.web.controller;

import aut.bme.CAFFStore.data.dto.CaffDTO;
import aut.bme.CAFFStore.data.dto.CaffDetailsDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.entity.User;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.data.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/caffs")
public class CaffController {

    Logger logger = LoggerFactory.getLogger(CaffController.class);

    @Autowired
    private CaffRepo caffRepo;

    @Autowired
    private UserRepo userRepo;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CaffDTO>> getAllCaffFiles() {
        logger.info("Getting all caff");
        return new ResponseEntity<>(CaffDTO.createCaffDTOs(caffRepo.findAll()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CaffDetailsDTO> getCaffDetailsById(@PathVariable String id) {
        logger.info("Finding caff file with id: " + id);
        Optional<Caff> caff = caffRepo.findById(id);
        return caff.map(value -> new ResponseEntity<>(CaffDetailsDTO.createCaffDetailsDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCaff(@PathVariable String id) {
        logger.info("Deleting caff file with id: " + id);
        if (caffRepo.findById(id).isPresent()) {
            caffRepo.deleteById(id);
            return new ResponseEntity<>("Successful deletion.", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/parse/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> parseCaff(@PathVariable String id, @RequestParam String filename, @RequestParam String userId) {
        logger.info("Parsing caff file with id: " + id + "and with filename: " + filename);
        Optional<Caff> caff = caffRepo.findById(id);
        //call dll
        //fill missing fields of the caff entity
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) {
            user.get().addCaffFile(filename);
        } else {
            return new ResponseEntity<>("User does not exist.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Successful parse.", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/caffid", method = RequestMethod.GET)
    public ResponseEntity<String> getCaffId() {
        logger.info("Generation caff id.");
        Caff caff = new Caff();
        caffRepo.save(caff);
        return new ResponseEntity<>(caff.getId(), HttpStatus.OK);
    }
}