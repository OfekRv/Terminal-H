package terminalH.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import terminalH.entities.Category;
import terminalH.entities.Section;
import terminalH.repositories.CategoryRepository;
import terminalH.repositories.SectionRepository;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;

@Named
public class DBSeedInitializer {
    @Bean
    @Transactional
    public ApplicationRunner initializer(SectionRepository sectionRepository, CategoryRepository categoryRepository) {
        return args ->
        {
            createOrUpdateSection("בגדים", Arrays.asList("סוודרים וסווטשירטים", "ג'קטים ומעילים", "מכנסיים", "חולצות", "שמלות וחצאיות", "חליפות ואוברולים", "בגדים", "גוזיות ספורט", "טייצים ארוכים", "טייצים קצרים", "חולצות ספורט", "מכנסי ספורט קצרים", "ג'קטים ועליוניות", "גופיות ספורט", "חליפות", "מכנסי ספורט ארוכים", "ביגוד", "סריגים וסוודרים", "סוויטשרטים וקפוצונים", "שמלות", "חצאיות", "DENIM"), sectionRepository, categoryRepository);
            createOrUpdateSection("אקססוריז", Arrays.asList("אקססוריז", "תיקים", "חגורות", "תכשיטים", "שעונים", "משקפי שמש", "צעיפים", "כובעים", "אביזרי שיער"), sectionRepository, categoryRepository);
            createOrUpdateSection("הלבשה תחתונה", Arrays.asList("הלבשה תחתונה", "פיג'מות והלבשה תחתונה"), sectionRepository, categoryRepository);
            createOrUpdateSection("בגדי ים", Arrays.asList("בגדי ים", "בגדי ים וחוף", "בגדי ים ובריכה", "ים ובריכה"), sectionRepository, categoryRepository);
            createOrUpdateSection("אביזרי ספורט", Arrays.asList("מכשירי כושר וציוד", "משחקים ופנאי", "ענפי ספורט", "אביזרי ספורט"), sectionRepository, categoryRepository);
            createOrUpdateSection("ביוטי", Arrays.asList("ביוטי ולייף סטייל"), sectionRepository, categoryRepository);
            createOrUpdateSection("נעליים", Arrays.asList("נעליים", "סניקרס", "מגפיים ומגפונים", "סנדלים", "נעליים שטוחות", "נעלי עקב", "נעלי ספורט", "נעלי נוחות", "נעלי פלטפורמה", "נעלי בית", "כפכפים", "מגפיים", "נעלי עבודה", "נעליים קלאסיות", "נעלי עור", "נעלי בד", "נעלי הרים"), sectionRepository, categoryRepository);
        };
    }

    @Transactional
    private void createOrUpdateSection(String name, Collection<String> categoryNames, SectionRepository sectionRepository, CategoryRepository categoryRepository) {
        Section section = getOrSaveSection(name, sectionRepository);
        for (String categoryName : categoryNames) {
            Category category = getOrSaveCategory(categoryName, categoryRepository);
            section.getCategories().add(category);
        }
        sectionRepository.save(section);
    }

    @Transactional
    private Section getOrSaveSection(String name, SectionRepository repository) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Section(name)));
    }

    @Transactional
    private Category getOrSaveCategory(String name, CategoryRepository repository) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Category(name)));
    }
}
