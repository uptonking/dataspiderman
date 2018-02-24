package us.codecraft.webmagic.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

/**
 * 页面解析处理接口
 * 需要开发者根据爬虫需求实现
 * <p>
 * Interface to be implemented to customize a crawler.<br>
 * <br>
 * In PageProcessor, you can customize:
 * <br>
 * start urls and other settings in {@link Site}<br>
 * how the urls to fetch are detected               <br>
 * how the data are extracted and stored             <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see Site
 * @see Page
 * @since 0.1.0
 */
public interface PageProcessor {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page page
     */
    void process(Page page);

    /**
     * get the site settings
     *
     * @return site
     * @see Site
     */
    Site getSite();
}
