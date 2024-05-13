package ir.tic.clouddc.document;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public DocumentController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/download")
    public void getDocument(@RequestParam long id, HttpServletResponse response) throws IOException {
        MetaData metaData = fileService.getDocument(id);
        response.setContentType(metaData.getType());
        String fileName = URLEncoder.encode(metaData.getName(), StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = metaData.getAttachment().getDocument();
        outputStream.write(buffer);
        outputStream.close();
    }
}

