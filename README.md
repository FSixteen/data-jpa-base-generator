# Data JPA Base Generator

基于注解的 JPA Specification 生成框架. 当前工程已经统一到 `canonical annotation DSL + compiled-only interpreter` 架构：注解层负责声明 DSL, 解释器先把注解编译成统一 spec, 再通过 provider 落成最终 JPA `Predicate`.

## 模块结构

- `data-jpa-base-generator-annotations`
  注解 DSL、常量、扩展接口
- `data-jpa-base-generator-interpreter`
  注解解析、compiled spec、provider、Predicate 构建
- `data-jpa-base-generator-entities`
  基础实体、分组对象、查询模型
- `data-jpa-base-generator-service`
  通用服务层、配置、异常定义
- `data-jpa-base-generator-controller`
  控制器基类与权限扩展
- `data-jpa-base-generator-common-utils`
  转换器、序列化器、工具类
- `data-jpa-base-generator-bom`
  BOM 依赖管理

## 环境与兼容性

- 使用 Java 11 开发与构建
- 主源码以 Java 8 目标字节码发布, 便于在 Java 8 运行环境中集成
- 依赖基线当前对齐到：
  - Spring Framework `5.3.39`
  - Spring Boot `2.7.18`
  - Spring Data JPA `2.7.18`
  - Hibernate `5.6.15.Final`
  - Jakarta Persistence API `2.2.3`
  - Jakarta Validation API `2.0.2`

仓库内常用命令：

- `mvn test`
  运行全部测试
- `mvn package`
  完整打包
- `mvn -pl data-jpa-base-generator-interpreter -am test`
  只编译并测试解释器及其依赖模块

## 依赖引入

推荐优先通过 BOM 锁定版本：

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.github.fsixteen</groupId>
      <artifactId>data-jpa-base-generator-bom</artifactId>
      <version>1.1.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

按能力选择模块：

```xml
<dependencies>
  <dependency>
    <groupId>io.github.fsixteen</groupId>
    <artifactId>data-jpa-base-generator-annotations</artifactId>
  </dependency>
  <dependency>
    <groupId>io.github.fsixteen</groupId>
    <artifactId>data-jpa-base-generator-interpreter</artifactId>
  </dependency>
  <dependency>
    <groupId>io.github.fsixteen</groupId>
    <artifactId>data-jpa-base-generator-entities</artifactId>
  </dependency>
  <dependency>
    <groupId>io.github.fsixteen</groupId>
    <artifactId>data-jpa-base-generator-service</artifactId>
  </dependency>
  <dependency>
    <groupId>io.github.fsixteen</groupId>
    <artifactId>data-jpa-base-generator-controller</artifactId>
  </dependency>
</dependencies>
```

通常场景建议：

- 只需要注解 DSL 和手动构建 `Specification`：`annotations + interpreter`
- 需要基础查询模型、分页接口、通用实体：再加 `entities`
- 需要默认 CRUD service/controller：再加 `service + controller`
- 需要常见 converter / serializer / 工具类：加 `common-utils`

## 当前架构

当前代码的稳定事实只有一套：

- 注解层统一走 canonical DSL
- interpreter 运行时统一走 compiled-only 主链路
- `@Constraint` 的执行扩展点是 `CompiledPredicateProvider`
- 公开入口仍保留历史 `CollectionCache -> AnnotationCollection -> ComputerCollection -> BuilderType`
- 工程内部优先直接使用 `selection / existence` 语义入口, 而不是继续扩散旧 builder/plugin 风格

统一内部模型：

- `CompiledAnnotationSpec`
- `CompiledPredicateSpec`
- `PredicateExpression`
- `PredicateOptionsSpec`
- `PredicateGroupSpec`
- `CompiledSubquerySpec`
- `CompiledPredicateProvider`
- `CompiledPredicateResult`

运行时不再依赖旧的 flat builder/plugin 执行模型.

## 稳定公开入口

对外稳定调用链保持不变：

```java
final AnnotationCollection collection = CollectionCache.getAnnotationCollection(args.getClass());
Predicate predicate = collection.toComputerCollection()
    .withArgs(args)
    .withSpecification(root, query, cb)
    .build(BuilderType.SELECTED)
    .getPredicate(cb);
```

这条链路仍然是公开稳定入口；内部执行已经统一转为 compiled 主链路.

如果在工程内部不需要守住历史边界, 优先直接使用：

- `AnnotationCollection.isSelectionEmpty() / isExistenceEmpty()`
- `CompiledPredicateFacade.selection(...) / existence(...)`
- `CompiledPredicateFacade.selectionPredicate(...) / existencePredicate(...)`
- `CompiledPredicateFacade.selectionPredicateArray(...) / existencePredicateArray(...)`

`@PredicateRole` 目前以 `selection / existence` 为主命名；`selectable / existed` 仍作为兼容别名被识别.

