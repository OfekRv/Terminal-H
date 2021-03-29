package terminalH.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import terminalH.entities.Brand;
import terminalH.entities.Category;
import terminalH.entities.Product;
import terminalH.entities.Shop;

@Configuration
public class SpringDataRestConf extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Shop.class);
        config.exposeIdsFor(Category.class);
        config.exposeIdsFor(Brand.class);
        config.exposeIdsFor(Product.class);
    }
}