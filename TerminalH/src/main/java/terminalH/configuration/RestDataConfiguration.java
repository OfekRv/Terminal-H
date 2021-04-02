package terminalH.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import terminalH.entities.*;

import javax.inject.Named;

@Named
public class RestDataConfiguration {
    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig(config -> {
            config.exposeIdsFor(Brand.class);
            config.exposeIdsFor(Product.class);
            config.exposeIdsFor(Category.class);
            config.exposeIdsFor(Section.class);
            config.exposeIdsFor(Shop.class);
        });
    }
}
