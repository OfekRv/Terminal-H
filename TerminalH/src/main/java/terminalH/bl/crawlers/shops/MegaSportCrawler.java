package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class MegaSportCrawler implements Crawler<Shop> {
    private static final String CURRENCY_SEPARATOR = " ";
    private static final int PRICE = 0;
    private static final int PAGE_IDX = 1;

    @Inject
    private ShopRepository repository;
    @Inject
    private BrandRepository brandRepository;
    @Inject
    private CategoryRepository categoryRepository;
    @Inject
    private ProductRepository productRepository;

    @Value("${MEGASPORT_URL}")
    private String megasportUrl;
    @Value("${MEGASPORT_NAME}")
    private String megasportName;

    @Override
    public void crawl() throws TerminalHCrawlerException {
        Shop shop = getShopToCrawl();

        try {
            Document landPage = getRequest(shop.getUrl());
            Collection<Element> categories = getFirstElementByClass(landPage, "navigation igormenu").
                    select("li.level0");
            categories.stream().
                    forEach(category -> {
                        try {
                            crawlCategory(extractCategory(category), shop);
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

            Collection<Element> products = getFirstElementByClass(pageOfCategory, "products list items product-items").
                    select("li");
            products.stream().
                    forEach(product ->
                            crawlProduct(
                                    product,
                                    category,
                                    shop));

            url = getNextPageUrl(pageOfCategory);
        } while (url != null);
    }

    private String getNextPageUrl(Document categoryPage) {
        String url = null;
        Element pages = getFirstElementByClass(categoryPage, "items pages-items");
        int currentPage = Integer.parseInt(
                getFirstElementByClass(pages, "item current").
                        select("span").get(PAGE_IDX).text());
        Elements nextPageElement = pages.select("li:contains(" + (currentPage + 1) + ")");
        String nextPageUrl = null;
        if (!nextPageElement.isEmpty()) {
            url = extractUrl(nextPageElement.first());
        }
        return url;
    }

    @Transactional
    private void crawlProduct(Element rawProduct, Category category, Shop shop) {
        String productUrl = extractUrl(getFirstElementByClass(rawProduct, "product details product-item-details wct3"));
        String picUrl = getFirstElementByClass(rawProduct, "product-image-photo").
                select("img").first().absUrl("src");
        if (!productRepository.existsByUrl(productUrl)) {
            try {
                Document productPage = getRequest(productUrl);
                String name = getFirstElementByClass(productPage, "page-title").text();
                String price = getFirstElementByClass(productPage, "price ar_finalPrice").text().
                        split(CURRENCY_SEPARATOR)[PRICE];
                String description = getFirstElementByClass(productPage, "additional-attributes-wrapper_1  aaw_1").text();
                String brandName = productPage.select("div[data-th=מותג]").text();
                Brand brand = brandRepository.findByName(brandName).
                        orElseGet(() -> brandRepository.save(new Brand(brandName)));

                productRepository.save(
                        new Product(shop,
                                productUrl,
                                picUrl,
                                name,
                                category,
                                brand,
                                description,
                                NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue()));
            } catch (IOException | ParseException e) {
                //TODO: log meeee
                e.printStackTrace();
            }
        }
    }

    private Category extractCategory(Element rawCategory) {
        String categoryUrl = extractUrl(rawCategory);
        return categoryRepository.findByUrl(categoryUrl).
                orElseGet(() -> categoryRepository.save(new Category(rawCategory.select("a").first().text(), categoryUrl)));
    }

    private Shop getShopToCrawl() {
        return repository.findByUrl(megasportUrl).
                orElseGet(() -> repository.save(new Shop(megasportUrl, megasportName)));
    }
}
