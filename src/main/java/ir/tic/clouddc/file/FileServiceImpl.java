package ir.tic.clouddc.file;

import ir.tic.clouddc.person.Person;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public Attachment registerAttachment(MultipartFile file, LocalDateTime dateTime, Person person) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setName(file.getOriginalFilename());
        attachment.setLocalDateTime(dateTime);
        attachment.setUploader(person);
        attachment.setDocument(file.getBytes());
        return attachment;
    }
}
