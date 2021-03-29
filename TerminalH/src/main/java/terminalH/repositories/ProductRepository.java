package terminalH.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import terminalH.entities.Product;
import terminalH.entities.projections.ProductProjection;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = ProductProjection.class)
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByUrl(String url);

    boolean existsByUrl(String url);
}
