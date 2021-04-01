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
            createOrUpdateSection("בגדים",
                    Arrays.asList("סוודרים וסווטשירטים", "ג'קטים ומעילים", "מכנסיים", "חולצות", "שמלות וחצאיות", "חליפות ואוברולים", "בגדים"),
                    sectionRepository, categoryRepository);
            createOrUpdateSection("אקססוריז", Arrays.asList("אקססוריז", "תיקים"), sectionRepository, categoryRepository);
            createOrUpdateSection("הלבשה תחתונה", Arrays.asList("הלבשה תחתונה"), sectionRepository, categoryRepository);
            createOrUpdateSection("בגדי ים", Arrays.asList("בגדי ים"), sectionRepository, categoryRepository);
            createOrUpdateSection("אביזרי ספורט", Arrays.asList(), sectionRepository, categoryRepository);
            createOrUpdateSection("ביוטי", Arrays.asList("ביוטי ולייף סטייל"), sectionRepository, categoryRepository);
            createOrUpdateSection("נעליים", Arrays.asList("נעליים"), sectionRepository, categoryRepository);
        };
    }

    private void createOrUpdateSection(String name, Collection<String> categoryNames, SectionRepository sectionRepository, CategoryRepository categoryRepository) {
        Section section = sectionRepository.findByName(name).orElseGet(() -> new Section(name));
        for (String categoryName : categoryNames) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
            if (!isCategoryInSection(section, category)) {
                section.getCategories().add(category);
            }
        }
        sectionRepository.save(section);
    }

    private boolean isCategoryInSection(Section section, Category category) {
        return section.getCategories().stream().filter(c -> c.getName().equals(category.getName())).findAny().isPresent();
    }
}
