package us.codecraft.xsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.xsoup.w3c.NodeAdaptors;
import us.codecraft.xsoup.xevaluator.XPathParser;

/**
 * Xsoup是提供抽取方法的工具类
 * <p>
 * 全是静态方法
 *
 * @author code4crafter@gmail.com
 */
public class Xsoup {

    /*-------------     XEvaluator         --------------- */

    public static XElements select(Element element, String xpathStr) {
        return XPathParser.parse(xpathStr).evaluate(element);
    }

    public static XElements select(String html, String xpathStr) {
        return XPathParser.parse(xpathStr).evaluate(Jsoup.parse(html));
    }

    public static XPathEvaluator compile(String xpathStr) {
        return XPathParser.parse(xpathStr);
    }

    /*-------------     W3cAdaptor         --------------- */

    /**
     * 将jsoup的Element转换成w3c的Element
     *
     * @param element jsoup的Element
     * @return w3c的Element
     */
    public static org.w3c.dom.Element convertElement(Element element) {
        return NodeAdaptors.getElement(element);
    }

    /**
     * 将jsoup的Document转换成w3c的Document
     *
     * @param document jsoup的Document
     * @return w3c的Document
     */
    public static org.w3c.dom.Document convertDocument(Document document) {
        return NodeAdaptors.getDocument(document);
    }

}
