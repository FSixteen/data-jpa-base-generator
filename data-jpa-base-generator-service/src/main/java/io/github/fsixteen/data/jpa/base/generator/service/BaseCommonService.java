package io.github.fsixteen.data.jpa.base.generator.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
        if (DAO_TYPES.containsKey(clazz)) {
            return DAO_TYPES.get(clazz);
        }
        synchronized (BaseCommonService.class) {
            if (DAO_TYPES.containsKey(clazz)) {
                return DAO_TYPES.get(clazz);
            }
            Type[] types = analyseDaoTypes(clazz);
            Optional<Type[]> daoTypes = Objects.nonNull(types) ? Optional.ofNullable(types) : Optional.empty();
            DAO_TYPES.put(clazz, daoTypes);
            return daoTypes;
        }
    }

    /**
     * 获取 {@code BaseDao} 泛型信息.<br>
     * 
     * @param clazz {@link BaseDao} Class对象.
     * @return Type[]
     */
    private static Type[] analyseDaoTypes(final Class<?> clazz) {
        ParameterizedType[] parameterizedTypes = analyseDaoParameterizedTypes(clazz);
        if (Objects.nonNull(parameterizedTypes)) {
            if (parameterizedTypes.length == 1) { // 直接继承, 不考虑间接信息
                return parameterizedTypes[0].getActualTypeArguments();
            } else {
                Type[] types = new Type[2];
                Type[] actualTypeArguments = parameterizedTypes[0].getActualTypeArguments();
                if (actualTypeArguments[0] instanceof TypeVariable<?>) {
                    types[0] = resolveTypeVariable((TypeVariable<?>) actualTypeArguments[0], parameterizedTypes, 1);
                } else {
                    types[0] = actualTypeArguments[0];
                }
                if (actualTypeArguments[1] instanceof TypeVariable<?>) {
                    types[1] = resolveTypeVariable((TypeVariable<?>) actualTypeArguments[1], parameterizedTypes, 1);
                } else {
                    types[1] = actualTypeArguments[1];
                }
                if (Objects.nonNull(types[0]) && Objects.nonNull(types[1])) {
                    return types;
                }
            }
        }
        return null;
    }

    /**
     * 返回实际泛型类型.<br>
     * 
     * @param typeVariable       TypeVariable
     * @param parameterizedTypes ParameterizedType
     * @param currentIndex       下标
     * @return Type
     */
    private static Type resolveTypeVariable(TypeVariable<?> typeVariable, ParameterizedType[] parameterizedTypes, int currentIndex) {
        String variableName = typeVariable.getName();
        for (int i = currentIndex; i < parameterizedTypes.length; i++) {
            ParameterizedType parameterizedType = parameterizedTypes[i];

            Type[] actualArgs = parameterizedType.getActualTypeArguments();
            TypeVariable<?>[] typeParams = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();

            for (int j = 0; j < typeParams.length && j < actualArgs.length; j++) {
                if (variableName.equals(typeParams[j].getName())) {
                    if (actualArgs[j] instanceof TypeVariable<?>) {
                        variableName = ((TypeVariable<?>) actualArgs[j]).getName();
                    } else {
                        return actualArgs[j];
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取 {@code BaseDao} 整个继承链路.<br>
     * 
     * @param clazz {@link BaseDao} Class对象.
     * @return ParameterizedType[]
     */
    private static ParameterizedType[] analyseDaoParameterizedTypes(final Class<?> clazz) {
        Type[] types = clazz.getGenericInterfaces();
        if (types.length > 0) {
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (parameterizedType.getRawType() == BaseDao.class) {
                        return new ParameterizedType[] { parameterizedType };
                    } else {
                        Type rawType = parameterizedType.getRawType();
                        if (rawType instanceof Class<?>) {
                            ParameterizedType[] nextTypes = analyseDaoParameterizedTypes((Class<?>) rawType);
                            if (Objects.nonNull(nextTypes)) {
                                ParameterizedType[] currTypes = Arrays.copyOf(nextTypes, nextTypes.length + 1);
                                currTypes[nextTypes.length] = parameterizedType;
                                return currTypes;
                            }
                        }
                    }
                }
            }
            // 为找到 ParameterizedType 相关内容, 可能是代理, 按照 Class 继续处理.
            for (Type type : types) {
                if (type instanceof Class<?>) {
                    ParameterizedType[] _types = analyseDaoParameterizedTypes((Class<?>) type);
                    if (Objects.nonNull(_types)) {
                        return _types;
                    }
                }
            }
        }
        return null;
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
                    Class<ID> tableClass = getDaoTypes(clazz).filter(it -> 2 <= it.length).map(it -> it[1]).map(it -> Class.class.cast(it))
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
        if (null == eles || eles.isEmpty()) {
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
