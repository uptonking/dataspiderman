package us.codecraft.webmagic.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * 解析结果进一步处理的Pipeline接口
 * <p>
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 *
 * @author code4crafter@gmail.com <br>
 * @see ConsolePipeline
 * @see FilePipeline
 * @since 0.1.0
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems resultItems
     * @param task        task
     */
    void process(ResultItems resultItems, Task task);
}
