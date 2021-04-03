package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.Shop;
import terminalH.entities.enums.Gender;

import javax.inject.Named;

@Named
public class Factory54Men extends Factory54Crawler {
    @Value("${FACTORY54_MEN_CATEGORY_PATH}")
    private String menCategoryPath;

    @Override
    public Shop getShopToCrawl() {
        return getShopRepository().findByUrl(super.getShopUrl()).
                orElseGet(() -> getShopRepository().save(new Shop(getShopUrl(), getShopName())));
    }

    @Override
    public Gender extractGender(Element product) {
        return Gender.MEN;
    }

    @Override
    public String getShopUrl() {
        return super.getShopUrl() + menCategoryPath;
    }
}
