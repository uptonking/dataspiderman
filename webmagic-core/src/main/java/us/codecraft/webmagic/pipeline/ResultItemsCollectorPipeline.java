package us.codecraft.webmagic.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析结果收集处理器 实现类
 *
 * @author code4crafter@gmail.com
 * @since 0.4.0
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems> {

    private List<ResultItems> resultItemsList = new ArrayList<ResultItems>();

    @Override
    public synchronized void process(ResultItems resultItems, Task task) {
        resultItemsList.add(resultItems);
    }

    @Override
    public List<ResultItems> getCollected() {
        return resultItemsList;
    }
}
