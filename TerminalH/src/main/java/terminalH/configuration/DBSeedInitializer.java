package terminalH.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import terminalH.entities.Section;
import terminalH.repositories.SectionRepository;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Arrays;

@Named
public class DBSeedInitializer {
    @Bean
    @Transactional
    public ApplicationRunner initializer(SectionRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(Arrays.asList(
                        new Section("בגדים"),
                        new Section("אקססוריז"),
                        new Section("הלבשה תחתונה"),
                        new Section("בגדי ים"),
                        new Section("אביזרי ספורט"),
                        new Section("ביוטי"),
                        new Section("נעליים")
                ));
            }
        };
    }
}
