package edu.uoc.allago.UOCSubmissionSystemServer.common;

import edu.uoc.allago.UOCSubmissionSystemServer.model.Pool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PoolTools {
    private final String userHome = System.getProperty("user.home");
    private final String FILE_DIRECTORY = userHome + "/UOCSubmissionSystemPools";

    public List<Pool> getPools() throws IOException {
        List<Pool> pools = new ArrayList<>();
        Path directory = Paths.get(FILE_DIRECTORY);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    pools.add(createFromPath(path.toString()));
                }
            }
        }

        // Ordenar los pools por fecha de creación en orden inverso
        pools.sort(Comparator.comparing(Pool::getDate).reversed());

        return pools;
    }

    public Pool createFromPath(String pathString) throws IOException {
        Path path = Paths.get(pathString);
        Path basePath = path.resolve("base");

        if (!Files.isDirectory(basePath)) {
            throw new IOException("No base directory found at path: " + basePath);
        }

        String id = path.getFileName().toString(); // The folder name is the ID
        String dateFilePath = basePath.resolve("date.txt").toString();
        String dateStr;
        boolean active;

        try {
            dateStr = new String(Files.readAllBytes(Paths.get(dateFilePath)));
            LocalDate date = LocalDate.parse(dateStr);
            LocalDate today = LocalDate.now();
            active = !today.isAfter(date);
        } catch (IOException e) {
            // date.txt does not exist, set active as true
            dateStr = null;
            active = true;
        }

        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    files.add(entry.getFileName().toString());
                }
            }
        }
        return new Pool(id, path.toString(), dateStr, files, active);
    }

    public Pool findPoolById(String id) {
        Path path = Paths.get(FILE_DIRECTORY, id);
        try {
            return createFromPath(path.toString());
        } catch (IOException e) {
            // Logging the exception is recommended
            return null;
        }
    }

    public Path getFilePathForPool(String poolId, String filename) {
        // Crear el path completo al archivo
        return Paths.get(FILE_DIRECTORY, poolId, filename);
    }

    public Path getDirectoryPathForPool(String poolId){
        return Paths.get(FILE_DIRECTORY, poolId);
    }
}