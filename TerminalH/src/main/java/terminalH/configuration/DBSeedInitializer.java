package terminalH.configuration;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Named
public class DBSeedInitializer {
    @Bean
    public ApplicationRunner initializer(SectionRepository sectionRepository, CategoryRepository categoryRepository) {
        return args ->
        {
            log.info("Start initializing DB seed");
            createOrUpdateSection("בגדים", Arrays.asList("סוודרים וסווטשירטים", "ג'קטים ומעילים", "מכנסיים", "חולצות", "שמלות וחצאיות", "חליפות ואוברולים", "בגדים", "גוזיות ספורט", "טייצים ארוכים", "טייצים קצרים", "חולצות ספורט", "מכנסי ספורט קצרים", "ג'קטים ועליוניות", "גופיות ספורט", "חליפות", "מכנסי ספורט ארוכים", "ביגוד", "סריגים וסוודרים", "סוויטשרטים וקפוצונים", "שמלות", "חצאיות", "DENIM", "בגדי גוף ואוברולים", "סטים ומארזים", "מכנסיים ואוברולים", "מארזי טי שרט"), sectionRepository, categoryRepository);
            createOrUpdateSection("אקססוריז", Arrays.asList("אקססוריז", "תיקים", "חגורות", "תכשיטים", "שעונים", "משקפי שמש", "צעיפים", "כובעים", "אביזרי שיער", "TECH + Gifts", "ACTIVE"), sectionRepository, categoryRepository);
            createOrUpdateSection("הלבשה תחתונה", Arrays.asList("הלבשה תחתונה", "פיג'מות והלבשה תחתונה", "תחתונים וגרביים", "מידות גדולות", "מחטבים", "גרבונים", "שינה ופנאי", "תחתונים", "חזיות", "בוקסרים"), sectionRepository, categoryRepository);
            createOrUpdateSection("בגדי ים", Arrays.asList("בגדי ים", "בגדי ים וחוף", "בגדי ים ובריכה", "ים ובריכה"), sectionRepository, categoryRepository);
            createOrUpdateSection("אביזרי ספורט", Arrays.asList("מכשירי כושר וציוד", "משחקים ופנאי", "ענפי ספורט", "אביזרי ספורט", "כדורגל"), sectionRepository, categoryRepository);
            createOrUpdateSection("ביוטי", Arrays.asList("ביוטי ולייף סטייל", "LIFESTYLE", "HAIR CARE", "פארם", "בישום", "מוצרי איפור", "טיפוח לפנים", "טיפוח לגוף", "טיפוח לשיער", "בישום וטיפוח", "לייף סטייל"), sectionRepository, categoryRepository);
            createOrUpdateSection("נעליים", Arrays.asList("נעליים", "סניקרס", "מגפיים ומגפונים", "סנדלים", "נעליים שטוחות", "נעלי עקב", "נעלי ספורט", "נעלי נוחות", "נעלי פלטפורמה", "נעלי בית", "כפכפים", "מגפיים", "נעלי עבודה", "נעליים קלאסיות", "נעלי עור", "נעלי בד", "נעלי הרים", "מגפיים ומגפונים"), sectionRepository, categoryRepository);
            log.info("Finished initializing DB seed");
        };
    }

    @Transactional
    private void createOrUpdateSection(String name, Collection<String> categoryNames, SectionRepository sectionRepository, CategoryRepository categoryRepository) {
        Section section = getOrSaveSection(name, sectionRepository);
        for (String categoryName : categoryNames) {
            Category category = getOrSaveCategory(categoryName, categoryRepository);
            if (category.getSection() == null) {
                category.setSection(section);
                categoryRepository.save(category);
            }
        }
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
