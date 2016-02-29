package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);
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

    @Configuration
    @ComponentScan
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public MessageProvider messageProvider() {
            return new MessageProvider();
        }
    }

    public static class MessageDto {
        public String message;
    }
}
