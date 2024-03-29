package io.github.fsixteen.data.jpa.base.generator.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.jpa.BaseDao;
import io.github.fsixteen.data.jpa.base.generator.utils.JsonIgnoreUtils;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class BaseCommonService {

    /** DAO对象TYPE信息. */
    private static final Map<Class<?>, Optional<Type[]>> DAO_TYPES = new ConcurrentHashMap<>();
    /** DAO对象Table Class. */
    private static final Map<Class<?>, Class<?>> TABLE_CLASS = new ConcurrentHashMap<>();
    /** DAO对象ID Class. */
    private static final Map<Class<?>, Class<?>> ID_CLASS = new ConcurrentHashMap<>();
    /** Class 忽略字段String[]. */
    private static final Map<Class<?>, String[]> CLASS_IGNORE_FIELDS_SET = new ConcurrentHashMap<>();

    /**
     * 获取{@link Type}对象信息.<br>
     * 
     * @param clazz {@link BaseDao} Class对象.
     * @return Optional&lt;Type[]&gt;, Class对象的{@link Type}对象.
     */
    public static Optional<Type[]> getDaoTypes(final Class<?> clazz) {
        if (!DAO_TYPES.containsKey(clazz) || Objects.isNull(DAO_TYPES.get(clazz))) {
            synchronized (BaseCommonService.class) {
                if (!DAO_TYPES.containsKey(clazz) || Objects.isNull(DAO_TYPES.get(clazz))) {
                    final List<Type> types = new ArrayList<>(Arrays.asList(clazz.getGenericInterfaces()));
                    Type type = null;
                    while (!types.isEmpty() && Objects.nonNull(type = types.remove(0))) {
                        if (type instanceof ParameterizedType && ParameterizedType.class.cast(type).getRawType() == BaseDao.class) {
                            Optional<Type[]> daoTypes = Optional.ofNullable(ParameterizedType.class.cast(type).getActualTypeArguments());
                            DAO_TYPES.put(clazz, daoTypes);
                            break;
                        }
                        if (type instanceof Class) {
                            types.addAll(Arrays.asList(Class.class.cast(type).getGenericInterfaces()));
                        }
                    }
                }
            }
        }
        return DAO_TYPES.get(clazz);
    }

    /**
     * 获取{@link BaseDao}实体Class信息.<br>
     *
     * @param <T>   实体类
     * @param clazz {@link BaseDao} Class对象.
     * @return Class&lt;ID&gt;, Class对象的{@link Type}对象.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTableClass(final Class<?> clazz) {
        if (!TABLE_CLASS.containsKey(clazz) || Objects.isNull(TABLE_CLASS.get(clazz))) {
            synchronized (BaseCommonService.class) {
                if (!TABLE_CLASS.containsKey(clazz) || Objects.isNull(TABLE_CLASS.get(clazz))) {
                    Class<T> tableClass = getDaoTypes(clazz).filter(it -> 1 <= it.length).map(it -> it[0]).map(it -> Class.class.cast(it))
                        .orElseThrow(() -> new RuntimeException(String.format("解析DAO泛型失败 :: %s", clazz.getName())));
                    TABLE_CLASS.put(clazz, tableClass);
                }
            }
        }
        return Class.class.cast(TABLE_CLASS.get(clazz));
    }

    /**
     * 获取{@link BaseDao}实体主键Class信息.<br>
     *
     * @param <ID>  实体主键类
     * @param clazz {@link BaseDao} Class对象.
     * @return Class&lt;ID&gt;, Class对象的{@link Type}对象.
     */
    @SuppressWarnings("unchecked")
    public static <ID> Class<ID> getIdClass(final Class<?> clazz) {
        if (!ID_CLASS.containsKey(clazz) || Objects.isNull(ID_CLASS.get(clazz))) {
            synchronized (BaseCommonService.class) {
                if (!ID_CLASS.containsKey(clazz) || Objects.isNull(ID_CLASS.get(clazz))) {
                    Class<ID> tableClass = getDaoTypes(clazz).filter(it -> 1 <= it.length).map(it -> it[1]).map(it -> Class.class.cast(it))
                        .orElseThrow(() -> new RuntimeException(String.format("解析DAO泛型失败 :: %s", clazz.getName())));
                    ID_CLASS.put(clazz, tableClass);
                }
            }
        }
        return Class.class.cast(ID_CLASS.get(clazz));
    }

    /**
     * 获取指定集合元素对象内忽略的字段.<br>
     * 
     * @param <T>  元素类型
     * @param eles 元素集合
     * @return String[]
     */
    public static <T> String[] jsonIgnoreProperties(List<T> eles) {
        if (null == eles || 0 == eles.size()) {
            return new String[0];
        }
        T arg = eles.get(0);
        return jsonIgnoreProperties(arg);
    }

    /**
     * 获取指定元素对象内忽略的字段.<br>
     * 
     * @param <T> 元素类型
     * @param ele 元素
     * @return String[]
     */
    public static <T> String[] jsonIgnoreProperties(T ele) {
        if (null == ele) {
            return new String[0];
        }
        if (CLASS_IGNORE_FIELDS_SET.containsKey(ele.getClass())) {
            return CLASS_IGNORE_FIELDS_SET.get(ele.getClass());
        } else {
            String[] ignoreSet = JsonIgnoreUtils.jsonIgnoreProperties(ele);
            CLASS_IGNORE_FIELDS_SET.put(ele.getClass(), ignoreSet);
            return ignoreSet;
        }
    }

}
