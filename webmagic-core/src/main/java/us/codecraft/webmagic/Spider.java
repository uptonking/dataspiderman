package us.codecraft.webmagic;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.thread.CountableThreadPool;
import us.codecraft.webmagic.utils.UrlUtils;
import us.codecraft.webmagic.utils.WMCollections;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Spider是webmagic的爬虫入口类，负责组件初始化、控制爬虫启停
 * <p>
 * Downloader、PageProcessor、Scheduler、Pipeline都是Spider的属性
 * <p>
 * Entrance of a crawler.<br>
 * A spider contains four modules: Downloader, Scheduler, PageProcessor and * Pipeline.<br>
 * Every module is a field of Spider. <br>
 * The modules are defined in interface. <br>
 * You can customize a spider with various implementations of them. <br>
 * Examples: <br>
 * <br>
 * A simple crawler: <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")).run();<br>
 * <br>
 * Store results to files by FilePipeline: <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")) <br>
 * .pipeline(new FilePipeline("/data/temp/webmagic/")).run(); <br>
 * <br>
 * Use FileCacheQueueScheduler to store urls and cursor in files, so that a
 * Spider can resume the status when shutdown. <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")) <br>
 * .scheduler(new FileCacheQueueScheduler("/data/temp/webmagic/cache/")).run(); <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see Downloader
 * @see Scheduler
 * @see PageProcessor
 * @see Pipeline
 * @since 0.1.0
 */
public class Spider implements Runnable, Task {

    private static Logger logger = LoggerFactory.getLogger(Spider.class);

    /**
     * 爬虫运行状态常量
     */
    private final static int STAT_INIT = 0;
    private final static int STAT_RUNNING = 1;
    private final static int STAT_STOPPED = 2;

    private Scheduler scheduler = new QueueScheduler();

    private Downloader downloader;

    private PageProcessor pageProcessor;

    private List<Pipeline> pipelines = new ArrayList<Pipeline>();

    /**
     * 爬虫初始url地址
     */
    private List<Request> startRequests;
    /**
     * 爬取站点的通用http连接信息bean
     */
    private Site site;
    /**
     * 爬虫任务id
     */
    private String uuid;
    /**
     * 爬虫计数器
     */
    private CountableThreadPool threadPool;

    private ExecutorService executorService;
    /**
     * 爬虫并发线程数，默认单线程
     */
    private int threadNum = 1;
    /**
     * 爬虫运行状态
     */
    private AtomicInteger stat = new AtomicInteger(STAT_INIT);

    private boolean exitWhenComplete = true;
    private boolean destroyWhenExit = true;
    /**
     * 是否是派生的二次爬虫地址
     */
    private boolean spawnUrl = true;

    private ReentrantLock newUrlLock = new ReentrantLock();
    private Condition newUrlCondition = newUrlLock.newCondition();

    private List<SpiderListener> spiderListeners;

    private final AtomicLong pageCount = new AtomicLong(0);
    /**
     * 爬虫启动时间
     */
    private Date startTime;

    private int emptySleepTime = 30000;

    /**
     * 传入PageProcessor，创建并初始化Spider
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static Spider create(PageProcessor pageProcessor) {
        return new Spider(pageProcessor);
    }

    /**
     * 传入PageProcessor初始化Spider
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public Spider(PageProcessor pageProcessor) {
        this.pageProcessor = pageProcessor;
        this.site = pageProcessor.getSite();
    }

    /**
     * 设置初始爬虫url地址
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startUrls startUrls
     * @return this
     */
    public Spider startUrls(List<String> startUrls) {
        checkIfRunning();
        this.startRequests = UrlUtils.convertToRequests(startUrls);
        return this;
    }

    /**
     * 设置初始爬虫url地址
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startRequests startRequests
     * @return this
     */
    public Spider startRequest(List<Request> startRequests) {
        checkIfRunning();
        this.startRequests = startRequests;
        return this;
    }

    /**
     * 设置爬虫uuid
     * Set an uuid for spider.<br>
     * Default uuid is domain of site.<br>
     *
     * @param uuid uuid
     * @return this
     */
    public Spider setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see #setScheduler(us.codecraft.webmagic.scheduler.Scheduler)
     */
    @Deprecated
    public Spider scheduler(Scheduler scheduler) {
        return setScheduler(scheduler);
    }

