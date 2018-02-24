package us.codecraft.webmagic.proxy;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于轮询算法的简单代理
 * 无心跳检测、错误检查，常用于稳定可靠的代理池
 * <p>
 * A simple ProxyProvider.
 * Provide proxy as round-robin without heartbeat and error check.
 * It can be used when all proxies are stable.
 *
 * @author code4crafter@gmail.com
 * Date: 17/4/16
 * Time: 10:18
 * @since 0.7.0
 */
public class SimpleProxyProvider implements ProxyProvider {

    /**
     * 可用代理对象列表
     */
    private final List<Proxy> proxies;
    /**
     * 当前使用的代理对象索引
     */
    private final AtomicInteger pointer;

    public SimpleProxyProvider(List<Proxy> proxies) {
        this(proxies, new AtomicInteger(-1));
    }

    private SimpleProxyProvider(List<Proxy> proxies, AtomicInteger pointer) {
        this.proxies = proxies;
        this.pointer = pointer;
    }

    /**
     * 创建代理提供者的静态方法
     */
    public static SimpleProxyProvider from(Proxy... proxies) {
        List<Proxy> proxiesTemp = new ArrayList<Proxy>(proxies.length);
        for (Proxy proxy : proxies) {
            proxiesTemp.add(proxy);
        }
        return new SimpleProxyProvider(Collections.unmodifiableList(proxiesTemp));
    }

    @Override
    public void returnProxy(Proxy proxy, Page page, Task task) {
        //todo do nothing
    }

    @Override
    public Proxy getProxy(Task task) {
        return proxies.get(incrForLoop());
    }

    /**
     * 依次轮询产生代理索引
     *
     * @return 下一个代理索引
     */
    private int incrForLoop() {
        int p = pointer.incrementAndGet();
        int size = proxies.size();
        if (p < size) {
            return p;
        }
        while (!pointer.compareAndSet(p, p % size)) {
            p = pointer.get();
        }
        return p % size;
    }
}
