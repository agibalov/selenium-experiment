package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Controller
    public static class DummyController {
        @RequestMapping("/")
        @ResponseBody
        public String test() {
            return "<html><head><title>test111</title></head><body>hello there</body></html>";
        }
    }
}
