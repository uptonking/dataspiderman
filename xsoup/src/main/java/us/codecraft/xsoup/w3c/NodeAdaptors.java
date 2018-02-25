package us.codecraft.xsoup.w3c;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * 多个Node适配器 类
 *
 * @author code4crafer@gmail.com
 */
public class NodeAdaptors {

    public static Node getNode(org.jsoup.nodes.Node node) {
        if (node == null) {
            return null;
        }
        if (node instanceof org.jsoup.nodes.Element) {
            return new ElementAdaptor((org.jsoup.nodes.Element) node);
        }
        return null;
    }


    public static org.w3c.dom.Element getElement(org.jsoup.nodes.Element element) {
        if (element == null) {
            return null;
        }
        return new ElementAdaptor(element);
    }

    public static Document getDocument(org.jsoup.nodes.Document document) {
        if (document == null) {
            return null;
        }
        return new DocumentAdaptor(document);
    }


    public static NodeList getNodeList(org.jsoup.select.Elements elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        return new NodeListAdaptor(elements);
    }

    public static NodeList getNodeList(List<org.jsoup.nodes.Node> elements) {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        return new NodeListAdaptor(elements);
    }

    public static Attr getAttr(Attribute attr, org.jsoup.nodes.Element element) {
        if (attr == null || element == null) {
            return null;
        }
        return new AttributeAdaptor(attr, element);
    }

    public static NamedNodeMap getNamedNodeMap(List<? extends Node> nodeList) {
        if (nodeList == null || nodeList == null) {
            return null;
        }
        return new NamedNodeMapAdaptor(nodeList);
    }

    public static List<Attr> getAttributes(Attributes attrs, org.jsoup.nodes.Element element) {
        if (attrs == null || element == null) {
            return null;
        }
        return new AttributesAdaptor(attrs, element).get();
    }


}
