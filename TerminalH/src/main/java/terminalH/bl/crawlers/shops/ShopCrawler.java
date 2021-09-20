package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;
import terminalH.bl.crawlers.Crawler;
import terminalH.entities.Brand;
import terminalH.entities.Category;
import terminalH.entities.Product;
import terminalH.entities.Shop;
import terminalH.entities.enums.Gender;
import terminalH.exceptions.TerminalHCrawlerException;
import terminalH.repositories.BrandRepository;
import terminalH.repositories.CategoryRepository;
import terminalH.repositories.ProductRepository;
import terminalH.repositories.ShopRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.getRequest;

public interface ShopCrawler extends Crawler<Shop> {
    default void crawl() throws TerminalHCrawlerException {
        Shop shop = getShopToCrawl();
        Optional<LocalDateTime> prevScanTime = Optional.ofNullable(shop.getLastScan());
        getLogger().info("Crawling shop: " + shop.getName());
        try {
            Document landPage = getRequest(shop.getUrl());
            Collection<Element> categories = extractRawCategories(landPage);
            categories.stream().
                    forEach(rawCategory -> {
                        try {
                            Optional<Category> category = extractCategory(rawCategory);
                            if (category.isPresent()) {
                                getLogger().info("Crawling category: " + category.get().getName());
                                crawlCategory(category.get(), extractCategoryUrl(rawCategory), shop);
                            }
                        } catch (TerminalHCrawlerException e) {
                            getLogger().error("Error while trying to crawl category in shop: " + shop.getName(), e);
                        }
                    });
        } catch (Exception e) {
            getLogger().error("Could not crawl shop " + shop.getName(), e);
            throw new TerminalHCrawlerException("Could not crawl shop " + shop.getName(), e);
        }

        getLogger().info("finished Crawling shop: " + shop.getName());
        updateLastScan(shop);
        if (prevScanTime.isPresent()) {
            getLogger().info("Cleaning out of stocks of shop: " + shop.getName());
            getProductRepository().deleteByLastScanBefore(prevScanTime.get());
            getLogger().info("Finished cleaning out of stocks of shop: " + shop.getName());
        }
    }

    default void crawlCategory(Category category, String categoryUrl, Shop shop) throws TerminalHCrawlerException {
        Optional<String> url = Optional.of(categoryUrl);
        Document pageOfCategory;
        do {
            try {
                pageOfCategory = getRequest(url.get());
            } catch (IOException e) {
                throw new TerminalHCrawlerException("Could not get category or page html", e);
            }

            Optional<Element> productsContainer = extractProductsContainer(pageOfCategory);
            if (productsContainer.isPresent()) {
                Collection<Element> products = extractRawProducts(productsContainer.get());
                products.stream().forEach(product -> crawlProduct(product, category, shop));
            }
            url = Optional.ofNullable(getNextPageUrl(pageOfCategory));
        } while (url.isPresent());
    }

    @Async
    default void crawlProduct(Element rawProduct, Category category, Shop shop) {
        Optional<String> productUrl = extractProductUrl(rawProduct);
        if (productUrl.isPresent()) {
            getLogger().info("Crawling product: " + productUrl.get());
            String picUrl = extractProductImageUrl(rawProduct);
            Document productPage = null;
            Optional<Float> price = Optional.empty();

            try {
                productPage = getRequest(productUrl.get());
                price = extractProductPrice(productPage);
            } catch (IOException e) {
                getLogger().error("Error while trying to crawl product: " + productUrl, e);
            }

            if (price.isPresent()) {
                if (!isInStock(productPage)) {
                    getLogger().info("Deleting product out of stock: " + productUrl);
                    getProductRepository().deleteByUrl(productUrl.get());
                } else {
                    if (getProductRepository().existsByUrl(productUrl.get())) {
                        updateProduct(productUrl.get(), price.get());
                    } else {
                        saveNewProduct(category, shop, productUrl.get(), picUrl, productPage, price.get());
                    }
                }
            }
        }
    }

    @Transactional
    default void saveNewProduct(Category category, Shop shop, String productUrl, String picUrl, Document productPage, Float price) {
        String name = extractProductName(productPage);
        String description = extractDescription(productPage);
        String brandName = extractBrand(productPage).toUpperCase();
        Brand brand = getBrandRepository().findByName(brandName).
                orElseGet(() -> getBrandRepository().save(new Brand(brandName)));
        Gender gender = extractGender(productPage);
        getLogger().info("Saving product (" + name + "): " + productUrl);
        getProductRepository().save(
                new Product(shop, productUrl, picUrl, name, category, brand, gender, description, price, LocalDateTime.now()));
    }

    @Transactional
    default void updateProduct(String productUrl, Float price) {
        Product alreadyExistProduct = getProductRepository().findByUrl(productUrl);
        if (alreadyExistProduct.getPrice() != price) {
            getLogger().info("Updating price of " + alreadyExistProduct.getName() + " to " + price);
            alreadyExistProduct.setPrice(price);
        }
        alreadyExistProduct.setLastScan(LocalDateTime.now());
        getProductRepository().save(alreadyExistProduct);
    }

    default Optional<Category> extractCategory(Element rawCategory) {
        String name = extractCategoryName(rawCategory);

        if (getIgnoredCategories().contains(name)) {
            return Optional.empty();
        }

        return Optional.of(getCategoryRepository().findByName(name).
                orElseGet(() -> getCategoryRepository().save(new Category(name))));
    }

    default Shop getShopToCrawl() {
        return getShopRepository().findByUrl(getShopUrl()).
                orElseGet(() -> getShopRepository().save(new Shop(getShopUrl(), getShopName())));
    }

    @Transactional
    default void updateLastScan(Shop shop) {
        shop.setLastScan(LocalDateTime.now());
        getShopRepository().save(shop);
    }

    Collection<Element> extractRawCategories(Element landPage);

    Optional<Element> extractProductsContainer(Element categoryPage);

    Collection<Element> extractRawProducts(Element productContainer);

    Optional<String> extractProductUrl(Element product);

    String extractProductImageUrl(Element product);

    Optional<Float> extractProductPrice(Element product);

    String extractProductName(Element product);

    String extractDescription(Element product);

    boolean isInStock(Element product);

    String extractCategoryName(Element rawCategory);

    String extractCategoryUrl(Element rawCategory);

    String extractBrand(Element product);

    Gender extractGender(Element product);

    String getNextPageUrl(Document categoryPage);

    String getShopUrl();

    String getShopName();

    ShopRepository getShopRepository();

    BrandRepository getBrandRepository();

    CategoryRepository getCategoryRepository();

    ProductRepository getProductRepository();

    Collection<String> getIgnoredCategories();

    Logger getLogger();
}
