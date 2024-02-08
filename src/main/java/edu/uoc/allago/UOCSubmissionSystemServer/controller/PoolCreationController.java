package edu.uoc.allago.UOCSubmissionSystemServer.controller;

import edu.uoc.allago.UOCSubmissionSystemServer.model.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@RestController
public class PoolCreationController {
    private final Logger logger = LoggerFactory.getLogger(PoolCreationController.class);

    private final String userHome = System.getProperty("user.home");
    private final String FILE_DIRECTORY =  userHome + "/UOCSubmissionSystemPools";

    @PostMapping("/create_pool/{uploadId}")
    public ResponseEntity<String> uploadFile(@PathVariable("uploadId") String uploadId,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("date") String date) {
        String originalFilename = file.getOriginalFilename();

        // Check if the file is a ZIP file
        if (originalFilename == null || !originalFilename.endsWith(".zip")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File type not allowed. Only ZIP files are accepted.");
        }

        // Check if the date is today or in the future
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate givenDate = LocalDate.parse(date, formatter);
            LocalDate currentDate = LocalDate.now();
            if (givenDate.isBefore(currentDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Date should be today or in the future.");
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format. It should be yyyy-MM-dd.");
        }

        try {
            Path baseDirectory = Paths.get(FILE_DIRECTORY);
            if (!Files.exists(baseDirectory)) {
                Files.createDirectories(baseDirectory);
            }

            saveFile(uploadId, file, date);
            return ResponseEntity.status(HttpStatus.OK).body("success");
        } catch (IOException e) {
            logger.error("Error uploading the file.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading the file.");
        }
    }

    private void saveFile(String uploadId, MultipartFile file, String date) throws IOException {
        Path filePath = Paths.get(FILE_DIRECTORY + "/" + uploadId + "/base/" + file.getOriginalFilename());
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Path dateFilePath = Paths.get(FILE_DIRECTORY + "/" + uploadId + "/base/date.txt");
        Files.write(dateFilePath, date.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Create the Pool instance
        Pool pool = new Pool();
        pool.setId(uploadId);
        pool.setPath(FILE_DIRECTORY + "/" + uploadId);
        pool.setDate(date);
        pool.setFiles(new ArrayList<>());
    }
}
