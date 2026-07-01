# Data JPA Base Generator

基于注解的JPA Specification快速生成框架工具，为Spring Data JPA应用提供优雅的查询解决方案。

## 🎯 核心功能

### **注解驱动查询**
- 使用简洁的注解方式定义复杂的多条件查询
- 支持多种数据类型（数值、字符串、集合等）的条件判断
- 自动类型检测和转换，简化开发流程

### **丰富查询操作符**
- **比较操作**: `@GreaterThan`, `@LessThan`, `@Between`, `@Equal`等
- **集合操作**: `@In`, `@NotIn`, `@InTable`等
- **字符串匹配**: `@Like`, `@NotLike`, `@Contains`, `@StartsWith`等
- **空值判断**: `@IsNull`, `@IsNotNull`等

### **灵活配置选项**
- `required`: 控制条件的必须性（必填字段 vs 可选过滤）
- `ignoreNull`: 是否忽略null值
- `ignoreEmpty`: 是否忽略空字符串
- `not`: 逻辑反向（如大于变成小于等于）

## 🏗️ 架构设计

### **模块化结构**
```
data-jpa-base-generator-parent (Maven父项目)
├── data-jpa-base-generator-annotations    # 注解定义模块
├── data-jpa-base-generator-interpreter   # 注解解释器核心模块
├── data-jpa-base-generator-service       # 基础服务接口
├── data-jpa-base-generator-controller    # REST控制器接口
├── data-jpa-base-generator-entities      # 实体类定义
├── data-jpa-base-generator-common-utils  # 通用工具
└── data-jpa-base-generator-bom          # 依赖管理
```

### **核心处理流程**
1. **注解解析** (`AnnotationDescriptor`) - 将注解属性解析为内部数据结构
2. **字段转换** (`AbstractComputerBuilderPlugin`) - 将Java对象字段转换为JPA Criteria表达式
3. **谓词创建** (具体插件) - 使用CriteriaBuilder创建具体的Predicate
4. **逻辑处理** - 应用忽略规则、反向逻辑等

## 📖 快速开始

### **1. 实体类定义**

```java
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.*;

@Entity
public class UserEntity {
    @Id
    private Long id;

    private String name;
    private Integer age;
    private Date createTime;
    private Boolean status;

    // 年龄大于指定值（必须条件）
    @GreaterThan(field = "age", required = true)
    private Integer minAge;

    // 姓名包含关键字（可选条件）
    @Like(field = "name", required = false)
    private String keyword;

    // 创建时间在范围内
    @Between(field = "createTime")
    private DateRange dateRange;

    // 状态精确匹配
    @Equal(field = "status")
    private Boolean userStatus;
}
```

### **2. Repository接口继承**

```java
import io.github.fsixteen.data.jpa.base.generator.jpa.BaseDao;

public interface UserRepository extends BaseDao<UserEntity, Long> {
    // 无需额外实现，直接使用注解查询
}
```

### **3. Service层使用**

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Page<UserEntity> searchUsers(UserQuery query) {
        return userRepository.findAll(createSpecification(query), query.toPageable());
    }

    private Specification<UserEntity> createSpecification(UserQuery query) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 添加各种注解生成的谓词条件
            predicates.add(cb.equal(root.get("status"), true));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
```

## 🔧 详细配置说明

### **FieldType 字段类型**
```java
AUTO:     // 自动计算（默认字段名）
LITERAL:  // 字面量值
FUNCTION: // Spring Data函数
UDFUNCTION:// 自定义函数
```

### **ValueType 值类型**
```java
VALUE:     // 从实体字段取值
COLUMN:    // 指定列名
LITERAL:   // 字面量值
FUNCTION:  // Spring Data函数
```

### **required 字段详解**
- **`required=true`**: 字段有值时正常计算，为null时返回 `field IS NULL`
- **`required=false`**: 字段有值时正常计算，为null时不参与查询

### **忽略规则配置**
```java
// 忽略null值和空字符串
@Like(required = false, ignoreNull = true, ignoreEmpty = true)

// 不忽略任何值（严格匹配）
@Equal(required = true, ignoreNull = false, ignoreEmpty = false)
```

## 🚀 高级特性

### **多条件分组**
```java
@Template(groups = {
    @GroupInfo("age_group", 1),
    @GroupInfo("name_group", 2)
})
private AgeRange ageRange;
```

### **自定义函数支持**
```java
@Function(value = "UPPER", type = String.class)
private String upperKeyword;
```

### **缓存优化**
- 字段处理器缓存 (`FIELD_PROCESSOR_CACHE`)
- 值处理器缓存 (`VALUE_PROCESSOR_CACHE`)
- 函数参数处理器缓存 (`ARGS_FUN_PROCESSOR_CACHE`)

## 📊 性能优化

### **自动类型检测**
- 运行时动态检测字段类型
- 智能选择最优的JPA Criteria API调用方式
- 避免不必要的类型转换开销

### **懒加载策略**
- 只在需要时才创建Predicate
- null值直接返回null，避免无效查询
- 集合操作自动去重和优化

## 🛡️ 安全性

### **输入验证**
- 注解属性完整性检查
- 反射操作异常处理
- SQL注入防护

### **权限控制**
- 集成Spring Security
- `@FsnPreAuthorize`注解支持
- 方法级权限控制

## 🧪 测试覆盖

### **单元测试**
- BuilderPlugin测试覆盖率 > 95%
- 边界条件测试
- 异常场景测试

### **集成测试**
- Spring Boot集成测试
- JPA Criteria API正确性验证
- 数据库兼容性测试

## 📦 依赖要求

- **JDK**: 11+
- **Spring Boot**: 2.7.x
- **Spring Data JPA**: 2.7.x
- **Hibernate**: 5.6.x
- **Maven**: 3.6+

## 🤝 贡献指南

1. Fork项目到个人仓库
2. 创建特性分支 (`git checkout -b feature/new-feature`)
3. 提交更改 (`git commit -am 'Add new feature'`)
4. 推送到分支 (`git push origin feature/new-feature`)
5. 创建Pull Request

## 📄 许可证

Apache License 2.0

## 🙏 致谢

本项目基于Spring Data JPA生态构建，感谢所有开源社区的贡献者。
