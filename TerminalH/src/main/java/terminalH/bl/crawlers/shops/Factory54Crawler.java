package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;

import static terminalH.utils.CrawlerUtils.*;

@Named
public class Factory54Crawler implements Crawler<Shop> {
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String NEW_LINE = "\n";
    private static final int PRICE_IDX = 0;
    private static final int PAGE_IDX = 1;

    @Inject
    private ShopRepository repository;
    @Inject
    private BrandRepository brandRepository;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private ProductRepository productRepository;

    @Value("${FACTORY54_URL}")
    private String factory54Url;
    @Value("${FACTORY54_NAME}")
    private String factory54Name;
    @Value("#{'${FACTORY54_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public void crawl() throws TerminalHCrawlerException {
        Shop shop = getShopToCrawl();

        try {
            Document landPage = getRequest(shop.getUrl());
            Collection<Element> categories = getFirstElementByClass(landPage, "nav_container").
                    select("li.level0");
            categories.stream().
                    forEach(rawCategory -> {
                        try {
                            Category category = extractCategory(rawCategory);
                            if (category != null) {
                                crawlCategory(category, shop);
                            }
                        } catch (TerminalHCrawlerException e) {
                            //TODO: log meeee
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            throw new TerminalHCrawlerException("Could not crawl shop " + shop.getName(), e);
        }

        shop.setLastScan(LocalDateTime.now());
        repository.save(shop);
    }

    private void crawlCategory(Category category, Shop shop) throws TerminalHCrawlerException {
        String url = category.getUrl();
        Document pageOfCategory;
        do {
            try {
                pageOfCategory = getRequest(url);
            } catch (IOException e) {
                throw new TerminalHCrawlerException("Could not get category or page html", e);
            }

            Element productsContainer = getFirstElementByClass(pageOfCategory, "group-product-item");
            if (productsContainer != null) {
                Collection<Element> products = getElementsByClass(productsContainer, "col3");
                products.stream().
                        forEach(product ->
                                crawlProduct(
                                        product,
                                        category,
                                        shop));
            }
            url = getNextPageUrl(pageOfCategory);
        } while (url != null);
    }

    private String getNextPageUrl(Document categoryPage) {
        String url = null;
        Element pages = getFirstElementByClass(categoryPage, "pages clearfix");
        if (pages != null) {
            Element rawNextPageLink = getFirstElementByClass(pages, "next");
            String nextPageUrl = null;
            if (rawNextPageLink != null) {
                url = extractUrl(rawNextPageLink);
            }
        }
        return url;
    }

    @Transactional
    private void crawlProduct(Element rawProduct, Category category, Shop shop) {
        String productUrl = extractUrl(rawProduct.select("h2").first());
        String picUrl = getFirstElementByClass(rawProduct, "lazy-img default").
                select("img").first().absUrl("data-src");
        if (!productRepository.existsByUrl(productUrl)) {
            try {
                Document productPage = getRequest(productUrl);
                Element rawPrice = getFirstElementByClass(productPage, "price-box");
                if (rawPrice != null) {
                    Element details = getFirstElementByClass(productPage, "showavimobile productHeader");
                    String name = details.select("p").text();
                    String price = rawPrice.text().split(CURRENCY_SEPARATOR)[PRICE_IDX];
                    Collection<Element> descriptions = getElementsByClass(productPage, "acc_container");

                    StringBuilder description = new StringBuilder();
                    descriptions.stream().forEach(desc -> description.append(NEW_LINE + desc.text()));

                    String brandName = details.select("a").text();
                    Brand brand = brandRepository.findByName(brandName).
                            orElseGet(() -> brandRepository.save(new Brand(brandName)));

                    productRepository.save(
                            new Product(shop,
                                    productUrl,
                                    picUrl,
                                    name,
                                    category,
                                    brand,
                                    description.toString(),
                                    NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue()));
                }
            } catch (IOException | ParseException e) {
                //TODO: log meeee
                e.printStackTrace();
            }
        }
    }

    private Category extractCategory(Element rawCategory) {
        rawCategory = rawCategory.select("a.level0").first();
        String categoryUrl = extractUrl(rawCategory);
        String name = rawCategory.text();

        if (ignoreCategories.contains(name)) {
            return null;
        }

        return categoryRepository.findByUrl(categoryUrl).
                orElseGet(() -> categoryRepository.save(new Category(name, categoryUrl)));
    }

    private Shop getShopToCrawl() {
        return repository.findByUrl(factory54Url).
                orElseGet(() -> repository.save(new Shop(factory54Url, factory54Name)));
    }
}
