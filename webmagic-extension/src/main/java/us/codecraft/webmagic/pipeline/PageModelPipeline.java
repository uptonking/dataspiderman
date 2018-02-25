package us.codecraft.webmagic.pipeline;

import us.codecraft.webmagic.Task;

/**
 * page model pipline 接口
 * 注解模式下，处理结果的类叫做PageModelPipeline
 * 通过实现它，可以自定义自己的结果处理方式
 * 多个Model可以对应一个PageModelPipeline
 * <p>
 * Implements PageModelPipeline to persistent your page model.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface PageModelPipeline<T> {

    /**
     * 对抽取出来的page model继续进行处理
     *
     * @param t    处理好的结果对象
     * @param task 任务
     */
    void process(T t, Task task);

}
