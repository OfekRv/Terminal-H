package terminalH.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import terminalH.entities.Product;
import terminalH.entities.QProduct;

import java.time.LocalDateTime;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product>,
        QuerydslBinderCustomizer<QProduct> {
    @RestResource(exported = false)
    boolean existsByUrl(String url);

    @RestResource(exported = false)
    Product findByUrl(String url);

    @RestResource(exported = false)
    void deleteByUrl(String url);

    @RestResource(exported = false)
    void deleteByLastScanBefore(LocalDateTime prevScanTime);

    @Override
    default void customize(QuerydslBindings bindings, QProduct product) {
        bindings.bind(product.name).first((name, value) -> name.containsIgnoreCase(value).or(product.brand.name.containsIgnoreCase(value)));
    }
}
