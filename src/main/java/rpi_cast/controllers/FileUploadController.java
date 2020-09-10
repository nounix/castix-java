package rpi_cast.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;
import java.io.File;

import rpi_cast.storage.StorageFileNotFoundException;
import rpi_cast.storage.StorageService;
import rpi_cast.common.Tools;

@Controller
public class FileUploadController {

    private final StorageService storageService;
    private final String uploadFileTmpDir = System.getProperty("catalina.base") + "/work/Tomcat/localhost/ROOT";
    private boolean isStoreAsync = false;
    private String channelID = UUID.randomUUID().toString();

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

	@ResponseBody
	@GetMapping("/rpi-cast/get-channel")
    public String getChannelID() throws IOException {
        return channelID;
    }

    @PostMapping("/rpi-cast/store-file-async")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void storeFileAsync(@RequestParam("fileName") String fileName) {
        new Thread(() -> {
            Tools.sleep(5000);
            String filePath = storageService.load(fileName).toString();
            String uploadFileTmpPath = Tools.findFile(uploadFileTmpDir);
            if (uploadFileTmpPath != null) {
                isStoreAsync = true;
                if (! new File(filePath).exists()) {
                    storageService.storeAsync(uploadFileTmpPath, fileName);
                }
                Tools.runCommand(false, "xdg-open '" + filePath + "'");
            }
        }).start();
    }

    @PostMapping("/rpi-cast/upload-file")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void handleFileUpload(@RequestParam("file") MultipartFile file) {
        String filePath = storageService.load(file.getOriginalFilename()).toString();
        if (!isStoreAsync) {
            if (! new File(filePath).exists()) {
                storageService.store(file);
            }
            Tools.runCommand(false, "xdg-open '" + filePath + "'");
        }
        isStoreAsync = false;
    }
}
