package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;

@Named
public class Factory54MenCrawler extends Factory54Crawler {
    @Value("${FACTORY54_MEN_NAME}")
    private String factory54MenName;
    @Value("${FACTORY54_MEN_CATEGORY_PATH}")
    private String menCategoryPath;

    @Override
    public Gender extractGender(Element product) {
        return Gender.MEN;
    }

    @Override
    public String getShopUrl() {
        return super.getShopUrl() + menCategoryPath;
    }

    @Override
    public String getShopName() {
        return factory54MenName;
    }

}
