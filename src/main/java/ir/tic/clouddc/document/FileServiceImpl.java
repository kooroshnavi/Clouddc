package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final MetaDataRepository metaDataRepository;

    private final LogService logService;


    @Autowired
    public FileServiceImpl(MetaDataRepository metaDataRepository, LogService logService) {
        this.metaDataRepository = metaDataRepository;
        this.logService = logService;
    }

    @Override
    public void checkAttachment(MultipartFile file, Persistence persistence) throws IOException {
        attachmentRegister(file, persistence);
    }

    private void attachmentRegister(MultipartFile file, Persistence persistence) throws IOException {
        DecimalFormat df = new DecimalFormat("###");
        MetaData metaData = new MetaData();
        metaData.setName(file.getOriginalFilename());
        metaData.setType(file.getContentType());
        var size = file.getSize() / 1024.0; // KB
        metaData.setSize(Float.parseFloat(df.format(size)));
        Attachment attachment = new Attachment();
        attachment.setDocument(file.getBytes());
        metaData.setAttachment(attachment);
        metaData.setPersistence(persistence);
        metaDataRepository.save(metaData);
    }

    @Override
    public List<MetaData> getRelatedMetadataList(List<Long> persistenceIDlist) {
        return metaDataRepository.fetchRelatedMetaDataList(persistenceIDlist);
    }

    @Override
    public MetaData getDocument(long id) {
        return metaDataRepository.findById(id).get();
    }

    @Override
    @PreAuthorize("documentOwner == requester")
    public void deleteDocument(long medaDataId, int documentOwner, int requester) {
        var persistence = metaDataRepository.fetchMetaDataPersistence(medaDataId);
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '5', new Person(documentOwner), persistence);
        metaDataRepository.deleteById(medaDataId);
    }

    @Override
    public int getDocumentOwner(long metaDataId) {
        return metaDataRepository.fetchMetaDataOwnerName(metaDataId);
    }
}