## 最小使用示例

定义查询对象：

```java
public class UserQuery implements Entity, BasePageRequest {

    @Equal
    private String status;

    @Length(op = CompareOp.GTE)
    private Integer nameLength;

    @Between(left = @Expr(path = "createdAt"))
    private List<String> createdAtRange;

    private int page = 0;

    private int size = 20;

    public String getStatus() {
        return status;
    }

    public Integer getNameLength() {
        return nameLength;
    }

    public List<String> getCreatedAtRange() {
        return createdAtRange;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }
}
```

其中 `@Length` 的零配置默认会把 `nameLength` 推断为目标路径 `name`, 即生成类似 `length(root.name) >= ?` 的谓词；如果字段名不以 `Length` 结尾, 则直接使用当前字段名本身作为目标路径. 该注解也支持直接标注在 getter 方法上, 只要属性可读即可参与 compiled 扫描.

当前 `@Length` 的运行时语义与 JPA 主链路保持一致：

- `length(null)` 按 SQL/JPA 语义返回 `null`, 不会把 `null` 回退为长度 `0`
- `BETWEEN / NOT_BETWEEN` 在运行时与 JPA 构建阶段都要求“恰好 2 个值”
- `length / lower / upper / trim` 只接受字符串参数, 不再对非字符串值隐式调用 `toString()`

手动构建查询谓词：

```java
final UserQuery args = new UserQuery();
final AnnotationCollection collection = CollectionCache.getAnnotationCollection(UserQuery.class);

Specification<UserEntity> specification = (root, query, cb) ->
    CompiledPredicateFacade.selectionPredicate(collection, args, root, query, cb);
```

如果需要保持历史公开入口, 也可以继续使用：

```java
final AnnotationCollection collection = CollectionCache.getAnnotationCollection(UserQuery.class);
Predicate predicate = collection.toComputerCollection()
    .withArgs(args)
    .withSpecification(root, query, cb)
    .build(BuilderType.SELECTED)
    .getPredicate(cb);
```

## Service / Controller 集成

### 1. Repository

基础仓储接口是：

```java
public interface UserDao extends BaseDao<UserEntity, Long> {
}
```

`BaseDao` 已经组合了 `JpaRepository` 和 `JpaSpecificationExecutor`.

### 2. Service

如果只需要查询能力, 实现 `BaseSelectService` 即可：

```java
public class UserSelectServiceImpl implements BaseSelectService<UserEntity, Long, UserQuery> {

    private final UserDao userDao;

    public UserSelectServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public BaseDao<UserEntity, Long> getDao() {
        return userDao;
    }
}
```

`BaseSelectService` 默认会：

- 从 `CollectionCache` 获取查询对象注解缓存
- 通过 `CompiledPredicateFacade.selectionPredicate(...)` 构建查询条件
- 把固定条件 `selectFixedPredicate()` 与注解条件统一合并
- 根据 `BasePageRequest#getPage()` 和 `getSize()` 生成分页请求

插入、更新、删除和聚合场景可分别复用：

- `BaseInsertService`
- `BaseUpdateService`
- `BaseDeleteService`
- `BaseService`
- `AbstractBaseAggService`

### 3. Controller

如果需要通用 REST 端点, 可以直接复用基类接口：

- `BaseSelectController`
- `BaseInsertController`
- `BaseUpdateController`
- `BaseDeleteController`
- `BaseController`

其中 `BaseSelectController` 默认提供：

- `POST /select`
  分页查询
- `POST /select/all`
  完整查询

单条详情查询 `findById(...)` 已抽成无映射逻辑方法, 业务控制器可以根据主键类型选择 `@PathVariable` 或 `@RequestBody` 暴露端点.

### 4. Spring 配置与自动装配

- `data-jpa-base-generator-service` 通过 `spring.factories` 注册了 `AppContextInitializer`
- `DataJpaGeneratorConfig` 可选注入全局默认排序 `Supplier<Sort>`
- `data-jpa-base-generator-controller` 提供 `@FsnPreAuthorize` 相关自动配置

示例：

```java
@Bean
public DataJpaGeneratorConfig dataJpaGeneratorConfig() {
    return new DataJpaGeneratorConfig()
        .withDefaultSortColumns(() -> Sort.by(Sort.Direction.DESC, "id"));
}
```

`@FsnPreAuthorize` 目前支持在类型级别声明以下操作的权限表达式：

- `insert`
- `update`
- `delete`
- `select`
- `selectAll`
- `selectOne`

## 注解参考

下面按 `annotations/plugins` 当前真实代码清单整理. 每个注解都给一句说明和一个最小示例.

### 1. 表达式与公共配置

- `@Expr`
  统一操作数模型, 可表达路径、运行时值、字面量、函数.
  示例：`@Expr(path = "user.name")`、`@Expr(type = ExprType.VALUE, valueField = "keyword")`
