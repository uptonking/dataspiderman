package us.codecraft.webmagic.utils;

/**
 * 数字操作工具类
 *
 * @author yihua.huang@dianping.com
 */
public abstract class NumberUtils {

    /**
     * 2个long变量比较
     */
    public static int compareLong(long o1, long o2) {
        if (o1 < o2) {
            return -1;
        } else if (o1 == o2) {
            return 0;
        } else {
            return 1;
        }
    }

}
