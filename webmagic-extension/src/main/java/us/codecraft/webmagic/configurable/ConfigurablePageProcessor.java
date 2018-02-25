package us.codecraft.webmagic.configurable;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.Experimental;

import java.util.List;

/**
 * 可配置的页面抽取器
 *
 * @author code4crafter@gmail.com <br>
 */
@Experimental
public class ConfigurablePageProcessor implements PageProcessor {

    private Site site;
    /**
     * 抽取规则列表
     */
    private List<ExtractRule> extractRules;

    public ConfigurablePageProcessor(Site site, List<ExtractRule> extractRules) {
        this.site = site;
        this.extractRules = extractRules;
    }

    @Override
    public void process(Page page) {

        for (ExtractRule extractRule : extractRules) {

            if (extractRule.isMulti()) {
                /// 抽取多个对象

                List<String> results = page.getHtml().selectDocumentForList(extractRule.getSelector());
                if (extractRule.isNotNull() && results.size() == 0) {
                    page.setSkip(true);
                } else {
                    page.getResultItems().put(extractRule.getFieldName(), results);
                }


            } else {
                /// 抽取单个对象

                String result = page.getHtml().selectDocument(extractRule.getSelector());
                if (extractRule.isNotNull() && result == null) {
                    page.setSkip(true);
                } else {
                    page.getResultItems().put(extractRule.getFieldName(), result);
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