- `@ExprArg`
  `@ExprFunction` 的一级参数.
  示例：`@ExprArg(type = ExprType.VALUE, valueField = "keyword")`
- `@ExprFunction`
  顶层函数表达式声明.
  示例：`@ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.VALUE, valueField = "keyword") })`
- `@PredicateOptions`
  统一承载 `scope/groups/required/not/ignore*/trim`.
  示例：`@PredicateOptions(required = true, trim = true)`
- `@CollectionPolicy`
  成员判断与 split/filter 族的集合策略.
  示例：`@CollectionPolicy(split = true, decollator = ",")`
- `@PredicateRef`
  `Cases` 分支里的回退命中入口, 仅在未声明 canonical `when` 和 `group` 时生效.
  示例：`@PredicateRef(predicateClass = MyPredicate.class)`
- `@ProcessorRef`
  `Cases` 分支里的处理器入口, 可接管最终谓词产出.
  示例：`@ProcessorRef(processorClass = MyPredicateProcessor.class)`

### 2. 递归表达式辅助注解

- `@NestedExprArg`
  二级函数参数, 仅用于 `@NestedExprFunction` 内部.
  示例：`@NestedExprArg(type = ExprType.LITERAL, literal = "-")`
- `@NestedExprFunction`
  二级嵌套函数.
  示例：`@NestedExprFunction(name = "concat", type = String.class, args = { @NestedExprArg(type = ExprType.PATH, path = "firstName") })`
- `@DeepNestedExprArg`
  三级函数参数, 当前表达式嵌套封顶层.
  示例：`@DeepNestedExprArg(type = ExprType.VALUE, valueField = "suffix")`
- `@DeepNestedExprFunction`
  三级嵌套函数.
  示例：`@DeepNestedExprFunction(name = "trim", type = String.class, args = { @DeepNestedExprArg(type = ExprType.PATH, path = "name") })`

### 3. canonical 根注解

- `@Compare`
  通用二元比较根注解.
  示例：`@Compare(op = CompareOp.EQ, left = @Expr(path = "status"), right = @Expr(type = ExprType.VALUE))`
- `@Range`
  通用范围比较根注解.
  示例：`@Range(op = CompareOp.BETWEEN, left = @Expr(path = "createdAt"), right = @Expr(type = ExprType.VALUE), extra = { @Expr(type = ExprType.VALUE, valueField = "endAt") })`
- `@Membership`
  通用成员判断根注解.
  示例：`@Membership(op = CompareOp.IN, left = @Expr(path = "status"), right = @Expr(type = ExprType.VALUE), collection = @CollectionPolicy(split = true))`
- `@TextMatch`
  通用文本匹配根注解.
  示例：`@TextMatch(op = CompareOp.LIKE, left = @Expr(path = "userName"), right = @Expr(type = ExprType.VALUE))`
- `@NullCheck`
  通用空值判断根注解.
  示例：`@NullCheck(op = CompareOp.IS_NULL, left = @Expr(path = "deletedAt"))`
- `@SubqueryPredicate`
  通用子查询根注解, 可直接声明 `mode/targetEntity/left/right/select/where`.
  示例：`@SubqueryPredicate(mode = SubqueryMode.EXISTS, targetEntity = AuditEntity.class, left = @Expr(path = "id"), right = @Expr(path = "userId"), select = @Expr(path = "id"))`
- `@Cases`
  分支根注解, 统一承载一组 `@Case`, 并支持 `otherwise = @CaseElse(...)` 兜底分支.
  示例：`@Cases(value = { @Case(...) }, otherwise = @CaseElse(enabled = true), left = @Expr(path = "status"))`
  说明：`Cases` 分支里的 canonical `when` 条件复用统一运行时比较语义, 因此 `IN / BETWEEN / length(...)` 的解释规则会与 JPA 主链路保持一致.

### 4. 子查询 where-group 辅助注解

- `@SubqueryGroup`
  顶层子查询内部条件组.
  示例：`@SubqueryGroup(compare = { @Compare(left = @Expr(path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")) })`
- `@NestedSubqueryGroup`
  `SubqueryGroup` 的下一层子组.
  示例：`@NestedSubqueryGroup(compare = { @Compare(left = @Expr(path = "status"), right = @Expr(type = ExprType.VALUE)) })`
- `@DeepSubqueryGroup`
  第三层子查询子组, 当前 group 嵌套封顶层.
  示例：`@DeepSubqueryGroup(compare = { @Compare(left = @Expr(path = "type"), right = @Expr(type = ExprType.VALUE)) })`

### 5. 比较快捷注解

- `@Equal`
  零配置等于, 默认 `currentFieldPath = currentFieldValue`.
  示例：`@Equal private String status = "ACTIVE";`
- `@NotEqual`
  不等于快捷包装.
  示例：`@NotEqual private String status = "DELETED";`
