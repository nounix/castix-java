package rpi_cast.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channels;
import java.nio.file.StandardOpenOption;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void storeAsync(String inputFilePath, String targetFileName) {
        new Thread(() -> {
            try {
                // Files.deleteIfExists(this.rootLocation.resolve("uploadFile.tmp"));
                File initialFile = new File(inputFilePath);
                InputStream inputStream = new FileInputStream(initialFile);
                final ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
                final AsynchronousFileChannel outputChannel = AsynchronousFileChannel.open(
                                this.rootLocation.resolve(targetFileName),
                                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                outputChannel.lock();

                final ByteBuffer buffer = ByteBuffer.allocate(4096);
                int position = 0;
                int recievedBytes = 0;
                Future<Integer> lastWrite = null;
                int count = 0;
                while (count < 100) {
                    while ((recievedBytes = inputChannel.read(buffer)) >= 0 || buffer.position() != 0) {
                        count = 0;
                        // System.out.println("Recieved bytes: " + recievedBytes);
                        // System.out.println("Buffer position: " + buffer.position());
                        buffer.flip();
                        lastWrite = outputChannel.write(buffer, position);
                        position += recievedBytes;
                        if (lastWrite != null)  lastWrite.get();
                        buffer.compact();
                        
                    }
                    Thread.sleep(500);
                    count++;
                }
                
                outputChannel.close();
                inputChannel.close();
                inputStream.close();
            } catch (Exception e) {
                throw new StorageException("Failed to store file " + targetFileName, e);
            }
        }).start();
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
