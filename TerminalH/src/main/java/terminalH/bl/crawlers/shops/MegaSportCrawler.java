package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import static terminalH.utils.CrawlerUtils.extractUrl;
import static terminalH.utils.CrawlerUtils.getFirstElementByClass;

@Named
public class MegaSportCrawler extends AbstractShopCrawler {
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
    public Element extractProductsContainer(Element categoryPage) {
        return getFirstElementByClass(categoryPage, "products list items product-items");
    }

    @Override
    public Collection<Element> extractRawProducts(Element productContainer) {
        return productContainer.select("li");
    }

    @Override
    public String extractProductUrl(Element product) {
        return extractUrl(getFirstElementByClass(product, "product details product-item-details wct3"));
    }

    @Override
    public String extractProductImageUrl(Element product) {
        return getFirstElementByClass(product, "product-image-photo")
                .select("img").first()
                .absUrl("src");
    }

    @Override
    public Optional<Float> extractProductPrice(Element product) {
        Element rawPrice = getFirstElementByClass(product, "price ar_finalPrice");
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
        return getFirstElementByClass(product, "page-title").text();
    }

    @Override
    public String extractDescription(Element product) {
        return getFirstElementByClass(product, "additional-attributes-wrapper_1  aaw_1").text();
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
        return product.select("div[data-th=מותג]").text();
    }

    @Override
    public Gender extractGender(Element product) {
        Optional<Element> genderSite =
                Optional.ofNullable(getFirstElementByClass(product, "col_label_data_1w attr_gender_site"));

        if (genderSite.isPresent()) {
            String[] genders =
                    getFirstElementByClass(genderSite.get(), "col_data_1").text().split(GENDER_SEPARATOR);

            if (genders.length == SINGLE_GENDER) {
                if (genders[GENDER_IDX].equals("גברים")) {
                    return Gender.MEN;
                }
                if (genders[GENDER_IDX].equals("נשים")) {
                    return Gender.WOMEN;
                }
            }
            return Gender.UNISEX;
        }
        return null;
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
}
