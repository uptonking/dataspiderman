package us.codecraft.webmagic;

/**
 * 页面状态监听器接口
 * 提供每个页面成功和错误的回调，可配置多个
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
