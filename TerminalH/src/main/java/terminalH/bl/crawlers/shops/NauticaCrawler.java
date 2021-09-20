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

@Named
public class NauticaCrawler extends AbstractShopCrawler {
    @Value("${NAUTICA_URL}")
    private String nauticaUrl;
    @Value("${NAUTICA_NAME}")
    private String nauticaName;
    @Value("#{'${NAUTICA_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        return getElementsByClass(landPage, "level0 submenu ui-menu ui-widget ui-widget-content ui-corner-all");
    }

    @Override
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        return Optional.ofNullable(getFirstElementByClass(categoryPage, "products wrapper grid products-grid"));
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return getElementsByClass(productContainer, "item product product-item");
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        return Optional.ofNullable(
                extractUrl(getFirstElementByClass(product, "product photo product-item-photo")));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "product-image-wrapper")
                .select("img").first()
                .absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        Optional<Element> rawPrices =
                Optional.ofNullable(getFirstElementByClass(product, "price-box price-final_price"));
        if (!rawPrices.isPresent()) {
            return Optional.empty();
        }

        String price = getElementsByClass(rawPrices.get(), "price-container price-final_price tax weee").last().attr("data-price-amount");
        try {
            return Optional.of(NumberFormat.getInstance(Locale.getDefault()).parse(price).floatValue());
        } catch (ParseException e) {
            getLogger().warn("Could not extract price");
        }

        return Optional.empty();
    }

    @Override
    public String extractProductName(Element product) {
        return product.select("h1.page-title").text();
    }

    @Override
    public String extractDescription(Element product) {
        return getFirstElementByClass(product, "product attribute features").text();
    }

    @Override
    public boolean isInStock(Element product) {
        return true;
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
        return nauticaName;
    }

    @Override
    public Gender extractGender(Element product) {
        return null;
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        return null;
    }

    @Override
    public String getShopUrl() {
        return nauticaUrl;
    }

    @Override
    public String getShopName() {
        return nauticaName;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }
}
