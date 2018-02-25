package us.codecraft.webmagic.model;

import us.codecraft.webmagic.Page;

/**
 * 提起后处理器 接口
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface AfterExtractor {

    /**
     * 会在抽取结束，字段都初始化完毕之后被调用，可以处理一些特殊的逻辑
     *
     * @param page 下载完成的page
     */
    void afterProcess(Page page);
}
