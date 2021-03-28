package terminalH.bl.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
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

    @Scheduled(cron = "${CRAWLER_EXECUTION_TIMING}")
    public void executeCrawlers() {
        shopCrawlers.stream().forEach(crawler -> {
            try {
                crawler.crawl();
            } catch (TerminalHCrawlerException e) {
                // TODO: log meee
                e.printStackTrace();
            }
        });
    }
}
