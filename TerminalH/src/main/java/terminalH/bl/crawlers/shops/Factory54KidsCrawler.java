package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;

@Named
public class Factory54KidsCrawler extends Factory54Crawler {
    @Value("${FACTORY54_KIDS_NAME}")
    private String factory54KidsName;
    @Value("${FACTORY54_KIDS_CATEGORY_PATH}")
    private String kidsCategoryPath;

    @Override
    public Gender extractGender(Element product) {
        return Gender.KIDS;
    }

    @Override
    public String getShopUrl() {
        return super.getShopUrl() + kidsCategoryPath;
    }

    @Override
    public String getShopName() {
        return factory54KidsName;
    }
}
