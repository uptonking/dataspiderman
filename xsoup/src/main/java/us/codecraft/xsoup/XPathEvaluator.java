package us.codecraft.xsoup;

import org.jsoup.nodes.Element;

/**
 * 执行匹配的接口
 *
 * @author code4crafter@gmail.com
 */
public interface XPathEvaluator {

    XElements evaluate(Element element);

    boolean hasAttribute();

}
