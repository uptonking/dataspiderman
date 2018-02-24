package us.codecraft.webmagic;

/**
 * 爬虫成功和失败的事件监听器接口
 * <p>
 * Listener of Spider on page processing. Used for monitor and such on.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
 public interface SpiderListener {

     void onSuccess(Request request);

     void onError(Request request);
}