    /**
     * 设置Scheduler，一个Spider只能有个一个Scheduler
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see Scheduler
     * @since 0.2.1
     */
    public Spider setScheduler(Scheduler scheduler) {
        checkIfRunning();
        Scheduler oldScheduler = this.scheduler;
        this.scheduler = scheduler;
        if (oldScheduler != null) {
            Request request;
            while ((request = oldScheduler.poll(this)) != null) {
                this.scheduler.push(request, this);
            }
        }
        return this;
    }

    /**
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see #addPipeline(us.codecraft.webmagic.pipeline.Pipeline)
     */
    @Deprecated
    public Spider pipeline(Pipeline pipeline) {
        return addPipeline(pipeline);
    }

    /**
     * 添加一个Pipeline，一个Spider可以有多个Pipeline
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see Pipeline
     * @since 0.2.1
     */
    public Spider addPipeline(Pipeline pipeline) {
        checkIfRunning();
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * 直接设置pipeline
     * set pipelines for Spider
     *
     * @param pipelines pipelines
     * @return this
     * @see Pipeline
     * @since 0.4.1
     */
    public Spider setPipelines(List<Pipeline> pipelines) {
        checkIfRunning();
        this.pipelines = pipelines;
        return this;
    }

    /**
     * 删除所有pipeline
     * clear the pipelines set
     *
     * @return this
     */
    public Spider clearPipeline() {
        pipelines = new ArrayList<Pipeline>();
        return this;
    }

    /**
     * set the downloader of spider
     *
     * @param downloader downloader
     * @return this
     * @see #setDownloader(us.codecraft.webmagic.downloader.Downloader)
     */
    @Deprecated
    public Spider downloader(Downloader downloader) {
        return setDownloader(downloader);
    }

    /**
     * 设置Downloader，一个Spider只能有个一个Downloader
     * set the downloader of spider
     *
     * @param downloader downloader
     * @return this
     * @see Downloader
     */
    public Spider setDownloader(Downloader downloader) {
        checkIfRunning();
        this.downloader = downloader;
        return this;
    }

    /**
     * 初始化组件
     * 包括downloader，pipelines
     * 在run()方法中被调用
     */
    private void initComponent() {

        if (downloader == null) {
            this.downloader = new HttpClientDownloader();
        }
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
        }

        downloader.setThread(threadNum);
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }

