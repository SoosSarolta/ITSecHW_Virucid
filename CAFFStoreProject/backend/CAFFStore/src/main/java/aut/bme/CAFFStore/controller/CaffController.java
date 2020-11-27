package aut.bme.CAFFStore.controller;

import aut.bme.CAFFStore.data.dto.response.BitmapResponseDTO;
import aut.bme.CAFFStore.data.dto.response.CaffDetailsResponseDTO;
import aut.bme.CAFFStore.data.dto.response.StringResponseDTO;
import aut.bme.CAFFStore.data.entity.Caff;
import aut.bme.CAFFStore.data.repository.CaffRepo;
import aut.bme.CAFFStore.service.CaffService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static aut.bme.CAFFStore.Constants.getCaffFilePath;

@RestController
@RequestMapping("/caffs")
public class CaffController {

    Logger logger = LoggerFactory.getLogger(CaffController.class);

    @Autowired
    private CaffRepo caffRepo;

    @Autowired
    private CaffService caffService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BitmapResponseDTO>> getAllCaffFiles() {
        logger.info("Getting all caff");
        return new ResponseEntity<>(BitmapResponseDTO.createCaffDTOs(caffRepo.findAll()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CaffDetailsResponseDTO> getCaffDetailsById(@PathVariable String id) {
        logger.info("Finding caff file with id: " + id);
        Optional<Caff> caff = caffRepo.findById(id);
        return caff.map(value -> new ResponseEntity<>(CaffDetailsResponseDTO.createCaffDetailsDTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<StringResponseDTO> deleteCaff(@PathVariable String id) {
        logger.info("Deleting caff file with id: " + id);
        return caffService.deleteCaffAndConnectedFiles(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<StringResponseDTO> uploadCaff(@RequestPart(name = "file", required = false) MultipartFile file,
                                                        @RequestParam String userId,
                                                        HttpServletRequest request) throws IOException, InterruptedException {
        if (file != null) {
            logger.info("Parsing caff file with filename: " + file.getOriginalFilename());
            return caffService.uploadCaff(file, userId);
        }
        return new ResponseEntity<>(new StringResponseDTO("File is missing."), HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET, produces = "application/zip")
    public byte[] downloadCaff(HttpServletResponse response, @PathVariable String id) throws IOException {
        response.setContentType("application/zip");
        logger.info("Downloading caff file with id: " + id);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        File file = new File(getCaffFilePath(id));
        if (file.exists()) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}