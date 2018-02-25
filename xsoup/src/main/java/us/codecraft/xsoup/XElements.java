package us.codecraft.xsoup;

import org.jsoup.select.Elements;

import java.util.List;

/**
 * 匹配多个元素接口
 *
 * @author code4crafter@gmail.com
 */
public interface XElements {

    String get();

    List<String> list();

    Elements getElements();

}
