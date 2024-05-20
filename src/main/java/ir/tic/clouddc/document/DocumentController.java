package ir.tic.clouddc.document;

import ir.tic.clouddc.person.PersonService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/download")
    public void getDocument(@RequestParam long metaDataId, HttpServletResponse response) throws IOException {
        MetaData metaData = fileService.getDocument(metaDataId);
        response.setContentType(metaData.getType());
        String fileName = URLEncoder.encode(metaData.getName(), StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = metaData.getAttachment().getDocument();
        outputStream.write(buffer);
        outputStream.close();
    }

    @GetMapping("/delete")
    public String deleteDocument(@RequestParam long metaDataId) {
        fileService.deleteDocument(
                metaDataId,
                fileService.getDocumentOwner(metaDataId),
                personService.getPersonId(SecurityContextHolder.getContext().getAuthentication().getName()));

        return "redirect:eventDetailList";
    }
}

