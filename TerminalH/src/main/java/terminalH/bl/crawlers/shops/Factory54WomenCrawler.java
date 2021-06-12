package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;

@Named
public class Factory54WomenCrawler extends Factory54Crawler {
    @Value("${FACTORY54_WOMEN_NAME}")
    private String factory54Name;
    @Value("${FACTORY54_WOMEN_CATEGORY_PATH}")
    private String womenCategoryPath;

    @Override
    public Gender extractGender(Element product) {
        return Gender.WOMEN;
    }

    @Override
    public String getShopUrl() {
        return super.getShopUrl() + womenCategoryPath;
    }

    @Override
    public String getShopName() {
        return factory54Name;
    }
}