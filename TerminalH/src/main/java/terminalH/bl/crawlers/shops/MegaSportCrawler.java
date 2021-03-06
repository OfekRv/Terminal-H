package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;
import java.util.Collection;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.extractUrl;
import static terminalH.utils.CrawlerUtils.getFirstElementByClass;
import static terminalH.utils.CurrencyUtils.parsePrice;

@Named
public class MegaSportCrawler extends AbstractShopCrawler {
    private static final String[] NO_EXTRA_PICS = new String[0];
    private static final String[] NO_SIZES = new String[0];
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String GENDER_SEPARATOR = ",";
    private static final int PRICE_IDX = 0;
    private static final int PAGE_IDX = 1;
    private static final int GENDER_IDX = 0;
    private static final int SINGLE_GENDER = 1;

    @Value("${MEGASPORT_URL}")
    private String megasportUrl;
    @Value("${MEGASPORT_NAME}")
    private String megasportName;
    @Value("#{'${MEGASPORT_IGNORE_CATEGORIES}'.split(',')}")
    private Collection<String> ignoreCategories;

    @Override
    public Collection<Element> extractRawCategories(Element landPage) {
        return getFirstElementByClass(landPage, "navigation igormenu").select("li.level0");
    }

    @Override
    public Optional<Element> extractProductsContainer(Element categoryPage) {
        return Optional.ofNullable(getFirstElementByClass(categoryPage, "products list items product-items"));
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return productContainer.select("li");
    }

    @Override
    public Optional<String> extractProductUrl(Element product) {
        return Optional.ofNullable(
                extractUrl(getFirstElementByClass(product, "product details product-item-details wct3")));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "product-image-photo")
                .select("img").first().absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        return extractPriceFromElement(product, "price ar_finalPrice");
    }

    @Override
    public Optional<Float> extractOriginalProductPrice(Element product) {
        return extractPriceFromElement(product, "old-price sly-old-price");
    }

    @Override
    public String extractProductName(Element product) {
        return getFirstElementByClass(product, "page-title").text();
    }

    @Override
    public String extractDescription(Element product) {
        return getFirstElementByClass(product, "additional-attributes-wrapper_1  aaw_1").text();
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
        return extractUrl(rawCategory);
    }

    @Override
    public String extractBrand(Element product) {
        return product.select("div[data-th=????????]").text();
    }

    @Override
    public Gender extractGender(Element product) {
        Optional<Element> genderSite =
                Optional.ofNullable(getFirstElementByClass(product, "col_label_data_1w attr_gender_site"));

        if (genderSite.isPresent()) {
            String[] genders =
                    getFirstElementByClass(genderSite.get(), "col_data_1").text().split(GENDER_SEPARATOR);

            if (genders.length == SINGLE_GENDER) {
                if (genders[GENDER_IDX].equals("??????????")) {
                    return Gender.MEN;
                }
                if (genders[GENDER_IDX].equals("????????")) {
                    return Gender.WOMEN;
                }
            }

            if (genders[GENDER_IDX].equals("?????????? ??????????")
                    || genders[GENDER_IDX].equals("?????????? ????????????")
                    || genders[GENDER_IDX].equals("??????????????")) {
                return Gender.KIDS;
            }

            // Multiple genders & not kids, which means men + women
            return Gender.UNISEX;
        }
        return null;
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
        String url = null;
        Optional<Element> pages =
                Optional.ofNullable(getFirstElementByClass(categoryPage, "items pages-items"));
        if (pages.isPresent()) {
            int currentPage = Integer.parseInt(
                    getFirstElementByClass(pages.get(), "item current").
                            select("span").get(PAGE_IDX).text());
            Elements nextPageElement = pages.get().select("li:contains(" + (currentPage + 1) + ")");
            if (!nextPageElement.isEmpty()) {
                url = extractUrl(nextPageElement.first());
            }
        }
        return url;
    }

    @Override
    public String getShopUrl() {
        return megasportUrl;
    }

    @Override
    public String getShopName() {
        return megasportName;
    }

    @Override
    public Collection<String> getIgnoredCategories() {
        return ignoreCategories;
    }

    private Optional<Float> extractPriceFromElement(Element element, String classQuery) {
        Optional<Element> rawPrice = Optional.ofNullable(getFirstElementByClass(element, classQuery));
        if (!rawPrice.isPresent()) {
            return Optional.empty();
        }

        String price = rawPrice.get().text().split(CURRENCY_SEPARATOR)[PRICE_IDX];
        return parsePrice(price);
    }
}
