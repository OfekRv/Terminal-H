package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;
import java.util.Collection;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.extractUrl;
import static terminalH.utils.CrawlerUtils.getFirstElementByClass;
import static terminalH.utils.CurrencyUtils.parsePrice;

@Named
public class AfroditaCrawler extends AbstractShopCrawler {
    private static final String[] NO_EXTRA_PICS = new String[0];
    private static final String[] NO_SIZES = new String[0];
    private static final String CURRENCY_SEPARATOR = " ";
    private static final int PRICE_IDX = 0;

    @Value("${AFRODITA_URL}")
    private String afroditaUrl;
    @Value("${AFRODITA_NAME}")
    private String afroditaName;
    @Value("#{'${AFRODITA_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        return getFirstElementByClass(landPage, "menu-all-pages-container").select("li.menu-full-width");
    }

    @Override
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        return Optional.ofNullable(
                getFirstElementByClass(categoryPage, "category-products")
                        .select("ul.products-grid").first());
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return productContainer.select("li");
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        return Optional.ofNullable(extractUrl(product.select("a").first()));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return product.select("img.defaultImage").attr("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        String price = product.select("span.price").first().text();
        price = price.split(CURRENCY_SEPARATOR)[PRICE_IDX];
        return parsePrice(price);
    }

    @Override
    public Optional<Float> extractOriginalProductPrice(Element product) {
        return Optional.empty();
    }

    @Override
    public String extractProductName(Element product) {
        return product.select("h1").text();
    }

    @Override
    public String extractDescription(Element product) {
        Optional<Element> description =
                Optional.ofNullable(getFirstElementByClass(product, "short-description no-border"));
        return description.isPresent() ? description.get().text() : null;
    }

    @Override
    public boolean isInStock(Element product) {
        return true;
    }

    @Override
    public String extractCategoryName(Element rawCategory) {
        return rawCategory.select("a").first().text();
    }

    @Override
    public String extractCategoryUrl(Element rawCategory) {
        return extractUrl(rawCategory.select("a").first());
    }

    @Override
    public String extractBrand(Element product) {
        return getFirstElementByClass(product, "product-brand").text();
    }

    @Override
    public Gender extractGender(Element product) {
        return Gender.WOMEN;
    }

    @Override
    public String[] extractExtraPictureUrls(Element product) {
        return NO_EXTRA_PICS;
    }

    @Override
    public String[] extractSizes(Document productPage) {
        return NO_SIZES;
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        Optional<Element> nextPageLink = Optional.ofNullable(getFirstElementByClass(categoryPage, "next i-next"));
        if (nextPageLink.isPresent()) {
            return extractUrl(nextPageLink.get());
        }
        return null;
    }

    @Override
    public String getShopUrl() {
        return afroditaUrl;
    }

    @Override
    public String getShopName() {
        return afroditaName;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }
}
