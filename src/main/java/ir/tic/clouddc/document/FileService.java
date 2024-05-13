package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    Persistence registerAttachment(MultipartFile file, Persistence persistence) throws IOException;
    List<MetaData> getRelatedMetadataList(List<Long> persistenceIDlist);

    MetaData getDocument(long id);
}
