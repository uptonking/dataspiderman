package us.codecraft.webmagic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 页面解析后的结果对象bean
 * <p>
 * Object contains extract results.<br>
 * It is contained in Page and will be processed in pipeline.
 *
 * @author code4crafter@gmail.com <br>
 * @see Page
 * @see us.codecraft.webmagic.pipeline.Pipeline
 * @since 0.1.0
 */
public class ResultItems {

    private Request request;
    /**
     * 解析页面得到的kv
     */
    private Map<String, Object> fields = new LinkedHashMap<String, Object>();
    /**
     * 是否跳过pipeline的处理
     */
    private boolean skip;

    /**
     * 根据key获取fields中的value
     */
    public <T> T get(String key) {
        Object o = fields.get(key);
        if (o == null) {
            return null;
        }
        return (T) fields.get(key);
    }

    /**
     * 获取fields中所有的kv
     */
    public Map<String, Object> getAll() {
        return fields;
    }

    /**
     * 向fields添加kv
     */
    public <T> ResultItems put(String key, T value) {
        fields.put(key, value);
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public ResultItems setRequest(Request request) {
        this.request = request;
        return this;
    }

    /**
     * Whether to skip the result.<br>
     * Result which is skipped will not be processed by Pipeline.
     *
     * @return whether to skip the result
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * Set whether to skip the result.<br>
     * Result which is skipped will not be processed by Pipeline.
     *
     * @param skip whether to skip the result
     * @return this
     */
    public ResultItems setSkip(boolean skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public String toString() {
        return "ResultItems{" +
                "fields=" + fields +
                ", request=" + request +
                ", skip=" + skip +
                '}';
    }
}