- `@Gt`
  大于快捷包装.
  示例：`@Gt private Integer age = 18;`
- `@Gte`
  大于等于快捷包装.
  示例：`@Gte private Integer score = 60;`
- `@Lt`
  小于快捷包装.
  示例：`@Lt private Integer amount = 100;`
- `@Lte`
  小于等于快捷包装.
  示例：`@Lte private Integer amount = 100;`
- `@GreaterThan`
  `@Gt` 的长名别名.
  示例：`@GreaterThan private Integer age = 18;`
- `@GreaterThanOrEqualTo`
  `@Gte` 的长名别名.
  示例：`@GreaterThanOrEqualTo private Integer score = 60;`
- `@LessThan`
  `@Lt` 的长名别名.
  示例：`@LessThan private Integer amount = 100;`
- `@LessThanOrEqualTo`
  `@Lte` 的长名别名.
  示例：`@LessThanOrEqualTo private Integer amount = 100;`
- `@IgnoreCaseEqual`
  忽略大小写相等, 内部会把左右两边统一包成 `lower(...)`.
  示例：`@IgnoreCaseEqual private String status = "ACTIVE";`
- `@Length`
  字符串长度比较快捷包装, 默认把形如 `nameLength` 的属性推断到目标路径 `name`, 并生成 `length(name)` 比较；也支持标注在 getter 方法上. 若属性名不以 `Length` 结尾, 则回退为当前属性名本身.
  示例：`@Length(op = CompareOp.GTE) private Integer nameLength = 3;`

### 6. 文本匹配快捷注解

- `@Like`
  包含匹配.
  示例：`@Like(left = @Expr(path = "userName")) private String keyword = "demo";`
- `@NotLike`
  不包含匹配.
  示例：`@NotLike(left = @Expr(path = "userName")) private String keyword = "demo";`
- `@LeftLike`
  左模糊匹配.
  示例：`@LeftLike(left = @Expr(path = "userName")) private String prefix = "adm";`
- `@RightLike`
  右模糊匹配.
  示例：`@RightLike(left = @Expr(path = "userName")) private String suffix = "son";`
- `@StartWith`
  前缀匹配.
  示例：`@StartWith(left = @Expr(path = "userName")) private String prefix = "adm";`
- `@EndWith`
  后缀匹配.
  示例：`@EndWith(left = @Expr(path = "email")) private String suffix = ".com";`
- `@IgnoreCaseLike`
  忽略大小写包含匹配.
  示例：`@IgnoreCaseLike(left = @Expr(path = "userName")) private String keyword = "Demo";`

### 7. 范围、成员、空值快捷注解

- `@Between`
  区间判断.
  示例：`@Between(left = @Expr(path = "createdAt")) private List<String> createdAt = Arrays.asList("2024-01-01", "2024-12-31");`
- `@NotBetween`
  非区间判断.
  示例：`@NotBetween(left = @Expr(path = "createdAt")) private List<String> createdAt = Arrays.asList("2024-01-01", "2024-12-31");`
- `@In`
  成员判断.
  示例：`@In private List<Integer> status = Arrays.asList(1, 2, 3);`
- `@NotIn`
  非成员判断.
  示例：`@NotIn private List<Integer> status = Arrays.asList(1, 2, 3);`
- `@SplitIn`
  逗号拆分后再做 `IN`.
  示例：`@SplitIn private String status = "1,2,3";`
- `@SplitNotIn`
  逗号拆分后再做 `NOT IN`.
  示例：`@SplitNotIn private String status = "1,2,3";`
- `@FilterIn`
  带过滤策略的 `IN`.
  示例：`@FilterIn(collection = @CollectionPolicy(regexp = "\\d+")) private List<String> status = Arrays.asList("1", "2");`
- `@FilterNotIn`
  带过滤策略的 `NOT IN`.
  示例：`@FilterNotIn(collection = @CollectionPolicy(regexp = "\\d+")) private List<String> status = Arrays.asList("1", "2");`
- `@Null`
  布尔开关式 null 谓词.
  示例：`@Null private Boolean deleted = Boolean.TRUE;`
- `@IsNull`
  `true` 时生成 `is null`.
  示例：`@IsNull(left = @Expr(path = "deletedAt")) private Boolean deleted = Boolean.TRUE;`
- `@IsNotNull`
  `true` 时生成 `is not null`.
  示例：`@IsNotNull(left = @Expr(path = "deletedAt")) private Boolean deleted = Boolean.TRUE;`

### 8. 子查询快捷注解

- `@Exists`
  单列 exists 子查询快捷包装.
  示例：`@Exists(targetEntity = AuditEntity.class, left = @Expr(path = "id"), right = @Expr(path = "userId"), select = @Expr(path = "id")) private Boolean existsAudit = Boolean.TRUE;`
