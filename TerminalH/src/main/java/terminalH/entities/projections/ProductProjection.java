package terminalH.entities.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import terminalH.entities.*;

@Projection(name = "detailedProduct", types = {Product.class})
public interface ProductProjection {
    Long getId();

    Shop getShop();

    String getUrl();

    String getPictureUrl();

    String getName();

    Category getCategory();

    @Value("#{target.category.section}")
    Section getCategorySection();

    Brand getBrand();

    @Value("#{target.brand.id}")
    Long getBrandId();

    String getDescription();

    float getPrice();
}
