package ir.tic.clouddc.document;

import ir.tic.clouddc.individual.PersonService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/document")
@Slf4j
public class DocumentController {

    private final FileService fileService;

    private final PersonService personService;

    @Autowired
    public DocumentController(FileService fileService, PersonService personService) {
        this.fileService = fileService;
        this.personService = personService;
    }

    @GetMapping("/{metaDataId}/download")
    public void getDocument(HttpServletResponse response, @PathVariable Long metaDataId) throws IOException {
        MetaData metaData = fileService.getDocument(metaDataId);
        response.setContentType(metaData.getType());
        String fileName = URLEncoder.encode(metaData.getName(), StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = metaData.getAttachment().getDocument();
        outputStream.write(buffer);
        outputStream.close();
    }

    @GetMapping("/{metaDataId}/delete")
    public String deleteDocument(@PathVariable Long metaDataId) {
        fileService.deleteDocument(metaDataId, fileService.getDocumentOwner(metaDataId), personService.getCurrentPerson().getId());

        return "404";
    }
}

