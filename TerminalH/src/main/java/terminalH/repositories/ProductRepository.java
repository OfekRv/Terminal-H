package terminalH.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import terminalH.entities.Product;
import terminalH.entities.QProduct;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product>,
        QuerydslBinderCustomizer<QProduct> {
    @RestResource(exported = false)
    boolean existsByUrl(String url);

    @RestResource(exported = false)
    Product findByUrl(String url);

    @Override
    default void customize(QuerydslBindings bindings, QProduct product) {
        bindings.bind(product.name).first((name, value) -> name.containsIgnoreCase(value));
    }
}
