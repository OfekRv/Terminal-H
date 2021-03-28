package terminalH;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TerminalHApplication {
    public static void main(String[] args) {
        SpringApplication.run(TerminalHApplication.class, args);
    }
}
