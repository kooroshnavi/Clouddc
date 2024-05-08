package ir.tic.clouddc.file;

import ir.tic.clouddc.person.Person;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

public interface FileService {
    Attachment registerAttachment(MultipartFile file, LocalDateTime dateTime, Person person) throws IOException;
}
