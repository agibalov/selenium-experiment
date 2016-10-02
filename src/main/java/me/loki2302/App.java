package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public MessageProvider messageProvider() {
        return new MessageProvider();
    }

    @RestController
    public static class ApiController {
        @Autowired
        private MessageProvider messageProvider;

        @RequestMapping(value = "/api/message", method = RequestMethod.GET)
        public MessageDto getMessage() throws InterruptedException {
            Thread.sleep(1000);
            MessageDto messageDto = new MessageDto();
            messageDto.message = messageProvider.getMessage();
            return messageDto;
        }
    }

    public static class MessageDto {
        public String message;
    }
}
