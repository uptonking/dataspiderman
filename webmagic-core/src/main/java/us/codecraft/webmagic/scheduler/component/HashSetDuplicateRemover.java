package us.codecraft.webmagic.scheduler.component;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用HashSet来进行去重，占用内存较大，所有去重默认使用HashSetDuplicateRemover
 * <p>
 * 使用BloomFilter来进行去重，占用内存较小，但是可能漏抓页面，如果使用BloomFilterDuplicateRemover，需要单独引入Guava依赖包
 *
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(Request request) {
        return request.getUrl();
    }

    /**
     * 清空集合
     */
    @Override
    public void resetDuplicateCheck(Task task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return urls.size();
    }
}
