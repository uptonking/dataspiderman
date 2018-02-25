package us.codecraft.webmagic.handler;

/**
 * 基于正则表达式的请求匹配、页面解析、结果存储的抽象类
 *
 * @author code4crafer@gmail.com
 */
public abstract class PatternProcessor extends PatternRequestMatcher implements SubPipeline, SubPageProcessor {
    /**
     * @param pattern url pattern to handle
     */
    public PatternProcessor(String pattern) {
        super(pattern);
    }
}
