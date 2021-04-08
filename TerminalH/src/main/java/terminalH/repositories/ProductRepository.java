package terminalH.repositories;

import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import terminalH.entities.Product;
import terminalH.entities.QProduct;

@Repository
@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product>,
        QuerydslBinderCustomizer<QProduct> {
    @RestResource(exported = false)
    boolean existsByUrl(String url);

    @RestResource(exported = false)
    Product findByUrl(String url);

    Page<Product> findByCategorySectionId(@Param("sectionId") long sectionId, Pageable page);

    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable page);

    @Override
    default void customize(QuerydslBindings bindings, QProduct product) {
        bindings.bind(product.name).first((name, value) -> name.containsIgnoreCase(value));
    }
}
