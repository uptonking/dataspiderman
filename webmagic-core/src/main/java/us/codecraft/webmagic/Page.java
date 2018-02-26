package us.codecraft.webmagic;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.selectable.Html;
import us.codecraft.webmagic.selector.selectable.Json;
import us.codecraft.webmagic.selector.selectable.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 下载完成得到的对象bean
 * 可以是html、json、其他文本格式
 * <p>
 * Object storing extracted result and urls to fetch.<br>
 * Not thread safe.<br>
 * Main method：                                               <br>
 * {@link #getUrl()} get url of current page                   <br>
 * {@link #getHtml()}  get content of current page                 <br>
 * {@link #putField(String, Object)}  save extracted result            <br>
 * {@link #getResultItems()} get extract results to be used in {@link us.codecraft.webmagic.pipeline.Pipeline}<br>
 * {@link #addTargetRequests(java.util.List)} {@link #addTargetRequest(String)} add urls to fetch                 <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see us.codecraft.webmagic.downloader.Downloader
 * @see us.codecraft.webmagic.processor.PageProcessor
 * @since 0.1.0
 */
public class Page {

    /**
     * 页面对应的请求url
     */
    private Request request;
    /**
     * 页面解析结果
     */
    private ResultItems resultItems = new ResultItems();

    private Html html;
    private Json json;
    private String rawText;

    private Selectable url;

    private Map<String, List<String>> headers;

    private int statusCode = HttpConstant.StatusCode.CODE_200;

    private String charset;

    /**
     * 页面是否下载成功的标识
     */
    private boolean downloadSuccess = true;

    private byte[] bytes;

    /**
     * 请求地址列表
     */
    private List<Request> targetRequests = new ArrayList<Request>();


    public Page() {
    }

    /**
     * 设置page下载状态为失败
     */
    public static Page fail() {
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }

    /**
     * 设置解析结果是否要跳过被pipeline处理
     *
     * @param skip 是否跳过处理，true则跳过，即不处理
     */
    public Page setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    /**
     * 添加页面解析到的结果kv
     * store extract results
     *
     * @param key   key
     * @param field field
     */
    public void putField(String key, Object field) {
        resultItems.put(key, field);
    }

    /**
     * get html content of page
     *
     * @return html
     */
    public Html getHtml() {
        if (html == null) {
            html = new Html(rawText, request.getUrl());
        }
        return html;
    }

    /**
     * @param html html
     * @deprecated since 0.4.0
     * The html is parse just when first time of calling {@link #getHtml()}, so use {@link #setRawText(String)} instead.
     */
    public void setHtml(Html html) {
        this.html = html;
    }

    /**
     * get json content of page
     *
     * @return json
     * @since 0.5.0
     */
    public Json getJson() {
        if (json == null) {
            json = new Json(rawText);
        }
        return json;
    }

    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    /**
     * 添加请求地址列表
     * add urls to fetch
     *
     * @param requests requests
     */
    public void addTargetRequests(List<String> requests) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetRequests.add(new Request(s));
        }
    }

    /**
     * 添加请求地址列表，带优先级
     * add urls to fetch
     *
     * @param requests requests
     * @param priority priority
     */
    public void addTargetRequests(List<String> requests, long priority) {
        for (String s : requests) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) {
                continue;
            }
            s = UrlUtils.canonicalizeUrl(s, url.toString());
            targetRequests.add(new Request(s).setPriority(priority));
        }
    }

    /**
     * 添加单个请求地址
     * add url to fetch
     *
     * @param requestString requestString
     */
    public void addTargetRequest(String requestString) {
        if (StringUtils.isBlank(requestString) || requestString.equals("#")) {
            return;
        }
        requestString = UrlUtils.canonicalizeUrl(requestString, url.toString());
        targetRequests.add(new Request(requestString));
    }

    /**
     * 添加单个请求地址
     * add requests to fetch
     *
     * @param request request
     */
    public void addTargetRequest(Request request) {
        targetRequests.add(request);
    }

    /**
     * get url of current page
     *
     * @return url of current page
     */
    public Selectable getUrl() {
        return url;
    }

    public void setUrl(Selectable url) {
        this.url = url;
    }

    /**
     * get request of current page
     *
     * @return request
     */
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRawText() {
        return rawText;
    }

    public Page setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "Page{" +
                "request=" + request +
                ", resultItems=" + resultItems +
                ", html=" + html +
                ", json=" + json +
                ", rawText='" + rawText + '\'' +
                ", url=" + url +
                ", headers=" + headers +
                ", statusCode=" + statusCode +
                ", downloadSuccess=" + downloadSuccess +
                ", targetRequests=" + targetRequests +
                ", charset='" + charset + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
