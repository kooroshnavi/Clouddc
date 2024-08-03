package ir.tic.clouddc.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Initialization {
    @PostConstruct
    private void init() {
        UtilService.setDate();
        log.info("Initialized Date: " + UtilService.getDATE());
    }
}
