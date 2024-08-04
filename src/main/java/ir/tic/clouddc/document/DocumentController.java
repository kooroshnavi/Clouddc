package ir.tic.clouddc.document;

import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.log.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/document")
@Slf4j
public class DocumentController {

    private final FileService fileService;

    private final PersonService personService;

    private final LogService logService;


    @Autowired
    public DocumentController(FileService fileService, PersonService personService, LogService logService) {
        this.fileService = fileService;
        this.personService = personService;
        this.logService = logService;
    }

    @GetMapping("/list")
    public String getAttachmentList(Model model) {
        List<Long> persistenceIdList = logService.getPersistenceIdList(personService.getCurrentPerson().getId());
        List<MetaData> metaDataList = fileService.getRelatedMetadataList(persistenceIdList, true);
        model.addAttribute("metadataList", metaDataList);

        return "attachmentList";
    }

    @GetMapping("/{metaDataId}/download")
    public void getDocument(HttpServletResponse response, @PathVariable Long metaDataId) throws IOException {
        Optional<MetaData> optionalMetaData = fileService.getDocument(metaDataId);
        if (optionalMetaData.isEmpty()) {
            throw new FileNotFoundException();
        }
        var metaData = optionalMetaData.get();
        response.setContentType(metaData.getType());
        String fileName = URLEncoder.encode(metaData.getName(), StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment; filename*=UTF-8''" + fileName);
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = metaData.getAttachment().getDocument();
        outputStream.write(buffer);
        outputStream.close();
    }

    @GetMapping("/{metadataId}/disable")
    public String disableDocument(@PathVariable Long metadataId) {
        var exist = fileService.checkMetadata(metadataId);
        if (!exist) {
            return "404";
        }
        fileService.disableDocument(metadataId, fileService.getDocumentOwner(metadataId), personService.getCurrentPerson().getId());

        return "redirect:/document/list";
    }

}

