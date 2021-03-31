package terminalH.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import terminalH.entities.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
}
