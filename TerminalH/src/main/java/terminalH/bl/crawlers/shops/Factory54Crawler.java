package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static terminalH.utils.CrawlerUtils.*;
import static terminalH.utils.CurrencyUtils.parsePrice;

public abstract class Factory54Crawler extends AbstractShopCrawler {
    private static final String NO_DESIGNER = "designer parameter missing";
    private static final String ALL_PRODUCTS_QUERY = "?sz=10000000";
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String NEW_LINE = "\n";
    private static final int PRICE_IDX = 1;

    @Value("${FACTORY54_URL}")
    private String factory54Url;
    @Value("#{'${FACTORY54_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        Collection<Element> rawCategories = new ArrayList<>();
        getFirstElementByClass(getFirstElementByClass(landPage, "header-white__no-menu"),
                "header-white__category-section")
                .getElementsByClass("header-white__category").
                stream()
                .filter(topLevelCategory ->
                        !ignoreCategories.contains(topLevelCategory.select("a.header-white__category--id").first().text()))
                .collect(Collectors.toList())
                .forEach(topLevelCategory ->
                        rawCategories.addAll(extractSubCategories(topLevelCategory)));
        return rawCategories;
    }

    @Override
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        Optional<Element> rootContainer = Optional.ofNullable(getFirstElementByClass(categoryPage, "col-sm-12 col-md-9"));
        if (rootContainer.isPresent()) {
            return Optional.ofNullable(getFirstElementByClass(rootContainer.get(), "product-grid"));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return getElementsByClass(productContainer, "present-product product also-like__img");
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        Optional<Element> productUrlContainer =
                Optional.ofNullable(getFirstElementByClass(product, "link tile-body__product-name"));
        return productUrlContainer.isPresent() ?
                Optional.of(extractUrl(productUrlContainer.get())) : Optional.empty();
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "image-container present-product__image-container")
                .select("img").first().absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        Element rawPrice = getFirstElementByClass(product, "price-inverse");
        if (rawPrice == null) {
            return Optional.empty();
        }

        String price = rawPrice.text().split(CURRENCY_SEPARATOR)[PRICE_IDX];
        return parsePrice(price);
    }

    @Override
    public Optional<Float> extractOriginalProductPrice(Element product) {
        return Optional.empty();
    }

    @Override
    public String extractProductName(Element product) {
        return getFirstElementByClass(product, "product-name name-product__product ").text();
    }

    @Override
    public String extractDescription(Element product) {
        StringBuilder description = new StringBuilder();

        Optional<Element> info = Optional.ofNullable(
                getFirstElementByClass(product, "col-sm-12 col-md-8 col-lg-9 value content no-col card-body is-accordion-box"));
        Optional<Element> moreDetails = Optional.ofNullable(
                getFirstElementByClass(product, "col-sm-12 col-md-8 col-lg-9 no-col card-body is-accordion-box no-padding value content"));

        if (info.isPresent()) {
            description.append(info.get().text());
        }

        if (moreDetails.isPresent()) {
            description.append(NEW_LINE + moreDetails.get().text());
        }

        return description.toString();
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
        return extractUrl(rawCategory) + ALL_PRODUCTS_QUERY;
    }

    @Override
    public String extractBrand(Element product) {
        return getFirstElementByClass(product, "product-name name-product__brand ").text();
    }

    @Override
    public String[] extractExtraPictureUrls(Element product) {
        Elements picContainers = getElementsByClass(product, "carousel-indicators__list");
        String[] pics = new String[picContainers.size()];
        for (int picIdx = 0; picIdx < picContainers.size(); picIdx++) {
            pics[picIdx] = picContainers.get(picIdx).select("img").attr("src");
        }

        return pics;
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        return null;
    }

    @Override
    public String getShopUrl() {
        return factory54Url;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }

    private Collection<Element> extractSubCategories(Element category) {
        Collection<Element> midCategries = new ArrayList<>();
        midCategries = getElementsByClass(category, "header-white__subcategory header-white__subcategory--padding")
                .stream()
                .filter(raw -> !ignoreCategories.contains(raw.select("a").first().text()))
                .collect(Collectors.toList());

        Collection<Element> subCategories = new ArrayList<>();
        for (Element midCategory : midCategries) {
            subCategories.addAll(getElementsByClass(midCategory, "dropdown-link dropdown-toggle")
                    .stream()
                    .filter(sub -> !ignoreCategories.contains(sub.text()))
                    .collect(Collectors.toList()));
        }
        return subCategories;
    }
}