        if (startRequests != null) {
            for (Request request : startRequests) {
                addRequest(request);
            }
            startRequests.clear();
        }
        startTime = new Date();
    }

    /**
     * 在新线程中运行爬虫任务
     */
    @Override
    public void run() {

        checkRunningStat();

        //初始化默认的HttpClientDownloader和ConsolePipeline
        initComponent();
        logger.info("Spider {} started!", getUUID());

        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            final Request request = scheduler.poll(this);
            if (request == null) {
                if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                    break;
                }

                // wait until new url added 等待新url，等待时加锁阻塞
                waitNewUrl();
            } else {
                threadPool.execute(new Runnable() {

                    /**
                     * 在新线程中处理下载url
                     */
                    @Override
                    public void run() {
                        try {
                            processRequest(request);
                            onSuccess(request);
                        } catch (Exception e) {
                            onError(request);
                            logger.error("process request " + request + " error", e);
                        } finally {
                            pageCount.incrementAndGet();

                            //释放锁
                            signalNewUrl();
                        }
                    }
                });
            }
        }

        stat.set(STAT_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
        logger.info("Spider {} closed! {} pages downloaded.", getUUID(), pageCount.get());
    }

    /**
     * 在新线程中启动本爬虫
     */
    public void runAsync() {
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

    public void start() {
        runAsync();
    }


    /**
     * 触发爬虫失败的监听器事件
     */
    private void onError(Request request) {
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onError(request);
            }
        }
    }

    /**
     * 触发爬虫成功的监听器事件
     */
    private void onSuccess(Request request) {
        if (CollectionUtils.isNotEmpty(spiderListeners)) {
            for (SpiderListener spiderListener : spiderListeners) {
                spiderListener.onSuccess(request);
            }
        }
    }

    /**
     * 检查爬虫运行状态，并将爬虫状态置为正在运行
     * 死循环
     */
    private void checkRunningStat() {

        while (true) {

            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }

            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    /**
     * 爬虫结束时释放资源
     */
    public void close() {
        destroyEach(downloader);
        destroyEach(pageProcessor);
        destroyEach(scheduler);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
        threadPool.shutdown();
    }

    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 抓取一个页面进行测试
     * Process specific urls without url discovering.
     *
     * @param urls urls to process
     */
    public void test(String... urls) {
        initComponent();
        if (urls.length > 0) {
            for (String url : urls) {
                processRequest(new Request(url));
            }
        }
    }

    /**
     * 下载请求的url
     */
    private void processRequest(Request request) {
        Page page = downloader.download(request, this);
        if (page.isDownloadSuccess()) {
            onDownloadSuccess(request, page);
        } else {
            onDownloaderFail(request);
        }
    }

    private void onDownloadSuccess(Request request, Page page) {
        if (site.getAcceptStatCode().contains(page.getStatusCode())) {

            //解析并抽取页面内容
            pageProcessor.process(page);
            extractAndAddRequests(page, spawnUrl);
            if (!page.getResultItems().isSkip()) {
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(page.getResultItems(), this);
                }
            }
        } else {
            logger.info("page status code error, page {} , code: {}", request.getUrl(), page.getStatusCode());
        }
        sleep(site.getSleepTime());
    }

    private void onDownloaderFail(Request request) {
        if (site.getCycleRetryTimes() == 0) {
            sleep(site.getSleepTime());
        } else {
            // 下载失败时重试，for cycle retry
            doCycleRetry(request);
        }
    }

    /**
     * 请求下载失败时，循环重试
     * 默认重复0次，即不重试，见{@link us.codecraft.webmagic.Site#cycleRetryTimes}
     */
    private void doCycleRetry(Request request) {
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);

        if (cycleTriedTimesObject == null) {
            addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            //重试次数限制
            if (cycleTriedTimes < site.getCycleRetryTimes()) {
                addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
            }
        }
        sleep(site.getRetrySleepTime());
    }

    /**
     * spider所在线程挂起，未释放锁
     *
     * @param time 毫秒
     */
    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted when sleep", e);
        }
    }

    /**
     * 从下载的页面中解析 二次爬虫地址，并加入请求队列
     */
    private void extractAndAddRequests(Page page, boolean spawnUrl) {
        if (spawnUrl && CollectionUtils.isNotEmpty(page.getTargetRequests())) {
            for (Request request : page.getTargetRequests()) {
                addRequest(request);
            }
        }
    }

    /**
     * 添加初始的Request
     * 添加单个请求url
     */
    private void addRequest(Request request) {
        if (site.getDomain() == null && request != null && request.getUrl() != null) {
            site.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
        scheduler.push(request, this);
    }

    /**
     * 添加多个请求url
     * Add urls with information to crawl.<br>
     *
     * @param requests requests
     * @return this
     */
    public Spider addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        signalNewUrl();
        return this;
    }


    /**
     * 检查爬虫是否运行
     * 若正在运行，则抛出异常
     * 若未运行，则无动作
     */
    private void checkIfRunning() {
        if (stat.get() == STAT_RUNNING) {
            throw new IllegalStateException("Spider is already running!");
        }
    }


    /**
     * 添加初始的URL
     * Add urls to crawl. <br>
     *
     * @param urls urls
     * @return this
     */
    public Spider addUrl(String... urls) {
        for (String url : urls) {
            addRequest(new Request(url));
        }
        signalNewUrl();
        return this;
    }

    /**
     * 同步调用，并直接取得所有结果
     * 同步下载所有url，返回所有解析结果
     * Download urls synchronizing.
     *
     * @param urls urls
     * @param <T>  type of process result
     * @return list downloaded
     */
    public <T> List<T> getAll(Collection<String> urls) {
        destroyWhenExit = false;
        spawnUrl = false;
        if (startRequests != null) {
            startRequests.clear();
        }
        for (Request request : UrlUtils.convertToRequests(urls)) {
            addRequest(request);
        }
        CollectorPipeline collectorPipeline = getCollectorPipeline();
        pipelines.add(collectorPipeline);
        run();
        spawnUrl = true;
        destroyWhenExit = true;
        return collectorPipeline.getCollected();
    }

    protected CollectorPipeline getCollectorPipeline() {
        return new ResultItemsCollectorPipeline();
    }

    /**
     * 同步调用，并直接取得结果
     * 返回第1个解析结果
     */
    public <T> T get(String url) {
        List<String> urls = WMCollections.newArrayList(url);
        List<T> resultItemses = getAll(urls);
        if (resultItemses != null && resultItemses.size() > 0) {
            return resultItemses.get(0);
        } else {
            return null;
        }
    }

    /**
     * 阻塞等待新url
     */
    private void waitNewUrl() {
        newUrlLock.lock();
        try {
            //double check
            if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                return;
            }
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitNewUrl - interrupted, error {}", e);
        } finally {
            newUrlLock.unlock();
        }
    }

    /**
     * 释放等待新url的锁
     */
    private void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }

    /**
     * 停止爬虫
     */
    public void stop() {
        if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
            logger.info("Spider " + getUUID() + " stop success!");
        } else {
            logger.info("Spider " + getUUID() + " stop fail!");
        }
    }

    /**
     * 获取下载的总页数
     * Get page count downloaded by spider.
     *
     * @return total downloaded page count
     * @since 0.4.1
     */
    public long getPageCount() {
        return pageCount.get();
    }

    /**
     * 获取爬虫运行状态
     * Get running status by spider.
     *
     * @return running status
     * @see Status
     * @since 0.4.1
     */
    public Status getStatus() {
        return Status.fromValue(stat.get());
    }

    /**
     * 获取正在运行的活动爬虫数量
     * Get thread count which is running
     *
     * @return thread count which is running
     * @since 0.4.1
     */
    public int getThreadAlive() {
        if (threadPool == null) {
            return 0;
        }
        return threadPool.getThreadAlive();
    }

    /**
     * 获取爬虫uuid
     * 若未设置爬虫uuid，则返回下载url的域名，若仍未空，则返回随机uuid
     */
    @Override
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        if (site != null) {
            return site.getDomain();
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public Spider setExecutorService(ExecutorService executorService) {
        checkIfRunning();
        this.executorService = executorService;
        return this;
    }

    /**
     * 设置爬虫线程数量
     * start with more than one threads
     *
     * @param threadNum threadNum
     * @return this
     */
    public Spider thread(int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        return this;
    }

    /**
     * 设置爬虫线程数量和线程池
     * start with more than one threads
     *
     * @param executorService executorService to run the spider
     * @param threadNum       threadNum
     * @return this
     */
    public Spider thread(ExecutorService executorService, int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        this.executorService = executorService;
        return this;
    }

    /**
     * 爬虫完成时是否退出
     */
    public boolean isExitWhenComplete() {
        return exitWhenComplete;
    }

    /**
     * 设置是否完成时退出
     * Exit when complete. <br>
     * True: exit when all url of the site is downloaded. <br>
     * False: not exit until call stop() manually.<br>
     *
     * @param exitWhenComplete exitWhenComplete
     * @return this
     */
    public Spider setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
        return this;
    }

    /**
     * 判断是否是二次爬虫地址，而不是初始地址
     */
    public boolean isSpawnUrl() {
        return spawnUrl;
    }

    /**
     * 设置是否是二次爬虫地址
     * 若是，则添加到下载队列
     * 若不是，则下载完初始爬虫地址后就结束
     * Whether add urls extracted to download.<br>
     * Add urls to download when it is true, and just download seed urls when it is false. <br>
     * DO NOT set it unless you know what it means!
     *
     * @param spawnUrl spawnUrl
     * @return this
     * @since 0.4.0
     */
    public Spider setSpawnUrl(boolean spawnUrl) {
        this.spawnUrl = spawnUrl;
        return this;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public List<SpiderListener> getSpiderListeners() {
        return spiderListeners;
    }

    public Spider setSpiderListeners(List<SpiderListener> spiderListeners) {
        this.spiderListeners = spiderListeners;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * 设置没有url时的等待时间
     * Set wait time when no url is polled.<br><br>
     *
     * @param emptySleepTime In MILLISECONDS.
     */
    public void setEmptySleepTime(int emptySleepTime) {
        this.emptySleepTime = emptySleepTime;
    }

    /**
     * 爬虫运行状态枚举类
     */
    public enum Status {
        Init(0), Running(1), Stopped(2);

        private Status(int value) {
            this.value = value;
        }

        private int value;

        int getValue() {
            return value;
        }

        public static Status fromValue(int value) {
            for (Status status : Status.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            //default value
            return Init;
        }
    }

}
