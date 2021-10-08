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
import java.util.Optional;
import java.util.stream.Collectors;

import static terminalH.utils.CrawlerUtils.*;
import static terminalH.utils.CurrencyUtils.parsePrice;

@Named
public class TerminalxCrawler extends AbstractShopCrawler {
    private static final String[] NO_EXTRA_PICS = new String[0];
    private static final String SEARCH_QUERY = "?p=";
    private static final String SEARCH_QUERY_PATTERN = "\\" + SEARCH_QUERY;
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String TOTAL_PRODUCTS_SEPARATOR = " ";
    private static final String NO_PRODUCTS_MESSAGE = "There are no products matching the applied filters";
    private static final int FIRST_NEXT_PAGE = 2;
    private static final int PRICE_IDX = 0;
    private static final int GENDER_IDX = 1;
    private static final int CATEGORY_URL_IDX = 0;
    private static final int CURRENT_PAGE_INX = 1;
    private static final int TOTAL_PRODUCTS_IDX = 0;

    @Value("${TERMINALX_URL}")
    private String terminalxUrl;
    @Value("${TERMINALX_NAME}")
    private String terminalxName;
    @Value("#{'${TERMINALX_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        Collection<Element> rawCategories = new ArrayList<>();

        getFirstElementByClass(landPage, "header-navigation-desktop-container_3l4X").select("li.column_3mZ8")
                .stream()
                .filter(topLevelCategory ->
                        !ignoreCategories.contains(topLevelCategory.select("a").first().text()))
                .collect(Collectors.toList())
                .forEach(topLevelCategory ->
                        rawCategories.addAll(extractSubCategories(topLevelCategory)));

        return rawCategories;
    }

    @Override
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        return Optional.ofNullable(getFirstElementByClass(categoryPage, "product-list-wrapper_1--3"));
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return getElementsByClass(productContainer, "listing-product_3mjp").stream()
                .filter(product -> product.select("a").hasAttr("title"))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        return Optional.of(extractUrl(getFirstElementByClass(product, "img-link_29yX tx-link_29YD")));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        Optional<Element> optionalPic = Optional.ofNullable(getFirstElementByClass(product, "image-div_3hfI"));

        return optionalPic.isPresent() ?
                optionalPic.get().select("img").first().absUrl("src") : null;
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        return extractPriceFromElement(product, "row_2tcG bold_2wBM prices-final_1R9x");
    }

    @Override
    public Optional<Float> extractOriginalProductPrice(Element product) {
        return extractPriceFromElement(product, "row_2tcG strikethrough_t2Ab prices-regular_yum0");
    }

    @Override
    public String extractProductName(Element product) {
        return getFirstElementByClass(product, "name_20R6").text();
    }

    @Override
    public String extractDescription(Element product) {
        return getFirstElementByClass(product, "visible_3kIl").text();
    }

    @Override
    public boolean isInStock(Element product) {
        return !isElementExistByClass(product, "gray-bg_2Rf4");
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
        return getFirstElementByClass(product, "brand_2ltz").text();
    }

    @Override
    public Gender extractGender(Element product) {
        Elements productCategories = getFirstElementByClass(product, "breadcrums-container_YruD").select("li");

        if (productCategories.size() < GENDER_IDX + 1) {
            return null;
        }

        String rawGender = productCategories.get(GENDER_IDX).text();
        if (rawGender.equals("נשים")) {
            return Gender.WOMEN;
        }
        if (rawGender.equals("גברים")) {
            return Gender.MEN;
        }
        if (rawGender.equals("TEEN")
                || rawGender.equals("בנות")
                || rawGender.equals("בייבי בנות")
                || rawGender.equals("בייבי בנים")
                || rawGender.equals("בנים")) {
            return Gender.KIDS;
        }
        return null;
    }

    @Override
    public String[] extractExtraPictureUrls(Element product) {
        Elements picContainers = getElementsByClass(product, "thumb_2ID9");

        if (picContainers.isEmpty()) {
            return NO_EXTRA_PICS;
        }

        String[] pics = new String[picContainers.size() - 1];
        for (int picIdx = 0; picIdx < picContainers.size() - 1; picIdx++) {
            pics[picIdx] = picContainers.get(picIdx).select("img").attr("src");
        }

        return pics;
    }

    @Override
    public String getNextPageUrl(Document categoryPage) {
        if (isAllProductsViewed(categoryPage)) {
            return null;
        }

        String currentUrl = categoryPage.location();
        String nextPageUrl;
        if (!currentUrl.contains(SEARCH_QUERY)) {
            nextPageUrl = currentUrl + SEARCH_QUERY + FIRST_NEXT_PAGE;
        } else {
            String[] urlParts = currentUrl.split(SEARCH_QUERY_PATTERN);
            nextPageUrl =
                    urlParts[CATEGORY_URL_IDX] + SEARCH_QUERY + (Integer.parseInt(urlParts[CURRENT_PAGE_INX]) + 1);
        }

        try {
            Document nextPage = getRequest(nextPageUrl, IGNORE_REDIRECTS);
            Optional<Element> noProductsElement =
                    Optional.ofNullable(
                            getFirstElementByClass(nextPage, "info_dzi3 toast_hN0l rtl_1l4_ full-width_p5rD"));
            if (noProductsElement.isPresent() && noProductsElement.get().text().equals(NO_PRODUCTS_MESSAGE)) {
                return null;
            }
        } catch (IOException e) {
            // TODO: What we do here??
            return null;
        }

        return nextPageUrl;
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
        return getElementsByClass(category, "sub-list-item_3FNx")
                .stream()
                .map(raw -> raw.select("a").first())
                .filter(raw -> !ignoreCategories.contains(raw.text()))
                .collect(Collectors.toList());
    }

    private Optional<Float> extractPriceFromElement(Element element, String classQuery) {
        Optional<Element> priceElement = Optional.ofNullable(getFirstElementByClass(element, classQuery));
        if (!priceElement.isPresent()) {
            return Optional.empty();
        }

        String price = priceElement.get().text();
        price = price.split(CURRENCY_SEPARATOR)[PRICE_IDX];

        return parsePrice(price);
    }

    private boolean isAllProductsViewed(Document categoryPage) {
        Optional<Element> totalProductsContainer =
                Optional.ofNullable(categoryPage.select("span[data-test=search-totals]").first());
        Optional<Element> viewedProductsContainer =
                Optional.ofNullable(getFirstElementByClass(categoryPage, "listing-previous-viewed_2Nou"));

        if (!(totalProductsContainer.isPresent() && viewedProductsContainer.isPresent())) {
            return false;
        }

        int totalProducts = Integer.parseInt(totalProductsContainer.get().text());
        int viewedProducts = Integer.parseInt(viewedProductsContainer.get().text().
                split(TOTAL_PRODUCTS_SEPARATOR)[TOTAL_PRODUCTS_IDX]);

        // for some reason it is possible for TerminalX to write that there are more viewed products than total products
        return viewedProducts >= totalProducts;
    }
}
