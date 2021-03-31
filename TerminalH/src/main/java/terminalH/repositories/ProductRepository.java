package terminalH.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import terminalH.entities.Product;

@Repository
@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByUrl(String url);

    Page<Product> findByCategorySectionId(@Param("sectionId") long sectionId, Pageable page);

    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable page);

    Page<Product> findByDescriptionContainingIgnoreCase(@Param("desc") String desc, Pageable page);
}
