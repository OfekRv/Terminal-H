package terminalH.entities.projections;

import org.springframework.data.rest.core.config.Projection;
import terminalH.entities.Category;
import terminalH.entities.Section;

@Projection(name = "detailedSection", types = {Section.class})
public interface SectionProjection {
    Long getId();

    String getName();
}
