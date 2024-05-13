package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final MetaDataRepository metaDataRepository;

    @Autowired
    public FileServiceImpl(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    @Override
    public Persistence registerAttachment(MultipartFile file, Persistence persistence) throws IOException {
        DecimalFormat df = new DecimalFormat("###");
        MetaData metaData = new MetaData();
        metaData.setName(file.getOriginalFilename());
        metaData.setType(file.getContentType());
        var size = file.getSize() / 1024;
        metaData.setSize(Float.parseFloat(df.format(size))); // to KB
        Attachment attachment = new Attachment();
        attachment.setDocument(file.getBytes());
        metaData.setAttachment(attachment);
        metaData.setPersistence(persistence);
        return metaDataRepository.save(metaData).getPersistence();
    }

    @Override
    public List<MetaData> getRelatedMetadataList(List<Long> persistenceIDlist) {
       return metaDataRepository.fetchRelatedMetaDataList(persistenceIDlist);
    }

    @Override
    public MetaData getDocument(long id) {
        return metaDataRepository.findById(id).get();
    }
}
