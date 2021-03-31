package terminalH.entities.projections;

import org.springframework.data.rest.core.config.Projection;
import terminalH.entities.Brand;
import terminalH.entities.Category;
import terminalH.entities.Section;

@Projection(name = "detailedBrand", types = {Brand.class})
public interface BrandProjection {
    Long getId();

    String getName();
}
