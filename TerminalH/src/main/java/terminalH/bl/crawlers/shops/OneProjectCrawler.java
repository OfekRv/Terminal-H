package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.*;

@Named
public class OneProjectCrawler extends AbstractShopCrawler {
    private static final String CURRENCY_SEPARATOR = " ";
    private static final int PRICE_IDX = 0;
    private static final int PAGE_IDX = 1;

    @Value("${ONEPROJECT_URL}")
    private String oneProjectUrl;
    @Value("${ONEPROJECT_NAME}")
    private String oneProjectName;
    @Value("#{'${ONEPROJECT_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        return getElementsByClass(
                getFirstElementByClass(landPage, "header_desktop_wrapper"),
                "menu-link");
    }

    @Override
    public Element extractProductsContainer(Element categoryPage) {
        return getFirstElementByClass(categoryPage, "gallery-content-results twoColumns");
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return productContainer.select("div[data-item=product]");
    }

    @Override
    public String extractProductUrl(Element product) {
        return extractUrl(getFirstElementByClass(product, "gallery-content-results-item-imagebox"));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "gallery-content-results-item-imagebox")
                .select("img").first()
                .absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        Element rawPrice = getFirstElementByClass(product, "product_price_real");
        if (rawPrice == null) {
            return null;
        }

        String price = rawPrice.attr("data-price");
        try {
            return Optional.of(NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue());
        } catch (ParseException e) {
            getLogger().warn("Could not extract price");
        }

        return null;
    }

    @Override
    public String extractProductName(Element product) {
        return product.select("h1.product_title").text();
    }

    @Override
    public String extractDescription(Element product) {
        return getFirstElementByClass(product, "product_info_details_row_text description active").text();
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
        return getFirstElementByClass(product, "product-row").text();
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        Elements nextPage = categoryPage.select("a.next-page-link");

        if (nextPage.isEmpty()) {
            return null;
        }

        String nextPageUrl = extractUrl(nextPage.first());

        return nextPageUrl.equals(EMPTY) ? null : nextPageUrl;
    }

    @Override
    public String getShopUrl() {
        return oneProjectUrl;
    }

    @Override
    public String getShopName() {
        return oneProjectName;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }
}
