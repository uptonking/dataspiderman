package us.codecraft.webmagic.utils;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 反射工具类
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class ClassUtils {

    /**
     * 返回当前类和父类的字段
     */
    public static Set<Field> getFieldsIncludeSuperClass(Class clazz) {
        Set<Field> fields = new LinkedHashSet<Field>();
        Class current = clazz;
        while (current != null) {
            Field[] currentFields = current.getDeclaredFields();
            for (Field currentField : currentFields) {
                fields.add(currentField);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

}
