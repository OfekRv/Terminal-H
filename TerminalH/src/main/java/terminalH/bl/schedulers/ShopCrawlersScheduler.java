package terminalH.bl.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import terminalH.bl.crawlers.ShopBl;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ShopCrawlersScheduler {
    @Inject
    private ShopBl bl;

    @Scheduled(fixedDelayString = "${CRAWLER_EXECUTION_FIXED_DELAY}",
            initialDelayString = "${CRAWLER_EXECUTION_INITIAL_DELAY}")
    public void populateOrUpdateShops() {
        bl.populateOrUpdateShops();
    }
}
