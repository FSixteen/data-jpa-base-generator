package io.github.fsixteen.data.jpa.base.generator.annotations.plugins.utils;

/**
 * 数组工具.
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class ArrayUtils {

    /**
     * 判断给定数据是否存在于给定数据数组中.
     * 
     * @param <T>  数据泛型类
     * @param eles 数据数组
     * @param ele  数据
     * @return boolean
     */
    public static final <T> boolean contains(final T[] eles, final T ele) {
        if (null == eles || 0 == eles.length) {
            return false;
        } else {
            if (null == ele) {
                for (T e : eles) {
                    if (null == e) {
                        return true;
                    }
                }
            } else if (eles.getClass().getComponentType().isInstance(ele)) {
                for (T e : eles) {
                    if (e.equals(ele)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
