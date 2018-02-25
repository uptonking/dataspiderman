package us.codecraft.webmagic.handler;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 检查请求匹配后的子页面解析结果存储的接口
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SubPipeline extends RequestMatcher {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param resultItems resultItems
     * @param task        task
     * @return whether continue to match
     */
    MatchOther processResult(ResultItems resultItems, Task task);

}
