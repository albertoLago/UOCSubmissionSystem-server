package edu.uoc.allago.UOCSubmissionSystemServer.controller;

import edu.uoc.allago.UOCSubmissionSystemServer.common.PoolTools;
import edu.uoc.allago.UOCSubmissionSystemServer.model.Pool;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/pools")
public class PoolController {
    private final PoolTools poolTools;
    private final Logger logger = LoggerFactory.getLogger(PoolController.class);

    public PoolController(PoolTools poolTools) {
        this.poolTools = poolTools;
    }

    @Autowired
    private ScheduledExecutorService executorService;

    @GetMapping
    public ResponseEntity<List<Pool>> getPools() {
        try {
            List<Pool> pools = poolTools.getPools();
            logger.info("Retrieved pools successfully. Count: {}", pools.size());
            return ResponseEntity.ok(pools);
        } catch (IOException e) {
            logger.error("Failed to retrieve pools due to an exception", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/view_files")
    public ResponseEntity<Pool> view_files(@RequestParam String poolId) {
        logger.info("Viewing files for poolId: {}", poolId);

        // Search for the corresponding pool from the provided ID
        Pool pool = poolTools.findPoolById(poolId);

        if (pool != null) {
            logger.info("Pool found for poolId: {}", poolId);
            return ResponseEntity.ok(pool);
        } else {
            logger.warn("No pool found for poolId: {}", poolId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String poolId, @RequestParam String filename) {
        logger.info("Downloading file: {}", filename);

        Path filePath = poolTools.getFilePathForPool(poolId, filename);

        try {
            File file = filePath.toFile();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            logger.error("Failed to download file due to an exception", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/downloadAll")
    public ResponseEntity<InputStreamResource> downloadAllFiles(@RequestParam String poolId) {
        logger.info("Downloading all files for poolId: {}", poolId);

        try {
            Path poolDirectoryPath = poolTools.getDirectoryPathForPool(poolId);

            // Create a temporary zip file to store all files
            File tempZipFile = File.createTempFile(poolId, ".zip");
            ZipFile zipFile = new ZipFile(tempZipFile);

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(CompressionMethod.STORE);

            // Add all files from the directory to the zip
            File[] files = poolDirectoryPath.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.isDirectory()) {  // No añade subdirectorios
                        zipFile.addFile(file, parameters);
                    }
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + poolId + ".zip");

            InputStreamResource resource = new InputStreamResource(new FileInputStream(tempZipFile));

            // Programa la eliminación del archivo ZIP para después de un retraso
            File finalTempZipFile = tempZipFile;
            executorService.schedule(() -> {
                if (!finalTempZipFile.delete()) {
                    logger.error("Failed to delete temporary zip file");
                }
            }, 1, TimeUnit.HOURS);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(finalTempZipFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            logger.error("Failed to download all files due to an exception", e);
            return ResponseEntity.status(500).build();
        }
    }
}