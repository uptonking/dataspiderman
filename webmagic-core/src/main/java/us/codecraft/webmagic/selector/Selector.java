package us.codecraft.webmagic.selector;

import java.util.List;

/**
 * 定义了根据字符串选择单个元素和选择多个元素的方法
 * <p>
 * Selector(extractor) for text.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Selector {

    /**
     * 抽取单个文本
     * Extract single result in text.<br>
     * If there are more than one result, only the first will be chosen.
     *
     * @param text text
     * @return result
     */
    public String select(String text);

    /**
     * 抽取多个文本列表
     * Extract all results in text.<br>
     *
     * @param text text
     * @return results
     */
    public List<String> selectList(String text);

}
