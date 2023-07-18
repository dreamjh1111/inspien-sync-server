package inspien.serversync.Service;

import inspien.serversync.Entity.WatchedFile;
import inspien.serversync.Repository.WatchedFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class FileCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(FileCheckerService.class);

    private final WatchedFileRepository watchedFileRepository;

    @Autowired
    public FileCheckerService(WatchedFileRepository watchedFileRepository) {
        this.watchedFileRepository = watchedFileRepository;
    }

    public void CheckAndMarkFiles() {
        List<WatchedFile> watchedFiles = watchedFileRepository.findByIsChangedfileTrue();
        for (WatchedFile watchedFile : watchedFiles) {
            String subPath = watchedFile.getServerPath();
            Path fullPath = Paths.get(subPath);
            if (Files.exists(fullPath)) {
                watchedFile.setChangedfile(false);
                watchedFileRepository.save(watchedFile);
                logger.info("File exists: {}", subPath);
            } else {
                logger.info("File does not exist: {}", subPath);
            }
        }
    }

    public void CheckDeleteFile() {
        List<WatchedFile> watchedFiles = watchedFileRepository.findByIsDeletedTrue();
        for (WatchedFile watchedFile : watchedFiles) {
            String serverPath = watchedFile.getServerPath();
            File file = new File(serverPath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                    System.out.println("폴더");
                } else {
                    deleteFile(file);
                    System.out.println("파일");
                }
                logger.info("Deleted file: {}", serverPath);
            } else {
                logger.info("File not found: {}", serverPath);
            }
            watchedFileRepository.delete(watchedFile);
        }
    }

    private void deleteFile(File file) {
        if (file.delete()) {
            logger.info("Deleted file: {}", file.getAbsolutePath());
        } else {
            logger.warn("Failed to delete file: {}", file.getAbsolutePath());
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    deleteFile(file);
                }
            }
        }
        if (directory.delete()) {
            logger.info("Deleted directory: {}", directory.getAbsolutePath());
        } else {
            logger.warn("Failed to delete directory: {}", directory.getAbsolutePath());
        }
    }

    public void SortAndCreateDirectories() {
        List<WatchedFile> watchedFiles = watchedFileRepository.findByIsChangedfileTrueAndType("directory");
        Collections.sort(watchedFiles, Comparator.comparingInt(this::countForwardSlash));

        for (WatchedFile watchedFile : watchedFiles) {
            logger.info("file: {}", watchedFile.getServerPath());
            String serverPath = watchedFile.getServerPath();
            int lastSlashIndex = serverPath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                String directoryPath = serverPath.substring(0, lastSlashIndex);
                File directory = new File(serverPath);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        logger.info("Created directory: {}", directoryPath);
                    } else {
                        logger.warn("Failed to create directory: {}", directoryPath);
                    }
                } else {
                    logger.info("이미 {} 디렉토리 있음", directory);
                }
            }
        }
    }

    private int countForwardSlash(WatchedFile watchedFile) {
        String subPath = watchedFile.getSubPath();
        int count = 0;
        for (char c : subPath.toCharArray()) {
            if (c == '/') {
                count++;
            }
        }
        return count;
    }

    @Scheduled(fixedRate = 2500) // 2.5초마다 실행
    public void checkAndMarkFilesScheduled() {
        CheckAndMarkFiles();
        CheckDeleteFile();
        SortAndCreateDirectories();
    }
}
