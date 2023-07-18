package inspien.serversync.Controller;

import inspien.serversync.Service.FileSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {

    private final FileSaveService fileSaveService;

    @Autowired
    public FileUploadController(FileSaveService fileSaveService) {
        this.fileSaveService = fileSaveService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("filepath") String filePath) {
        try {
            String uploadPath = filePath; // 파일을 저장할 경로 설정
            fileSaveService.uploadFile(file, uploadPath);
            return ResponseEntity.ok("File uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed!");
        }
    }
}
