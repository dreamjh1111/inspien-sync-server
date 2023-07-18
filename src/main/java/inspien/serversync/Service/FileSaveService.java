package inspien.serversync.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileSaveService {

    public void uploadFile(MultipartFile file, String uploadPath) throws IOException {
        String filename = file.getOriginalFilename();
        Path targetPath = Path.of(uploadPath, filename);
        System.out.println(uploadPath);
        System.out.println(filename);
        System.out.println("파일 저장함");
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
