package us.codecraft.webmagic.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 集合工具类
 *
 * @author code4crafter@gmail.com
 * Date: 16/12/18
 * Time: 上午10:16
 */
public class WMCollections {

    /**
     * 创建大小为输入元素个数的HashSet
     */
    public static <T> Set<T> newHashSet(T... t) {
        Set<T> set = new HashSet<T>(t.length);
        for (T t1 : t) {
            set.add(t1);
        }
        return set;
    }

    /**
     * 创建大小为输入元素个数的ArrayList
     */
    public static <T> List<T> newArrayList(T... t) {
        List<T> list = new ArrayList<T>(t.length);
        for (T t1 : t) {
            list.add(t1);
        }
        return list;
    }
}
