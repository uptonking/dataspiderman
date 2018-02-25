package us.codecraft.webmagic.model.formatter;

/**
 * 格式化方法 接口
 *
 * @author code4crafter@gmail.com
 */
public interface ObjectFormatter<T> {

    T format(String raw) throws Exception;

    Class<T> clazz();

    void initParam(String[] extra);

}
