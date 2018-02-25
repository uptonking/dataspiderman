package us.codecraft.webmagic.model.formatter;

/**
 * 类型转换 接口
 * 除了自动类型转换之外，Formatter还可以通过自定义来做一些结果的后处理的事情
 *
 * @author code4crafter@gmail.com
 */
public interface ObjectFormatter<T> {

    T format(String raw) throws Exception;

    Class<T> clazz();

    void initParam(String[] extra);

}
