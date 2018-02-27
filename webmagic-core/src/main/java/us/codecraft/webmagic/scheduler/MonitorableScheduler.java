package us.codecraft.webmagic.scheduler;

import us.codecraft.webmagic.Task;

/**
 * 请求url地址管理及监控接口
 * <p>
 * The scheduler whose requests can be counted for monitor.
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public interface MonitorableScheduler extends Scheduler {

    /**
     * 获取请求url剩余数量
     */
    int getLeftRequestsCount(Task task);

    /**
     * 获取请求url总数
     */
    int getTotalRequestsCount(Task task);

}
