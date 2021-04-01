package terminalH.bl.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import terminalH.bl.crawlers.Crawler;
import terminalH.entities.Shop;
import terminalH.exceptions.TerminalHCrawlerException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

@Named
public class ShopCrawlersScheduler {
    @Inject
    private Collection<Crawler<Shop>> shopCrawlers;

    @Scheduled(fixedDelayString = "${CRAWLER_EXECUTION_FIXED_DELAY}", initialDelayString = "${CRAWLER_EXECUTION_INITIAL_DELAY}")
    public void executeCrawlers() {
        shopCrawlers.stream().forEach(crawler -> {
            try {
                crawler.crawl();
            } catch (TerminalHCrawlerException e) {
                e.printStackTrace();
            }
        });
    }
}
