package terminalH.bl.crawlers.shops;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import terminalH.entities.enums.Gender;

import javax.inject.Named;

@Named
public class Factory54WomenCrawler extends Factory54Crawler {
    private static final String NO_DESIGNER = "designer parameter missing";
    private static final String CURRENCY_SEPARATOR = " ";
    private static final String NEW_LINE = "\n";
    private static final int PRICE_IDX = 0;

    @Value("${FACTORY54_WOMEN_NAME}")
    private String factory54Name;

    @Override
    public Gender extractGender(Element product) {
        return Gender.WOMEN;
    }

    public String getShopName() {
        return factory54Name;
    }
}