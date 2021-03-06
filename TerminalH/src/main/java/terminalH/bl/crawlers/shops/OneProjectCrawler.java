package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;
import java.util.Collection;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static terminalH.utils.CrawlerUtils.*;
import static terminalH.utils.CurrencyUtils.parsePrice;
import static terminalH.utils.CurrencyUtils.removeCurrencySymbol;

@Named
public class OneProjectCrawler extends AbstractShopCrawler {
    private static final String[] NO_SIZES = new String[0];
    private static final String SEPARATOR = " ";
    private static final int PRODUCT_COUNT_IDX = 0;
    private static final int GENDER_IDX = 3;

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
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        return Optional.ofNullable(getFirstElementByClass(categoryPage, "gallery-content-results twoColumns"));
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return productContainer.select("div[data-item=product]");
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        return Optional.ofNullable(
                extractUrl(getFirstElementByClass(product, "gallery-content-results-item-imagebox")));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "gallery-content-results-item-imagebox")
                .select("img").first()
                .absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        Optional<Element> rawPrice =
                Optional.ofNullable(getFirstElementByClass(product, "product_price_real"));
        if (!rawPrice.isPresent()) {
            return Optional.empty();
        }

        String price = rawPrice.get().attr("data-price");
        return parsePrice(price);
    }

    @Override
    public Optional<Float> extractOriginalProductPrice(Element product) {
        Optional<Element> rawPrice =
                Optional.ofNullable(getFirstElementByClass(product, "product_price_old"));
        if (!rawPrice.isPresent()) {
            return Optional.empty();
        }

        String price = removeCurrencySymbol(rawPrice.get().text());
        return parsePrice(price);
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
        return getFirstElementByClass(product, "product-row").text();
    }

    @Override
    public Gender extractGender(Element product) {
        Optional<Element> productCategoriesContainer = Optional.ofNullable(getFirstElementByClass(product, "breadcrumbs-wrapper"));
        if (!productCategoriesContainer.isPresent()) {
            return null;
        }

        Elements productCategories = getFirstElementByClass(product, "breadcrumb").select("span");
        if (productCategories.size() < GENDER_IDX + 1) {
            return null;
        }

        String rawGender = productCategories.get(GENDER_IDX).text();
        if (rawGender.equals("????????")) {
            return Gender.WOMEN;
        }
        if (rawGender.equals("??????????")) {
            return Gender.MEN;
        }
        if (rawGender.equals("??????????????")
                || rawGender.equals("????????")
                || rawGender.equals("????????")) {
            return Gender.KIDS;
        }
        return null;
    }

    @Override
    public String[] extractExtraPictureUrls(Element product) {
        Elements picContainers = getElementsByClass(product, "product_image_small_item");
        String[] pics = new String[picContainers.size() - 1];
        for (int picIdx = 0; picIdx < picContainers.size() - 1; picIdx++) {
            Optional<String> picUrl = Optional.ofNullable(picContainers.get(picIdx).select("img").attr("data-src-large"));
            if (picUrl.isPresent()) {
                pics[picIdx] = picUrl.get();
            }
        }

        return pics;
    }

    @Override
    public String[] extractSizes(Document productPage) {
        Optional<Element> sizes =
                Optional.ofNullable(getFirstElementByClass(productPage, "product_info_size_select_options"));

        if (!sizes.isPresent()) {
            return NO_SIZES;
        }

        return sizes.get().select("span")
                .stream().map(sizeContainer -> sizeContainer.text())
                .toArray(String[]::new);
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        Elements nextPage = categoryPage.select("a.next-page-link");

        if (nextPage.isEmpty()
                || parseBoolean(nextPage.first().attr("data-is-last"))
                || getCurrentProductsCount(categoryPage) == 0) {
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

    private int getCurrentProductsCount(Document categoryPage) {
        return Integer.parseInt(
                getFirstElementByClass(categoryPage, "gallery-content-results-info-count")
                        .text().split(SEPARATOR)[PRODUCT_COUNT_IDX]);
    }
}
