package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    void checkAttachment(MultipartFile file, Persistence persistence) throws IOException;

    List<MetaData> getRelatedMetadataList(List<Long> persistenceIdList);

    MetaData getDocument(long id);

    void deleteDocument(Long medaDataId, int documentOwner, int requester);

    int getDocumentOwner(Long metaDataId);

}
