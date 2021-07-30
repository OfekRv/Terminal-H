package terminalH.bl.crawlers;

import terminalH.entities.Shop;
import terminalH.exceptions.TerminalHCrawlerException;
import terminalH.repositories.ProductRepository;
import terminalH.repositories.ShopRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Named
public class ShopBl {
    @Inject
    private ShopRepository shopRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private Collection<Crawler<Shop>> shopCrawlers;

    public void populateOrUpdateShops() {
        shopCrawlers.stream().forEach(crawler -> {
            try {
                crawler.crawl();
            } catch (TerminalHCrawlerException e) {
                e.printStackTrace();
            }
        });
    }
}
