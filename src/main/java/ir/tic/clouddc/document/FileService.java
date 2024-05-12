package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    Persistence registerAttachment(MultipartFile file, Persistence persistence) throws IOException;
}
