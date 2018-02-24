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
 * 用于管理请求url地址的优先队列
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
     * 普通队列，非优先队列
     */
    private BlockingQueue<Request> noPriorityQueue = new LinkedBlockingQueue<Request>();

    /**
     * 优先队列元素个数
     */
    private static final int INITIAL_CAPACITY = 5;
    /**
     * 优先队列
     */
    private PriorityBlockingQueue<Request> priorityQueuePlus = new PriorityBlockingQueue<Request>(INITIAL_CAPACITY, new Comparator<Request>() {

        @Override
        public int compare(Request o1, Request o2) {
            return -NumberUtils.compareLong(o1.getPriority(), o2.getPriority());
        }
    });

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
     */
    @Override
    public synchronized Request poll(Task task) {
        Request poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        return noPriorityQueue.size();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return getDuplicateRemover().getTotalRequestsCount(task);
    }
}
