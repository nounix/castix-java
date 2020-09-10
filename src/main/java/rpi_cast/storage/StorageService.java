package rpi_cast.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;
import java.io.InputStream;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    void storeAsync(String inputFilePath, String targetFileName);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
