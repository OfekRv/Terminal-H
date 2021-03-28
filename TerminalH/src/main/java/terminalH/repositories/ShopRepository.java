package terminalH.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import terminalH.entities.Shop;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByUrl(String url);

    Collection<Shop> findAllByOrderByLastScanAsc();
}
