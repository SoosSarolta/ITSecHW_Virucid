package aut.bme.caffstore.controller;

import aut.bme.caffstore.data.dto.response.BitmapResponseDTO;
import aut.bme.caffstore.data.dto.response.CaffDetailsResponseDTO;
import aut.bme.caffstore.data.dto.response.StringResponseDTO;
import aut.bme.caffstore.service.CaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/caffs")
public class CaffController {

    private final Logger logger = LoggerFactory.getLogger(CaffController.class);

    @Autowired
    private CaffService caffService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<BitmapResponseDTO>> getAllCaffFiles() {
        logger.info("Getting all caff.");
        return caffService.getAllCaffs();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<CaffDetailsResponseDTO> getCaffDetailsById(@PathVariable String id) {
        logger.info("Finding caff file with id: {}", id);
        return caffService.getCaffDetailsById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<StringResponseDTO> deleteCaff(@PathVariable String id) {
        logger.info("Deleting caff file with id: {}", id);
        return caffService.deleteCaffAndConnectedFiles(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/upload")
    public ResponseEntity<StringResponseDTO> uploadCaff(@RequestPart(name = "file", required = false) MultipartFile file,
                                                        @RequestParam String userId,
                                                        HttpServletRequest request) throws IOException, InterruptedException {
        if (file != null) {
            logger.info("Parsing caff file with filename: {}", file.getOriginalFilename());
            return caffService.uploadCaff(file, userId);
        }
        return new ResponseEntity<>(new StringResponseDTO("File is missing."), HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/download/{id}", produces = "application/zip")
    public ResponseEntity<byte[]> downloadCaff(HttpServletResponse response, @PathVariable String id) throws IOException {
        response.setContentType("application/zip");
        logger.info("Downloading caff file with id: {}", id);
        return caffService.downloadCaff(id);
    }
}