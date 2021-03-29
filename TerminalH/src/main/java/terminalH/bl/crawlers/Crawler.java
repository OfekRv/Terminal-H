package terminalH.bl.crawlers;

import terminalH.exceptions.TerminalHCrawlerException;

public interface Crawler<T> {
    void crawl() throws TerminalHCrawlerException;
}
