package us.codecraft.webmagic.handler;

import us.codecraft.webmagic.Request;

/**
 * 检查请求url是否匹配的接口
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface RequestMatcher {

    /**
     * 检查请求url是否匹配
     * 若匹配，则下载页面
     * 若不匹配，则不下载页面
     * Check whether to process the page.<br><br>
     * Please DO NOT change page status in this method.
     *
     * @param page page
     * @return whether matches
     */
    boolean match(Request page);

    enum MatchOther {
        YES, NO
    }
}
