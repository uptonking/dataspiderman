package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于抽取元素的注解
 * 它描述了一种抽取规则
 * ExtractBy注解主要作用于字段，它表示使用这个抽取规则，将抽取到的结果保存到这个字段中
 * 在类上使用这个注解，是使用这个结果抽取一个区域，在这个类中的字段上再使用@ExtractBy的话，则是从这个区域而不是整个页面进行抽取
 * <p>
 * Define the extractor for field or class.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ExtractBy {

    /**
     * Extractor expression, support XPath, CSS Selector and regex.
     *
     * @return extractor expression
     */
    String value();

    /**
     * 抽取表达式的类型
     * types of extractor expressions
     */
    enum Type {
        XPath, Regex, Css, JsonPath
    }

    /**
     * 默认使用xpath抽取
     * Extractor type, support XPath, CSS Selector and regex.
     *
     * @return extractor type
     */
    Type type() default Type.XPath;

    /**
     * 此字段不允许为空。如果为空，这条抽取到的结果会被丢弃。
     * 对于一些页面的关键性属性（例如文章的标题等），设置notnull为true，可以有效的过滤掉无用的页面
     * <p>
     * Define whether the field can be null.<br>
     * If set to 'true' and the extractor get no result, the entire class will be discarded. <br>
     *
     * @return whether the field can be null
     */
    boolean notNull() default false;

    /**
     * types of source for extracting.
     */
    enum Source {
        /**
         * extract from the content extracted by class extractor
         */
        SelectedHtml,
        /**
         * extract from the raw html
         */
        RawHtml,
        RawText
    }

    /**
     * The source for extracting. <br>
     * It works only if you already added 'ExtractBy' to Class. <br>
     *
     * @return the source for extracting
     */
    Source source() default Source.SelectedHtml;

    /**
     * 表示这条抽取规则是对应多条记录还是单条记录。对应的，这个字段必须为java.util.List类型。
     * 在0.4.3之后，当字段为List类型时，这个属性会自动为true，无须再设置。
     * <p>
     * Define whether the extractor return more than one result.
     * When set to 'true', the extractor return a list of string (so you should define the field as List). <br>
     * <p>
     * Deprecated since 0.4.2. This option is determined automatically by the class of field.
     *
     * @return whether the extractor return more than one result
     * @deprecated since 0.4.2
     */
    @Deprecated
    boolean multi() default false;

}
