package terminalH.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
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
            config.getExposureConfiguration().forDomainType(Brand.class).withItemExposure((metadata, httpmethods) ->
                    httpmethods.disable(HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST));
            config.getExposureConfiguration().forDomainType(Product.class).withItemExposure((metadata, httpmethods) ->
                    httpmethods.disable(HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST));
            config.getExposureConfiguration().forDomainType(Category.class).withItemExposure((metadata, httpmethods) ->
                    httpmethods.disable(HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST));
            config.getExposureConfiguration().forDomainType(Section.class).withItemExposure((metadata, httpmethods) ->
                    httpmethods.disable(HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST));
            config.getExposureConfiguration().forDomainType(Shop.class).withItemExposure((metadata, httpmethods) ->
                    httpmethods.disable(HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST));
        });
    }
}
