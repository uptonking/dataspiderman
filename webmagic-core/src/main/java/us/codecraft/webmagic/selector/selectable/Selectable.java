package us.codecraft.webmagic.selector.selectable;

import us.codecraft.webmagic.selector.Selector;

import java.util.List;

/**
 * 可抽取文本的接口
 * 链式抽取
 * <p>
 * Selectable text.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Selectable {

    /**
     * 使用XPath选择
     * select list with xpath
     *
     * @param xpath xpath
     * @return new Selectable after extract
     */
    Selectable xpath(String xpath);

    /**
     * 使用Css选择器选择
     * select list with css selector
     *
     * @param selector css selector expression
     * @return new Selectable after extract
     */
    Selectable $(String selector);

    /**
     * 使用Css选择器选择
     * select list with css selector
     *
     * @param selector css selector expression
     * @param attrName attribute name of css selector
     * @return new Selectable after extract
     */
    Selectable $(String selector, String attrName);

    /**
     * 使用Css选择器选择，同$()
     * select list with css selector
     *
     * @param selector css selector expression
     * @return new Selectable after extract
     */
    Selectable css(String selector);

    /**
     * 使用Css选择器选择，同$()
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
     * 根据正则表达式替换内容
     * replace with regex
     *
     * @param regex       regex
     * @param replacement replacement
     * @return new Selectable after extract
     */
    Selectable replace(String regex, String replacement);

    /**
     * 返回一条String类型的结果
     * 知道页面只会有一条结果，那么可以使用selectable.get()或者selectable.toString()拿到这条结果
     * single string result
     *
     * @return single string result
     */
    String get();

    /**
     * 功能同get()，返回一条String类型的结果
     * single string result
     *
     * @return single string result
     */
    String toString();

    /**
     * 是否有匹配结果
     * if result exist for select
     *
     * @return true if result exist
     */
    boolean match();

    /**
     * 返回所有抽取结果
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
