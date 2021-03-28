package terminalH.bl.crawlers;

import terminalH.exceptions.TerminalHCrawlerException;

public interface Crawler<T> {
    public void crawl() throws TerminalHCrawlerException;
}
