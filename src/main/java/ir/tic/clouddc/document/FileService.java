package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileService {

    void registerAttachment(MultipartFile file, Persistence persistence) throws IOException;

    List<MetaData> getRelatedMetadataList(List<Long> persistenceIdList, boolean fullList);

    Optional<MetaData> getDocument(Long id);

    void disableDocument(Long medaDataId, Integer documentOwnerId,  Integer requesterId);

    Integer getDocumentOwner(Long metaDataId);

    boolean checkMetadata(Long metadataId);

}
