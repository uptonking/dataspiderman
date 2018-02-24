package us.codecraft.webmagic;

/**
 * 任务接口
 * 用于获取uuid来区分不同任务
 * <p>
 * Interface for identifying different tasks.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @see us.codecraft.webmagic.scheduler.Scheduler
 * @see us.codecraft.webmagic.pipeline.Pipeline
 * @since 0.1.0
 */
 public interface Task {

    /**
     * unique id for a task.
     *
     * @return uuid
     */
     String getUUID();

    /**
     * site of a task
     *
     * @return site
     */
     Site getSite();

}
