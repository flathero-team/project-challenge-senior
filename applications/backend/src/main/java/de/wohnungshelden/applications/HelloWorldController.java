package de.wohnungshelden.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloWorldController {

    @RequestMapping("/")
    public String hello() {
        log.info("Hello World was called!");
        return "Hello World";
    }

}
