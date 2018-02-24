package us.codecraft.webmagic.selector.selectable;

import us.codecraft.webmagic.selector.Selector;

import java.util.List;

/**
 * 可抽取文本的接口
 * Selectable text.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
 public interface Selectable {

    /**
     * select list with xpath
     *
     * @param xpath xpath
     * @return new Selectable after extract
     */
   Selectable xpath(String xpath);

    /**
     * select list with css selector
     *
     * @param selector css selector expression
     * @return new Selectable after extract
     */
   Selectable $(String selector);

    /**
     * select list with css selector
     *
     * @param selector css selector expression
     * @param attrName attribute name of css selector
     * @return new Selectable after extract
     */
   Selectable $(String selector, String attrName);

    /**
     * select list with css selector
     *
     * @param selector css selector expression
     * @return new Selectable after extract
     */
   Selectable css(String selector);

    /**
     * select list with css selector
     *
     * @param selector css selector expression
     * @param attrName attribute name of css selector
     * @return new Selectable after extract
     */
   Selectable css(String selector, String attrName);

    /**
     * 自动抽取可读的文本
     * select smart content with ReadAbility algorithm
     *
     * @return content
     */
   Selectable smartContent();

    /**
     * 抽取所有超链接
     * select all links
     *
     * @return all links
     */
   Selectable links();

    /**
     * 根据正则表达式抽取
     * select list with regex, default group is group 1
     *
     * @param regex regex
     * @return new Selectable after extract
     */
   Selectable regex(String regex);

    /**
     * select list with regex
     *
     * @param regex regex
     * @param group group
     * @return new Selectable after extract
     */
   Selectable regex(String regex, int group);

    /**
     * replace with regex
     *
     * @param regex       regex
     * @param replacement replacement
     * @return new Selectable after extract
     */
   Selectable replace(String regex, String replacement);

    /**
     * single string result
     *
     * @return single string result
     */
     String get();

    /**
     * single string result
     *
     * @return single string result
     */
     String toString();

    /**
     * if result exist for select
     *
     * @return true if result exist
     */
     boolean match();

    /**
     * multi string result
     *
     * @return multi string result
     */
     List<String> all();

    /**
     * extract by JSON Path expression
     *
     * @param jsonPath jsonPath
     * @return result
     */
   Selectable jsonPath(String jsonPath);

    /**
     * extract by custom selector
     *
     * @param selector selector
     * @return result
     */
   Selectable select(Selector selector);

    /**
     * extract by custom selector
     *
     * @param selector selector
     * @return result
     */
   Selectable selectList(Selector selector);

    /**
     * get all nodes
     *
     * @return result
     */
    List<Selectable> nodes();

}
