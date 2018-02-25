package us.codecraft.webmagic.proxy;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;

/**
 * 代理提供者
 * ProxyProvider定位更多是一个“组件”，是由HttpClientDownloader设置
 * <p>
 * Proxy provider
 *
 * @since 0.7.0
 */
public interface ProxyProvider {

    /**
     * 将用完后的代理返回代理池
     * Return proxy to Provider when complete a download.
     *
     * @param proxy the proxy config contains host,port and identify info
     * @param page  the download result
     * @param task  the download task
     */
    void returnProxy(Proxy proxy, Page page, Task task);

    /**
     * 为爬虫任务获取代理对象
     * Get a proxy for task by some strategy.
     *
     * @param task the download task
     * @return proxy
     */
    Proxy getProxy(Task task);

}
