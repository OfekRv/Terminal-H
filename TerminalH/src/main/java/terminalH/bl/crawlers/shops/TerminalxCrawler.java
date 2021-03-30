package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static terminalH.utils.CrawlerUtils.*;

@Named
public class TerminalxCrawler extends AbstractShopCrawler {
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String EMPTY = "";
    private static final int PRICE_IDX = 0;
    private static final int PAGE_IDX = 1;

    @Value("${TERMINALX_URL}")
    private String terminalxUrl;
    @Value("${TERMINALX_NAME}")
    private String terminalxName;
    @Value("#{'${TERMINALX_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        Collection<Element> rawCategories = new ArrayList<>();

        getFirstElementByClass(landPage, "navigation").select("li.level0")
                .stream()
                .filter(topLevelCategory ->
                        !ignoreCategories.contains(topLevelCategory.select("a").first().text()))
                .collect(Collectors.toList())
                .forEach(topLevelCategory ->
                        rawCategories.addAll(extractSubCategories(topLevelCategory)));

        return rawCategories;
    }

    @Override
    public Element extractProductsContainer(Element categoryPage) {
        return getFirstElementByClass(categoryPage, "products list items product-items");
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return productContainer.select("li").stream()
                .filter(product -> product.hasAttr("product-type"))
                .collect(Collectors.toList());
    }

    @Override
    public String extractProductUrl(Element product) {
        return extractUrl(
                getFirstElementByClass(product,
                        "product photo product-item-photo idus-lazy-fadeOLD product-item-link"));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "product-image-photo")
                .select("img").first()
                .absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        String price = product.select("span.price").first().text();
        price = price.split(CURRENCY_SEPARATOR)[PRICE_IDX];
        try {
            return Optional.of(NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue());
        } catch (ParseException e) {
            getLogger().warn("Could not extract price");
        }
        return null;
    }

    @Override
    public String extractProductName(Element product) {
        return getFirstElementByClass(product, "product name product-item-name").text();
    }

    @Override
    public String extractDescription(Element product) {
        // Not supported yet
        return EMPTY;
    }

    @Override
    public String extractCategoryName(Element rawCategory) {
        return rawCategory.text();
    }

    @Override
    public String extractCategoryUrl(Element rawCategory) {
        return extractUrl(rawCategory);
    }

    @Override
    public String extractBrand(Element product) {
        return getFirstElementByClass(product, "product-item-brand").text();
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        String url = null;
        Element pages = getFirstElementByClass(categoryPage, "items pages-items");
        if (pages != null) {
            int currentPage = Integer.parseInt(
                    getFirstElementByClass(pages, "item current").
                            select("span").get(PAGE_IDX).text());
            Elements nextPageElement = pages.select("li:contains(" + (currentPage + 1) + ")");
            if (!nextPageElement.isEmpty()) {
                url = extractUrl(nextPageElement.first());
            }
        }
        return url;

    }

    @Override
    public String getShopUrl() {
        return terminalxUrl;
    }

    @Override
    public String getShopName() {
        return terminalxName;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }

    private Collection<Element> extractSubCategories(Element category) {
        return getElementsByClass(category, "level1")
                .stream()
                .map(raw -> raw.select("a").first())
                .filter(raw -> !ignoreCategories.contains(raw.text()))
                .collect(Collectors.toList());
    }
}