- `@NotExists`
  单列 not exists 子查询快捷包装.
  示例：`@NotExists(targetEntity = AuditEntity.class, left = @Expr(path = "id"), right = @Expr(path = "userId"), select = @Expr(path = "id")) private Boolean missAudit = Boolean.TRUE;`
- `@InTable`
  单列 `in(subquery)` 快捷包装, 支持 `whereCompare` 和 `where`.
  示例：`@InTable(targetEntity = AuditEntity.class, left = @Expr(path = "id"), right = @Expr(path = "userId")) private Integer userId = 1;`
- `@Unique`
  唯一性快捷包装, 本质是基于 `Existed/Compare` 的语义封装.
  示例：`@Unique(left = @Expr(path = "userName")) private String userName = "admin";`

### 9. Cases 家族

- `@Case`
  `@Cases` 里的单个分支.
  示例：`@Case(when = @CaseWhen(...), then = @CaseThen(...))`
- `@CaseElse`
  `@Cases` 的显式兜底分支, 当前面分支都不命中时执行.
  示例：`@CaseElse(enabled = true, then = @CaseThen(op = CompareOp.EQ, right = @Expr(type = ExprType.LITERAL, literal = "FALLBACK")))`
- `@CaseWhen`
  单个分支的命中条件, canonical `op/left/right/extra` 是主语言, 也支持 `group` 结构化命中树.
  示例：`@CaseWhen(op = CompareOp.EQ, left = @Expr(path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"))`
- `@CaseWhenGroup`
  `CaseWhen` 的顶层命中分组.
  示例：`@CaseWhenGroup(compare = { @Compare(left = @Expr(path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")) })`
- `@NestedCaseWhenGroup`
  `CaseWhenGroup` 的第二层子组.
  示例：`@NestedCaseWhenGroup(junction = GroupComputerType.Type.OR, compare = { @Compare(...) })`
- `@DeepCaseWhenGroup`
  `NestedCaseWhenGroup` 的第三层子组.
  示例：`@DeepCaseWhenGroup(compare = { @Compare(...) })`
- `@CaseThen`
  单个分支的执行动作, canonical `op/right/extra` 是主语言, 也支持 `group` 一次生成多条叶子谓词.
  示例：`@CaseThen(op = CompareOp.EQ, right = @Expr(path = "enabled"))`
- `@CaseThenGroup`
  `CaseThen` 的顶层动作分组.
  示例：`@CaseThenGroup(compare = { @Compare(left = @Expr(path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")) })`
- `@NestedCaseThenGroup`
  `CaseThenGroup` 的第二层子组.
  示例：`@NestedCaseThenGroup(junction = GroupComputerType.Type.OR, compare = { @Compare(...) })`
- `@DeepCaseThenGroup`
  `NestedCaseThenGroup` 的第三层子组.
  示例：`@DeepCaseThenGroup(compare = { @Compare(...) })`

### 10. tuple-values 家族

- `@TupleColumn`
  tuple-values 中的单列映射声明；一列对应一个 `TupleColumn`.
  示例：`@TupleColumn(leftPath = "a", itemIndex = 0, targetType = Integer.class)`
- `@TupleInValues`
  多列参数 tuple 集合条件, 最终降级为 `OR(AND(...), ...)`.
  示例：`@TupleInValues(columns = { @TupleColumn(leftPath = "a", itemIndex = 0), @TupleColumn(leftPath = "b", itemIndex = 1) })`
- `@TupleNotInValues`
  `TupleInValues` 的整体取反版本.
  示例：`@TupleNotInValues(columns = { @TupleColumn(leftPath = "a", itemIndex = 0), @TupleColumn(leftPath = "b", itemIndex = 1) })`

### 11. tuple-subquery 家族

- `@TuplePair`
  多列子查询中的单对 outer/subquery 映射.
  示例：`@TuplePair(left = @Expr(path = "a"), right = @Expr(path = "c1"))`
- `@TupleExists`
  多列相关 exists 子查询, 支持 `pairs + whereCompare + where`.
  示例：`@TupleExists(targetEntity = AuditEntity.class, pairs = { @TuplePair(left = @Expr(path = "userId"), right = @Expr(path = "auditUserId")) })`
- `@TupleNotExists`
  多列相关 not exists 子查询.
  示例：`@TupleNotExists(targetEntity = AuditEntity.class, pairs = { @TuplePair(left = @Expr(path = "userId"), right = @Expr(path = "auditUserId")) })`

## 统一默认规则

当前 canonical/compiled 主链路采用这些默认约定：

- 单值比较默认 `left = PATH(currentField)`
- 单值比较默认 `right = VALUE(currentFieldValue)`
- 范围比较默认从当前字段值读取两个边界
- 成员比较默认从当前字段值读取集合
- 空值判断默认 `left = PATH(currentField)`
- 子查询路径默认按当前字段名回退
- 运行时值绑定只要求 getter 可读, 不再要求 setter 存在
- 顶层默认 `scope/groups` 由 compiled 层补齐为 `default`
- 嵌套空 `@PredicateOptions()` 仍表达“继承父级”

