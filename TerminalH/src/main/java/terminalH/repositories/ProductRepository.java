package terminalH.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import terminalH.entities.Product;

import java.util.Collection;

@Repository
@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByUrl(String url);

    Collection<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    Collection<Product> findByDescriptionContainingIgnoreCase(@Param("desc") String desc);
}
