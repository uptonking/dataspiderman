package us.codecraft.webmagic.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * url地址管理接口
 * <p>
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Scheduler {

    /**
     * 添加
     * add a url to fetch
     *
     * @param request request
     * @param task    task
     */
    void push(Request request, Task task);

    /**
     * 取出
     * get an url to crawl
     *
     * @param task the task of spider
     * @return the url to crawl
     */
    Request poll(Task task);

}