## 公共选项与集合策略

公共选项统一收口到：

- `@PredicateOptions`

它承载：

- `scope`
- `groups`
- `required`
- `not`
- `ignoreNull`
- `ignoreEmpty`
- `ignoreBlank`
- `trim`

集合/拆分/过滤策略统一收口到：

- `@CollectionPolicy`

它承载：

- `split`
- `decollator`
- `regexp`
- `targetType`
- `targetFormat`
- `predicate`

## 子查询模型

普通子查询统一使用：

- `@SubqueryPredicate`
- `@Exists`
- `@NotExists`
- `@InTable`
- `@SubqueryGroup`
- `@NestedSubqueryGroup`
- `@DeepSubqueryGroup`

运行时统一走：

- `CompiledSubquerySpec`
- `CompiledSubquerySupport`
- `CompiledSubqueryGroupSpecs`

支持能力：

- 相关字段条件
- 子查询内部 `where` 分组树
- `AND / OR` 嵌套
- `mode` 编译后直接写入 `CompiledSubquerySpec`

`@InTable` 当前仍是单列 `in(subquery)` 模型, 不负责多列 tuple 子查询.

## Cases 模型

分支模型统一使用：

- `@Cases`
- `@Case`
- `@CaseElse`
- `@CaseWhen`
- `@CaseWhenGroup`
- `@NestedCaseWhenGroup`
- `@DeepCaseWhenGroup`
- `@CaseThen`
- `@CaseThenGroup`
- `@NestedCaseThenGroup`
- `@DeepCaseThenGroup`

规则：

- `CaseWhen` 的 canonical `op/left/right/extra` 是主命中语言
- `CaseWhen.group` 可表达结构化 `AND / OR` 命中树
- `PredicateRef` 只在未声明 canonical `when` 时作为扩展回退入口
  只在未声明 canonical `when` 和 `group` 时作为扩展回退入口
- `CaseThen` 的 canonical `op/right/extra` 是主执行语言
- `CaseThen.group` 可一次生成多条 canonical 叶子谓词并按分组组合
- `ProcessorRef` 可接管最终谓词产出, 但 canonical `then` 规格仍会保留并通过上下文暴露
- `CaseElse` 只在 `otherwise.enabled = true` 且前面分支都未命中时生效
- 当前优先级为：
  `when: canonical > group > predicateRef > always`
  `then: processor > group > canonical > 默认 EQ`

说明：

- 下面的 `Query` 片段为了聚焦注解用法, 省略了 getter / setter
- 实际运行时, 参与 `Cases` 绑定与 `PATH / VALUE_PATH` 读取的属性应保证 getter 可读

示例一：基础分支

```java
public class Query {

    @Cases(
        left = @Expr(type = ExprType.PATH, path = "status"),
        value = {
            @Case(
                when = @CaseWhen(
                    op = CompareOp.EQ,
                    left = @Expr(type = ExprType.PATH, path = "status"),
                    right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")
                ),
                then = @CaseThen(
                    op = CompareOp.EQ,
                    right = @Expr(type = ExprType.LITERAL, literal = "ENABLED")
                )
            )
        }
    )
    private String status = "ACTIVE";
}
```

大致语义：

```sql
where status = 'ENABLED'
```

示例二：`otherwise = @CaseElse(...)` 兜底分支

```java
public class Query {

    @Cases(
        value = {
            @Case(
                when = @CaseWhen(
                    left = @Expr(type = ExprType.PATH, path = "status"),
                    right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")
                ),
                left = @Expr(type = ExprType.PATH, path = "status")
            )
        },
        otherwise = @CaseElse(
            enabled = true,
            then = @CaseThen(
                op = CompareOp.EQ,
                right = @Expr(type = ExprType.LITERAL, literal = "FALLBACK")
            )
        )
    )
    private String status = "INACTIVE";
}
```

当 `status != ACTIVE` 时, 大致语义：

```sql
where status = 'FALLBACK'
```

示例三：`CaseWhen.group` 命中树

```java
public class Query {

    @Cases(
        value = {
            @Case(
                when = @CaseWhen(
                    group = @CaseWhenGroup(
                        compare = {
                            @Compare(
                                left = @Expr(type = ExprType.PATH, path = "status"),
                                right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")
                            )
                        },
                        groups = {
                            @NestedCaseWhenGroup(
                                junction = GroupComputerType.Type.OR,
                                compare = {
                                    @Compare(
                                        left = @Expr(type = ExprType.PATH, path = "phase"),
                                        right = @Expr(type = ExprType.LITERAL, literal = "PENDING")
                                    ),
                                    @Compare(
                                        left = @Expr(type = ExprType.PATH, path = "phase"),
                                        right = @Expr(type = ExprType.LITERAL, literal = "WAITING")
                                    )
                                }
                            )
                        }
                    )
                ),
                left = @Expr(type = ExprType.PATH, path = "status")
            )
        }
    )
    private String status = "ACTIVE";

    private String phase = "PENDING";
}
```

