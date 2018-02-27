package us.codecraft.webmagic.scheduler;

import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.NumberUtils;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 用于保存请求url地址的优先队列
 * <p>
 * 内置3个队列，优先级的正负影响poll的顺序
 * <p>
 * Priority scheduler.
 * Request with higher priority will poll earlier. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
@ThreadSafe
public class PriorityScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

    /**
     * 普通队列，非优先队列，将元素优先级都设为0L
     */
    private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<Request>();

    /**
     * 优先队列元素个数
     */
    private static final int INITIAL_CAPACITY = 5;
    /**
     * 优先队列plus，优先级为正数
     */
    private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY, new Comparator<Request>() {

        @Override
        public int compare(Request o1, Request o2) {
            return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
        }
    });
    /**
     * 优先队列minus，优先级为负数
     */
    private PriorityBlockingQueue<Request> priorityQueueMinus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY, new Comparator<Request>() {
        @Override
        public int compare(Request o1, Request o2) {
            return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
        }
    });

    /**
     * 加入元素
     */
    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        if (request.getPriority() == 0) {
            noPriorityQueue.add(request);
        } else if (request.getPriority() > 0) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    /**
     * 取出元素
     * 根据优先级正负
     */
    @Override
    public synchronized Request poll(Task task) {

        //优先级为正数
        Request poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        //普通队列
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }

        //优先级为负数
        return priorityQueueMinus.poll();
    }

    /**
     * 普通队列中元素数量 作为剩余请求数
     */
    @Override
    public int getLeftRequestsCount(Task task) {
        return noPriorityQueue.size();
    }

    /**
     * 获取请求总数
     */
    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
