package us.codecraft.xsoup.w3c;

import org.w3c.dom.TypeInfo;

/**
 * 可以指向Element或Attr的虚拟类型
 * 单例
 * todo 单例实现不安全
 *
 * @author code4crafer@gmail.com
 */
public class DummyTypeInfo implements TypeInfo {

    private static final DummyTypeInfo INSTANCE = new DummyTypeInfo();

    public static DummyTypeInfo getInstance() {
        return INSTANCE;
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public String getTypeNamespace() {
        return null;
    }

    @Override
    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
        return false;
    }
}
