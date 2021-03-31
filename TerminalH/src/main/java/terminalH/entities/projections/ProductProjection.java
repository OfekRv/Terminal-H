package terminalH.entities.projections;

import org.springframework.data.rest.core.config.Projection;
import terminalH.entities.Brand;
import terminalH.entities.Category;
import terminalH.entities.Product;
import terminalH.entities.Shop;

@Projection(name = "detailedProduct", types = {Product.class})
public interface ProductProjection {
    Long getId();

    Shop getShop();

    String getUrl();

    String getPictureUrl();

    String getName();

    Category getCategory();

    Brand getBrand();

    String getDescription();

    float getPrice();
}
