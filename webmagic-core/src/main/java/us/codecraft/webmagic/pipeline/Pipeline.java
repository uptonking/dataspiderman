package us.codecraft.webmagic.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * Pileline是抽取结束后，进行处理的部分<br>
 * 它主要用于抽取结果的保存，也可以定制Pileline可以实现一些通用的功能<br>
 * Pipeline其实就是将PageProcessor抽取的结果，继续进行处理，也可以直接在PageProcessor实现<br>
 * 使用Pipeline是为了模块分离，每个页面的抽取方式有多种，但是后续处理方式则比较固定，如保存到文件、保存到数据库等<br>
 * 一个Spider可以有多个Pipeline
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
     * 解析结果进一步处理
     * Process extracted results.
     *
     * @param resultItems resultItems
     * @param task        task
     */
    void process(ResultItems resultItems, Task task);
}
