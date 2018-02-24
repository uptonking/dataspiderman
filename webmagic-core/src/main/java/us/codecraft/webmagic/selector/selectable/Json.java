package us.codecraft.webmagic.selector.selectable;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.selector.JsonPathSelector;

import us.codecraft.xsoup.XTokenQueue;

import java.util.List;

/**
 * 抽取JSON
 * 基于Xsoup，Xsoup是一个XPath selector based on Jsoup
 * <p>
 * parse json
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public class Json extends PlainText {

    private static Logger logger = LoggerFactory.getLogger(Json.class);

    public Json(List<String> strings) {
        super(strings);
    }

    public Json(String text) {
        super(text);
    }

    /**
     * remove padding for JSONP
     *
     * @param padding padding
     * @return json after padding removed
     */
    public Json removePadding(String padding) {
        String text = getFirstSourceText();
        XTokenQueue tokenQueue = new XTokenQueue(text);
        tokenQueue.consumeWhitespace();
        tokenQueue.consume(padding);
        tokenQueue.consumeWhitespace();
        String chompBalanced = tokenQueue.chompBalancedNotInQuotes('(', ')');
        return new Json(chompBalanced);
    }

    public <T> T toObject(Class<T> clazz) {
        if (getFirstSourceText() == null) {
            return null;
        }
        return JSON.parseObject(getFirstSourceText(), clazz);
    }

    public <T> List<T> toList(Class<T> clazz) {
        if (getFirstSourceText() == null) {
            return null;
        }
        return JSON.parseArray(getFirstSourceText(), clazz);
    }

    @Override
    public Selectable jsonPath(String jsonPath) {
        JsonPathSelector jsonPathSelector = new JsonPathSelector(jsonPath);
        return selectList(jsonPathSelector, getSourceTexts());
    }
}