命中条件大致等价于：

```text
status = 'ACTIVE' and (phase = 'PENDING' or phase = 'WAITING')
```

示例四：`CaseThen.group` 多动作输出

```java
public class Query {

    @Cases(
        value = {
            @Case(
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
                then = @CaseThen(
                    group = @CaseThenGroup(
                        compare = {
                            @Compare(
                                left = @Expr(type = ExprType.PATH, path = "status"),
                                right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")
                            ),
                            @Compare(
                                left = @Expr(type = ExprType.PATH, path = "phase"),
                                right = @Expr(type = ExprType.LITERAL, literal = "PENDING")
                            )
                        }
                    )
                )
            )
        }
    )
    private String trigger = "Y";
}
```

大致语义：

```sql
where status = 'ACTIVE' and phase = 'PENDING'
```

## Tuple 能力

### TupleInValues / TupleNotInValues

用途：

- 表达 `(a, b) in ((1, 2), (3, 4))`
- 表达 `(a, b) not in ((1, 2), (3, 4))`

当前实现不会直接生成数据库 tuple `IN`, 而是稳定降级为：

- `TupleInValues -> OR(AND(...), AND(...), ...)`
- `TupleNotInValues -> NOT(OR(AND(...), AND(...), ...))`

规则：

- `tupleField` 为空时, 宿主字段本身就是 tuple 数据源
- `tupleField` 非空时, 宿主字段承担触发器语义, tuple 数据源从指定属性路径读取
- `TupleColumn.leftPath` 与 `TupleColumn.itemPath` 都支持点路径
- 数组行通过 `TupleColumn.itemIndex` 读取列值
- 每列可通过 `targetType / targetFormat` 显式对齐右值类型, 避免 JPA 类型不匹配

当前支持的 tuple 数据源：

- `List<PairDto>`
- `Set<PairDto>`
- `PairDto[]`
- `List<Object[]>`
- `Set<Object[]>`
- `Object[][]`
- `String[][]`
- `Integer[][]`
- `Long[][]`
- `int[][]`
- `long[][]`
- `List<String[]>`
- `Set<Long[]>`
- `List<int[]>`
- `Set<long[]>`

空值策略：

- 外层 source 为 `null` 或空 -> 忽略整条谓词
- 某一行为空 -> 跳过该行
- 某一列值为 `null` -> 跳过该行
- 某一列类型转换失败 -> 直接抛清晰异常

示例一：宿主字段就是 tuple 数据源

```java
public class UserQuery {

    @TupleInValues(columns = {
        @TupleColumn(leftPath = "statusId", itemIndex = 0, targetType = Long.class),
        @TupleColumn(leftPath = "tenantId", itemIndex = 1, targetType = Long.class)
    })
    private Long[][] pairs = new Long[][] {
        { 1L, 7L },
        { 2L, 7L }
    };
}
```

示例二：触发字段 + 外部 tuple 数据源

```java
public class UserQuery {

    @TupleNotInValues(
        tupleField = "pairs",
        columns = {
            @TupleColumn(leftPath = "audit.userId", itemPath = "left.value", targetType = Long.class),
            @TupleColumn(leftPath = "audit.tenantId", itemPath = "right.value", targetType = Long.class)
        }
    )
    private Boolean excludePairs = Boolean.TRUE;

    private java.util.List<PairDto> pairs;
}
```

### TupleExists / TupleNotExists

用途：

- 表达多列相关子查询
- 例如 `exists(select 1 from T sub where sub.c1 = outer.a and sub.c2 = outer.b ...)`

当前实现不直接尝试生成 tuple `in(select c1, c2 ...)`, 统一落为更稳定的：

- `exists (...)`
- `not exists (...)`

规则：

- `TuplePair` 描述 outer/subquery 的多列映射
- `whereCompare` 用于补一个简单的子查询内部附加比较条件
- `where` 用于补完整子查询分组树
- `TupleNotExists` 只是 `TupleExists` 的对称薄包装

示例：

```java
public class UserQuery {

    @TupleExists(
        targetEntity = AuditEntity.class,
        pairs = {
            @TuplePair(left = @Expr(path = "userId"), right = @Expr(path = "auditUserId")),
            @TuplePair(left = @Expr(path = "tenantId"), right = @Expr(path = "auditTenantId"))
        },
        whereCompare = @Compare(
            left = @Expr(path = "status"),
            right = @Expr(type = ExprType.VALUE, valueField = "status")
        ),
        where = @SubqueryGroup(compare = {
            @Compare(
                left = @Expr(path = "deleted"),
                right = @Expr(type = ExprType.LITERAL, literal = "false", javaType = Boolean.class)
            )
        })
    )
    private Boolean existsAuditPair = Boolean.TRUE;

    private Long userId = 1L;

    private Long tenantId = 7L;

    private String status = "ACTIVE";
}
```

