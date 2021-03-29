package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import terminalH.bl.crawlers.Crawler;
import terminalH.entities.Brand;
import terminalH.entities.Category;
import terminalH.entities.Product;
import terminalH.entities.Shop;
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
        getLogger().info("Crawling shop: " + shop.getName());
        try {
            Document landPage = getRequest(shop.getUrl());
            Collection<Element> categories = extractRawCategories(landPage);
            categories.stream().
                    forEach(rawCategory -> {
                        try {
                            Category category = extractCategory(rawCategory);
                            if (category != null) {
                                getLogger().info("Crawling category: " + category.getName());
                                crawlCategory(category, shop);
                            }
                        } catch (TerminalHCrawlerException e) {
                            getLogger().error("Error while trying to crawl category in shop - " + shop, e);
                        }
                    });
        } catch (Exception e) {
            getLogger().error("Could not crawl shop " + shop.getName(), e);
            throw new TerminalHCrawlerException("Could not crawl shop " + shop.getName(), e);
        }

        getLogger().info("finished Crawling shop: " + shop.getName());
        updateLastScan(shop);
    }


    default void crawlCategory(Category category, Shop shop) throws TerminalHCrawlerException {
        String url = category.getUrl();
        Document pageOfCategory;
        do {
            try {
                pageOfCategory = getRequest(url);
            } catch (IOException e) {
                throw new TerminalHCrawlerException("Could not get category or page html", e);
            }

            Element productsContainer = extractProductContainer(pageOfCategory);
            if (productsContainer != null) {
                Collection<Element> products = extractRawProducts(productsContainer);
                products.stream().forEach(product -> crawlProduct(product, category, shop));
            }
            url = getNextPageUrl(pageOfCategory);
        } while (url != null);
    }

    @Transactional
    default void crawlProduct(Element rawProduct, Category category, Shop shop) {
        String productUrl = extractProductUrl();
        getLogger().info("Crawling product: " + productUrl);
        String picUrl = extractProductImageUrl();
        if (!getProductRepository().existsByUrl(productUrl)) {
            try {
                Document productPage = getRequest(productUrl);
                Optional<Float> price = extractProductPrice();
                if (price.isPresent()) {
                    String name = extractProductName();
                    String description = extractDescription();
                    String brandName = extractBrand(rawProduct);
                    Brand brand = getBrandRepository().findByName(brandName).
                            orElseGet(() -> getBrandRepository().save(new Brand(brandName)));

                    getLogger().info("Saving product (" + name + "): " + productUrl);
                    getProductRepository().save(
                            new Product(shop, productUrl, picUrl, name, category, brand, description, price.get()));
                }
            } catch (IOException e) {
                getLogger().error("Error while trying to crawl product: " + productUrl, e);
            }
        }
    }

    default Category extractCategory(Element rawCategory) {
        String categoryUrl = extractCategoryUrl(rawCategory);
        String name = extractCategoryName(rawCategory);

        if (getIgnoredCategories().contains(name)) {
            return null;
        }

        return getCategoryRepository().findByUrl(categoryUrl).
                orElseGet(() -> getCategoryRepository().save(new Category(name, categoryUrl)));
    }

    default Shop getShopToCrawl() {
        return getShopRepository().findByUrl(getShopUrl()).
                orElseGet(() -> getShopRepository().save(new Shop(getShopUrl(), getShopName())));
    }

    default void updateLastScan(Shop shop) {
        shop.setLastScan(LocalDateTime.now());
        getShopRepository().save(shop);
    }

    Collection<Element> extractRawCategories(Element landPage);

    Element extractProductContainer(Element productContainer);

    Collection<Element> extractRawProducts(Element productContainer);

    String extractProductUrl();

    String extractProductImageUrl();

    Optional<Float> extractProductPrice();

    String extractProductName();

    String extractDescription();

    String extractCategoryName(Element rawCategory);

    String extractCategoryUrl(Element rawCategory);

    String extractBrand(Element product);

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
