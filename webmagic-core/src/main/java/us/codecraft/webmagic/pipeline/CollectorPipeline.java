package us.codecraft.webmagic.pipeline;

import java.util.List;

/**
 * 收集结果的Pipeline接口
 * Pipeline that can collect and store results. <br>
 * Used for {@link us.codecraft.webmagic.Spider#getAll(java.util.Collection)}
 *
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public interface CollectorPipeline<T> extends Pipeline {

    /**
     * Get all results collected.
     *
     * @return collected results
     */
    List<T> getCollected();
}
