package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

/**
 * 数组辅助工具.
 *
 * <p>
 * 该类型保留少量与解释器内部兼容相关的基础数组判断逻辑,
 * 主要用于 null-safe 的包含判断.
 * </p>
 *
 * <p>
 * 当前功能刻意保持收敛, 没有引入更复杂的集合转换或数组操作,
 * 只承担老代码与新 compiled 链路仍会复用的最小公共能力.
 * </p>
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
