package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.*;
import static terminalH.utils.CurrencyUtils.parsePrice;

@Named
public class NauticaCrawler extends AbstractShopCrawler {
    private static final String[] NO_EXTRA_PICS = new String[0];
    private static final int FIRST_PRICE_IDX = 0;
    private static final int ORIGINAL_PRICE_IDX = 0;
    private static final int PRICE_IDX = 1;

    @Value("${NAUTICA_URL}")
    private String nauticaUrl;
    @Value("${NAUTICA_NAME}")
    private String nauticaName;
    @Value("#{'${NAUTICA_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        Elements parentCategories =
                getFirstElementByClass(landPage, "footer-sections")
                        .select("ul").first().select("li");

        Collection<Element> categories = new ArrayList<>();
        for (Element parentCategory : parentCategories) {
            categories.addAll(extractRawCategoriesFromParent(parentCategory));
        }

        return categories;
    }

    private Collection<Element> extractRawCategoriesFromParent(Element parentCategory) {
        try {
            Document categoriesContainer = getRequest(extractUrl(parentCategory), FOLLOW_REDIRECTS);
            return getFirstElementByClass(categoriesContainer, "o-list").select("li.level1");
        } catch (IOException e) {
            getLogger().warn("Could not extract categories");
        }
        return Collections.EMPTY_LIST;
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
        return extractPriceByIndex(product, PRICE_IDX);
    }

    @Override
    public Optional<Float> extractOriginalProductPrice(Element product) {
        return extractPriceByIndex(product, ORIGINAL_PRICE_IDX);
    }

    @Override
    public String extractProductName(Element product) {
        return product.select("h1.page-title").text();
    }

    @Override
    public String extractDescription(Element product) {
        Optional<Element> descriptionContainer = Optional.ofNullable(
                getFirstElementByClass(product, "product attribute features"));
        if (!descriptionContainer.isPresent()) {
            return EMPTY;
        }

        return descriptionContainer.get().text();
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
    public String[] extractExtraPictureUrls(Element product) {
        return NO_EXTRA_PICS;
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

    private Optional<Float> extractPriceByIndex(Element product, int priceIdx) {
        Optional<Element> rawPrices =
                Optional.ofNullable(getFirstElementByClass(product, "price-box price-final_price"));
        if (!rawPrices.isPresent()) {
            return Optional.empty();
        }

        Elements priceContainer =
                getElementsByClass(rawPrices.get(), "price-container price-final_price tax weee");
        if (priceContainer.isEmpty()) {
            return Optional.empty();
        }

        if (priceContainer.size() == 1) {
            priceIdx = FIRST_PRICE_IDX;
        }

        String price = getFirstElementByClass(priceContainer.get(priceIdx), "price").text();
        return parsePrice(price);
    }
}
