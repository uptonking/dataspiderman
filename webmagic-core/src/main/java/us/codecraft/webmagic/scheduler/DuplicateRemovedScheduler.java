package us.codecraft.webmagic.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.utils.HttpConstant;

/**
 * 请求url去重的抽象类
 * <p>
 * Remove duplicate urls and only push urls which are not duplicate.<br><br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected static Logger logger = LoggerFactory.getLogger(DuplicateRemovedScheduler.class);

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    /**
     * 加入请求队列时检查重复
     * 请求方法为POST，默认不去重
     *
     * @param request request
     * @param task    task
     */
    @Override
    public void push(Request request, Task task) {
        logger.trace("get a candidate url {}", request.getUrl());

        /// 3个条件满足一个就加入爬取队列
        if (shouldReserved(request) || noNeedToRemoveDuplicate(request) || !duplicatedRemover.isDuplicate(request, task)) {
            logger.debug("push to queue {}", request.getUrl());

            //注意post方法不去重
            pushWhenNoDuplicate(request, task);
        }
    }

    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    /**
     * 判断请求方法是否为POST，若是，则不去重，若不是，则该去重
     *
     * @param request 请求
     * @return 是否不去重
     */
    protected boolean noNeedToRemoveDuplicate(Request request) {
        return HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod());
    }

    /**
     * 请求入队，由子类实现
     * 模板设计模式
     */
    protected void pushWhenNoDuplicate(Request request, Task task) {
        logger.debug("====DuplicateRemovedScheduler.pushWhenNoDuplicate() called");
    }
}