## 组合注解与扩展边界

推荐扩展边界：

- `CompiledPredicateProvider`
- `PredicateExpressionTemplate`
- `RegisteredPredicateTemplateProvider`
- `@Constraint(provider = @ProviderRef(...))`

当前 provider 路由规则：

- 内建层优先注册 canonical 根注解与少量特殊注解
- 快捷注解、组合注解通过元注解递归路由命中 canonical provider
- `Selectable / Existed` 通过元注解递归路由到对应 canonical provider
- `@Constraint` 仍是显式自定义 provider 扩展入口

业务组合注解可以继续叠加 `@Compare / @SubqueryPredicate / @Selectable / @Existed`.

示例：

```java
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@SubqueryPredicate(
    mode = SubqueryMode.EXISTS,
    targetEntity = AuditEntity.class,
    left = @Expr(path = "id"),
    right = @Expr(path = "userId"),
    select = @Expr(path = "id")
)
public @interface ExistsAudit {
}
```

## 快速示例

```java
public class UserQuery {

    @Equal
    private String status = "ACTIVE";

    @Gt(left = @Expr(path = "audit.updatedAt"), right = @Expr(path = "audit.createdAt"))
    private String changedAt = "ignored";

    @Like(left = @Expr(path = "userName"))
    private String keyword = "demo";

    @Between(left = @Expr(path = "createdAt"))
    private java.util.List<String> createdAt = java.util.Arrays.asList("2024-01-01", "2024-12-31");

    @Exists(
        targetEntity = AuditEntity.class,
        left = @Expr(path = "id"),
        right = @Expr(path = "userId"),
        select = @Expr(path = "id"),
        where = @SubqueryGroup(compare = {
            @Compare(left = @Expr(path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId"))
        })
    )
    private Boolean existsAudit = Boolean.TRUE;

    private Integer tenantId = 7;
}
```

更复杂的比较可直接使用 `@Compare`：

```java
@Compare(
    op = CompareOp.EQ,
    left = @Expr(path = "userName"),
    right = @Expr(
        type = ExprType.FUNCTION,
        function = @ExprFunction(
            name = "lower",
            type = String.class,
            args = { @ExprArg(type = ExprType.VALUE, valueField = "keyword") }
        )
    ),
    options = @PredicateOptions(trim = true)
)
private String keyword = "Demo";
```

## 能力验证基线

下面这些能力已经有仓库内直接测试证据覆盖：

- 稳定公开调用链 `CollectionCache -> toComputerCollection -> build(BuilderType.SELECTED)` 可用
- getter-only query model 可用
- `Selectable / Existed` 可通过稳定入口运行
- `IgnoreCaseEqual / IgnoreCaseLike` 可通过稳定入口运行
- SPI compiled provider 与 `@Constraint` provider 可通过稳定入口运行
- 快捷注解 direct meta 直达 canonical 根注解
- `Cases` 家族保持 canonical-first 语义
- `CaseElse` 兜底分支可通过 `otherwise.enabled = true` 启用并执行
- `CaseWhen.group` 结构化 AND/OR 命中树可运行
- `CaseThen.group` 结构化多动作输出可运行
- `InTable` 保持 canonical 子查询语义
- 零配置 `@Equal` 编译为 `PATH(currentField) = VALUE(currentFieldValue)`
- `InTable / Exists / NotExists / SubqueryPredicate` 统一编译为子查询规格
- 子查询 where tree 可以继承和覆盖 `PredicateOptions`
- `Cases` 的 canonical `when/then` 主语义可运行
- tuple-values 与 tuple-subquery 两条新能力均已接入 compiled 主链路

当前模块级回归命令：

```bash
mvn -pl data-jpa-base-generator-annotations -am test
mvn -pl data-jpa-base-generator-interpreter -am test
mvn -pl data-jpa-base-generator-annotations,data-jpa-base-generator-interpreter -am test
mvn test
```

## 构建与开发

仓库根目录常用命令：

```bash
mvn test
mvn package
mvn -pl data-jpa-base-generator-interpreter test
mvn spotless:apply
mvn checkstyle:check
```

工程使用 Java 11 开发, 主源码编译目标兼容 Java 8.

## 当前结论

当前工程已经不是“旧注解 + 多套运行时解释器”模型, 而是：

1. canonical 基础注解承载统一 DSL 能力中心
2. 快捷注解保留零配置和见名知意的使用体验
3. interpreter 统一编译到 compiled spec, 再交给 provider 执行
4. tuple-values 与 tuple-subquery 已经进入主链路, 且分别采用最稳定的运行时落地方式
