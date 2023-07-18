package inspien.serversync.Repository;

import inspien.serversync.Entity.WatchedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.file.Path;
import java.util.List;

public interface WatchedFileRepository extends JpaRepository<WatchedFile, Long> {

    List<WatchedFile> findByIsChangedfileTrue();

    List<WatchedFile> findByIsDeletedTrue();

    List<WatchedFile> findByIsChangedfileTrueAndType(String directory);
}
