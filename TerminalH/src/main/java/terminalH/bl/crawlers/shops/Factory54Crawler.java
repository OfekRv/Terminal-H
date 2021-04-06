package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.*;

//@Named
public class Factory54Crawler extends AbstractShopCrawler {
    private static final String NO_DESIGNER = "designer parameter missing";
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String NEW_LINE = "\n";
    private static final int PRICE_IDX = 0;
    private static final int PAGE_IDX = 1;

    @Value("${FACTORY54_URL}")
    private String factory54Url;
    @Value("${FACTORY54_NAME}")
    private String factory54Name;
    @Value("#{'${FACTORY54_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        return getFirstElementByClass(landPage, "nav_container").select("li.level0");
    }

    @Override
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        return Optional.ofNullable(getFirstElementByClass(categoryPage, "group-product-item"));
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return getElementsByClass(productContainer, "col3");
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        Element titleElement = product.select("h2").first();
        return titleElement.text().toLowerCase().contains(NO_DESIGNER)
                ? Optional.empty() : Optional.ofNullable(extractUrl(titleElement));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "lazy-img default")
                .select("img").first()
                .absUrl("data-src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        Element rawPrice = getFirstElementByClass(product, "price-box");
        if (rawPrice == null) {
            return Optional.empty();
        }

        String price = rawPrice.text().split(CURRENCY_SEPARATOR)[PRICE_IDX];
        try {
            return Optional.of(NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue());
        } catch (ParseException e) {
            getLogger().warn("Could not extract price");
        }

        return Optional.empty();
    }

    @Override
    public String extractProductName(Element product) {
        return getFirstElementByClass(product, "showavimobile productHeader")
                .select("p").text();
    }

    @Override
    public String extractDescription(Element product) {
        StringBuilder description = new StringBuilder();
        getElementsByClass(product, "acc_container").stream()
                .forEach(desc -> description.append(NEW_LINE + desc.text()));

        return description.toString();
    }

    @Override
    public String extractCategoryName(Element rawCategory) {
        return rawCategory.select("a.level0").first().text();
    }

    @Override
    public String extractCategoryUrl(Element rawCategory) {
        return extractUrl(rawCategory);
    }

    @Override
    public String extractBrand(Element product) {
        return getFirstElementByClass(product, "showavimobile productHeader").select("a").text();
    }

    @Override
    public Gender extractGender(Element product) {
        // The default factory54 contains only woman products
        return Gender.WOMEN;
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        String url = null;
        Optional<Element> pages = Optional.ofNullable(getFirstElementByClass(categoryPage, "pages clearfix"));
        if (pages.isPresent()) {
            Optional<Element> rawNextPageLink =
                    Optional.ofNullable(getFirstElementByClass(pages.get(), "next"));
            String nextPageUrl = null;
            if (rawNextPageLink.isPresent()) {
                url = extractUrl(rawNextPageLink.get());
            }
        }
        return url;
    }

    @Override
    public String getShopUrl() {
        return factory54Url;
    }

    @Override
    public String getShopName() {
        return factory54Name;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }
}
