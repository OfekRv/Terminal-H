package terminalH.bl.crawlers.shops;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import terminalH.repositories.BrandRepository;
import terminalH.repositories.CategoryRepository;
import terminalH.repositories.ProductRepository;
import terminalH.repositories.ShopRepository;

import javax.inject.Inject;

@Slf4j
public abstract class AbstractShopCrawler implements ShopCrawler {
    @Inject
    private ShopRepository repository;
    @Inject
    private BrandRepository brandRepository;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private ProductRepository productRepository;

    public ShopRepository getShopRepository() {
        return repository;
    }

    public BrandRepository getBrandRepository() {
        return brandRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public Logger getLogger() {
        return log;
    }
}
