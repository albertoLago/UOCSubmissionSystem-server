package edu.uoc.allago.UOCSubmissionSystemServer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class SubmissionController {
    private String userHome = System.getProperty("user.home");

    private String FILE_DIRECTORY = userHome + "/UOCSubmissionSystemPools";

    @PostMapping("/upload/{uploadId}")
    public ResponseEntity<String> uploadFile(@PathVariable("uploadId") String uploadId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.getContentType().equals("application/zip")) {
                if (isSubmissionValid(uploadId, file)) {
                    return ResponseEntity.status(HttpStatus.OK).body("success");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Upload ID not found or deadline passed.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File type not allowed. Only ZIP files are accepted.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading the file.");
        }
    }

    private boolean isSubmissionValid(String uploadId, MultipartFile file) throws IOException {
        Path directoryPath = Paths.get(FILE_DIRECTORY + "/" + uploadId);
        if (Files.exists(directoryPath)) {
            Path filePath = directoryPath.resolve(file.getOriginalFilename());
            Path dateFilePath = directoryPath.resolve("/base/date.txt");
            if (Files.exists(dateFilePath)) {
                String deadlineDateString = new String(Files.readAllBytes(dateFilePath)).trim();
                LocalDate deadlineDate = LocalDate.parse(deadlineDateString, DateTimeFormatter.ISO_LOCAL_DATE);
                if (LocalDate.now().isAfter(deadlineDate)) {
                    return false; // If current date is after the deadline, reject the submission
                }
            }
            Files.write(filePath, file.getBytes());
            return true;
        } else {
            return false;
        }
    }
}
