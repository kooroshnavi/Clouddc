package ir.tic.clouddc.document;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final MetaDataRepository metaDataRepository;

    private final LogService logService;


    @Autowired
    public FileServiceImpl(MetaDataRepository metaDataRepository, LogService logService) {
        this.metaDataRepository = metaDataRepository;
        this.logService = logService;
    }

    @Override
    public void registerAttachment(MultipartFile file, Persistence persistence) throws IOException {
        if (!file.isEmpty()) {
            attachmentRegister(file, persistence);
        }
    }

    private void attachmentRegister(MultipartFile file, Persistence persistence) throws IOException {
        DecimalFormat df = new DecimalFormat("###");
        MetaData metaData = new MetaData();
        metaData.setName(file.getOriginalFilename());
        metaData.setType(file.getContentType());
        var size = file.getSize() / 1024.0; // KB
        metaData.setSize(Float.parseFloat(df.format(size)));
        metaData.setUploadDate(UtilService.getDATE());
        Attachment attachment = new Attachment();
        attachment.setDocument(file.getBytes());
        metaData.setAttachment(attachment);
        metaData.setPersistence(persistence);
        metaData.setEnabled(true);

        metaDataRepository.save(metaData);
    }

    @Override
    public List<MetaData> getRelatedMetadataList(List<Long> persistenceIdList, boolean fullList) {
        List<MetaData> metadataList;
        if (fullList) {
            metadataList = metaDataRepository.fetchFullMetadataList(persistenceIdList);
        } else {
            metadataList = metaDataRepository.fetchRelatedMetaDataList(persistenceIdList, true);
        }
        if (!metadataList.isEmpty()) {
            for (MetaData metadata : metadataList) {
                metadata.setPersianUploadDate(UtilService.getFormattedPersianDate(metadata.getUploadDate()));
                if (!metadata.isEnabled()) {
                    metadata.setPersianRemoveDate(UtilService.getFormattedPersianDate(metadata.getRemoveDate()));
                }
            }
        }

        return metadataList;
    }

    @Override
    public Optional<MetaData> getDocument(Long id) {
        return metaDataRepository.findById(id);
    }

    @Override
    @PreAuthorize("#documentOwner == #requester")
    public void disableDocument(Long metadataId, @Param("documentOwner") Integer documentOwnerId, @Param("requester") Integer requesterId) {
        var deleteDate = UtilService.validateNextDue(UtilService.getDATE().plusDays(7));
        metaDataRepository.disableMetadata(metadataId, false, deleteDate);
        log.info(String.valueOf(documentOwnerId));
        log.info(String.valueOf(requesterId));
        var persistence = metaDataRepository.fetchMetaDataPersistence(metadataId);
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("DisableAttachment"), new Person(requesterId), persistence);
        log.info(String.valueOf(metadataId));
    }

    @Override
    public Integer getDocumentOwner(Long metaDataId) {
        return metaDataRepository.fetchMetaDataOwnerId(metaDataId);
    }

    @Override
    public boolean checkMetadata(Long metadataId) {
        return metaDataRepository.existsById(metadataId);
    }

    @Override
    public String scheduleDocumentRemoval(LocalDate date) {
        List<Long> removalDueIdList = metaDataRepository.getRemovalDueIdList(date, false);
        if (!removalDueIdList.isEmpty()) {
            metaDataRepository.deleteAllById(removalDueIdList);

            return removalDueIdList.size() + " attachment removed.";
        }
        return "No file were removed.";
    }

}
