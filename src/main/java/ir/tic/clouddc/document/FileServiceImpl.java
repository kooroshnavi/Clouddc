package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {

    private final MetaDataRepository metaDataRepository;

    @Autowired
    public FileServiceImpl(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }

    @Override
    public Persistence registerAttachment(MultipartFile file, Persistence persistence) throws IOException {
        MetaData metaData = new MetaData();
        metaData.setName(file.getOriginalFilename());
        metaData.setType(file.getContentType());
        metaData.setSize((float) file.getSize());
        Attachment attachment = new Attachment();
        attachment.setDocument(file.getBytes());
        metaData.setAttachment(attachment);
        metaData.setPersistence(persistence);
        return metaDataRepository.save(metaData).getPersistence();
    }
}
