# 基于SpringBoot+MyBatisPlus实现的外卖平台开发文档



## 一、框架搭建

### 1、基础环境配置

#### 1.1 插件依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3</version>
</dependency>
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus</artifactId>
    <version>3.5.3</version>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>

<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.76</version>
</dependency>

<dependency>
    <groupId>commons-lang</groupId>
    <artifactId>commons-lang</artifactId>
    <version>2.6</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.23</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```



#### 1.2 静态文件

静态文件存放位置 src/main/resource



### 2、数据库及表结构

#### 2.1 创建数据库

**CREATE** **DATABASE** reggie_takeout /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;



#### 2.2 创建表结构

##### 2.2.1 Employee 员工表

-- reggie_takeout.employee definition

**CREATE** **TABLE** `employee` (

  `id` **bigint** **NOT** **NULL** COMMENT '主键',

  `name` **varchar**(32) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '姓名',

  `username` **varchar**(32) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '用户名',

  `password` **varchar**(64) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '密码',

  `phone` **varchar**(11) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '手机号',

  `sex` **varchar**(2) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '性别',

  `id_number` **varchar**(18) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '身份证号',

  `status` **int** **NOT** **NULL** **DEFAULT** '1' COMMENT '状态 0:禁用，1:正常',

  `create_time` **datetime** **NOT** **NULL** COMMENT '创建时间',

  `update_time` **datetime** **NOT** **NULL** COMMENT '更新时间',

  `create_user` **bigint** **NOT** **NULL** COMMENT '创建人',

  `update_user` **bigint** **NOT** **NULL** COMMENT '修改人',

  **PRIMARY** **KEY** (`id`) **USING** BTREE,

  **UNIQUE** **KEY** `idx_username` (`username`)

) **ENGINE**=InnoDB **DEFAULT** CHARSET=utf8 **COLLATE**=utf8_bin COMMENT='员工信息';



##### 2.2.2 dish 菜品表



**CREATE** **TABLE** `dish` (

  `id` **bigint** **NOT** **NULL** COMMENT '主键',

  `name` **varchar**(64) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '菜品名称',

  `category_id` **bigint** **NOT** **NULL** COMMENT '菜品分类id',

  `price` **decimal**(10,2) **DEFAULT** **NULL** COMMENT '菜品价格',

  `code` **varchar**(64) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '商品码',

  `image` **varchar**(200) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '图片',

  `description` **varchar**(400) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **DEFAULT** **NULL** COMMENT '描述信息',

  `status` **int** **NOT** **NULL** **DEFAULT** '1' COMMENT '0 停售 1 起售',

  `sort` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '顺序',

  `create_time` **datetime** **NOT** **NULL** COMMENT '创建时间',

  `update_time` **datetime** **NOT** **NULL** COMMENT '更新时间',

  `create_user` **bigint** **NOT** **NULL** COMMENT '创建人',

  `update_user` **bigint** **NOT** **NULL** COMMENT '修改人',

  `is_deleted` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '是否删除',

  **PRIMARY** **KEY** (`id`) **USING** BTREE,

  **UNIQUE** **KEY** `idx_dish_name` (`name`)

) **ENGINE**=InnoDB **DEFAULT** CHARSET=utf8 **COLLATE**=utf8_bin COMMENT='菜品管理';



##### 2.2.3 Category 菜品分类表



**CREATE** **TABLE** `category` (

  `id` **bigint** **NOT** **NULL** COMMENT '主键',

  `type` **int** **DEFAULT** **NULL** COMMENT '类型   1 菜品分类 2 套餐分类',

  `name` **varchar**(64) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '分类名称',

  `sort` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '顺序',

  `create_time` **datetime** **NOT** **NULL** COMMENT '创建时间',

  `update_time` **datetime** **NOT** **NULL** COMMENT '更新时间',

  `create_user` **bigint** **NOT** **NULL** COMMENT '创建人',

  `update_user` **bigint** **NOT** **NULL** COMMENT '修改人',

  `is_deleted` **int** **NOT** **NULL** **DEFAULT** '1' COMMENT '是否删除',

  **PRIMARY** **KEY** (`id`) **USING** BTREE,

  **UNIQUE** **KEY** `idx_category_name` (`name`)

) **ENGINE**=InnoDB **DEFAULT** CHARSET=utf8 **COLLATE**=utf8_bin COMMENT='菜品及套餐分类';



##### 2.2.4 Setmeal 套餐表

-- reggie_takeout.setmeal definition



**CREATE** **TABLE** `setmeal` (

  `id` **bigint** **NOT** **NULL** COMMENT '主键',

  `category_id` **bigint** **NOT** **NULL** COMMENT '菜品分类id',

  `name` **varchar**(64) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '套餐名称',

  `price` **decimal**(10,2) **NOT** **NULL** COMMENT '套餐价格',

  `status` **int** **DEFAULT** **NULL** COMMENT '状态 0:停用 1:启用',

  `code` **varchar**(32) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **DEFAULT** **NULL** COMMENT '编码',

  `description` **varchar**(512) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **DEFAULT** **NULL** COMMENT '描述信息',

  `image` **varchar**(255) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **DEFAULT** **NULL** COMMENT '图片',

  `create_time` **datetime** **NOT** **NULL** COMMENT '创建时间',

  `update_time` **datetime** **NOT** **NULL** COMMENT '更新时间',

  `create_user` **bigint** **NOT** **NULL** COMMENT '创建人',

  `update_user` **bigint** **NOT** **NULL** COMMENT '修改人',

  `is_deleted` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '是否删除',

  **PRIMARY** **KEY** (`id`) **USING** BTREE,

  **UNIQUE** **KEY** `idx_setmeal_name` (`name`)

) **ENGINE**=InnoDB **DEFAULT** CHARSET=utf8 **COLLATE**=utf8_bin COMMENT='套餐';



##### 2.2.5 Dish_flavor 菜品和口味关联表

-- reggie_takeout.dish_flavor definition



**CREATE** **TABLE** `dish_flavor` (

  `id` **bigint** **NOT** **NULL** COMMENT '主键',

  `dish_id` **bigint** **NOT** **NULL** COMMENT '菜品',

  `name` **varchar**(64) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '口味名称',

  `value` **varchar**(500) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **DEFAULT** **NULL** COMMENT '口味数据list',

  `create_time` **datetime** **NOT** **NULL** COMMENT '创建时间',

  `update_time` **datetime** **NOT** **NULL** COMMENT '更新时间',

  `create_user` **bigint** **NOT** **NULL** COMMENT '创建人',

  `update_user` **bigint** **NOT** **NULL** COMMENT '修改人',

  `is_deleted` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '是否删除',

  **PRIMARY** **KEY** (`id`) **USING** BTREE

) **ENGINE**=InnoDB **DEFAULT** CHARSET=utf8 **COLLATE**=utf8_bin COMMENT='菜品口味关系表';



##### 2.2.6 Setmeal_dish套餐和菜品关联表

-- reggie_takeout.setmeal_dish definition



**CREATE** **TABLE** `setmeal_dish` (

  `id` **bigint** **NOT** **NULL** COMMENT '主键',

  `setmeal_id` **varchar**(32) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '套餐id ',

  `dish_id` **varchar**(32) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **NOT** **NULL** COMMENT '菜品id',

  `name` **varchar**(32) **CHARACTER** **SET** utf8 **COLLATE** utf8_bin **DEFAULT** **NULL** COMMENT '菜品名称 （冗余字段）',

  `price` **decimal**(10,2) **DEFAULT** **NULL** COMMENT '菜品原价（冗余字段）',

  `copies` **int** **NOT** **NULL** COMMENT '份数',

  `sort` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '排序',

  `create_time` **datetime** **NOT** **NULL** COMMENT '创建时间',

  `update_time` **datetime** **NOT** **NULL** COMMENT '更新时间',

  `create_user` **bigint** **NOT** **NULL** COMMENT '创建人',

  `update_user` **bigint** **NOT** **NULL** COMMENT '修改人',

  `is_deleted` **int** **NOT** **NULL** **DEFAULT** '0' COMMENT '是否删除',

  **PRIMARY** **KEY** (`id`) **USING** BTREE

) **ENGINE**=InnoDB **DEFAULT** CHARSET=utf8 **COLLATE**=utf8_bin COMMENT='套餐菜品关系';



### 3、配置文件

配置文件application.yml

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://localhost:3306/reggie_takeout?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
    username: root
    password: 12345678

mybatis-plus:
  configuration:
    #映射实体类时，将表名和字段名中的下划线去掉，按照驼峰命名法映射
    map-underscore-to-camel-case: true 
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
```



### 4、服务启动测试

此时不需要过多的配置就可以启动

​	curl http://localhost:8080


## 二、服务接口开发

### 1、页面展示

此时访问页面还是404，由于springboot 默认的静态页面放在resource/static， 而这里是放在resource下，所以需要做下静态页面映射。

#### 1.1 定义配置类config/WebMvcConfig

```java
/**
 * @author
 * @date 2023/8/8
 */
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
```

#### 1.2 app增加注入扫描@ServletComponentScan

```java
@Slf4j
@SpringBootApplication
@ServletComponentScan
public class Reggietakeout2Application {

    public static void main(String[] args) {
        SpringApplication.run(Reggietakeout2Application.class, args);
        log.info("服务启动成功");
    }

}
```



#### 1.3 浏览器访问

http://localhost:8080/backend/index.html
效果：



### 2、员工管理开发

#### 2.1 登录接口开发

##### 2.1.1 需求分析

从前端接口可以看到登录是post /employee/login 接口

所以需要创建以下内容

##### 2.1.2 代码开发

2.1.2.1 实体类entity/Employee

```java
@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    private String createTime;

    private String updateTime;

    private Long createUser;

    private Long updateUser;
}
```

2.1.2.2 Mapper类 mapper/EmployeeMapper

```java
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
/* 继承BaseMapper获取常见的数据库操作（增删改查）
*/
}
```

2.1.2.3 接口service/EmployeeService

```java
public interface EmployeeService extends IService<Employee> {
	/*继承IService获取dao层交互操作（修改、保存等业务逻辑的抽象）
	*/
}
```

2.1.2.4 接口实现类service/impl/EmployeeServiceImpl

```java
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
/*继承ServiceImpl实现更高级的查询，如分页等
*/
}
```

2.1.2.5 返回值类common/Result

```java
@Data
public class Result<T> {
    private Integer code; //1 成功， 0 失败
    private String msg;
    private T data;
    private Map map = new HashMap();

    public static <T> Result<T> success(T object){
        Result<T> tResult = new Result<>();
        tResult.data = object;
        tResult.code = 1;
        return tResult;
    }

    public static <T> Result<T> error(String msg){
        Result<T> tResult = new Result<>();
        tResult.msg = msg;
        tResult.code = 0;
        return tResult;
    }
    
  	public Result<T> add(String key, Object value){
        this.map.put(key, value);
        return this;
    }
}
```

2.1.2.6 控制器controller/EmployeeController

```java
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee employee){
        String password = employee.getPassword();
        log.info("username: {}, password: {}", employee.getUsername(), employee.getPassword());
        if (employee.getUsername().equals("admin") && employee.getPassword().equals("123456")){
            return Result.success(employee);
        }
        return Result.error("登录失败");

    }
}
```

这里是简单的测试登录效果，账户：admin 密码:123456， 暂不涉及查数据库。

2.1.3 登录测试


2.2 登录接口优化

2.2.1 从数据库获取账户密码

员工表中插入数据

**INSERT** **INTO** reggie_takeout.employee

(id, name, username, **password**, phone, sex, id_number, status, create_time, update_time, create_user, update_user)

**VALUES**(12, 'test', 'test', md5('123456'), '13812312312', '1', '110101199001010047', 1, '2023-07-27 22:40:01', '2023-07-27 22:40:01', 0, 0);

登录控制器优化

```java
    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     * 判断条件包括：查询数据是否为空、密码是否正确、账户是否禁用
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("username: {}, password: {}", employee.getUsername(), employee.getPassword());

        // 构建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null){
            return Result.error("登录失败");
        }

        if (!emp.getPassword().equals(password)){
            return Result.error("密码错误");
        }
        if (emp.getStatus().equals(0)){
            return Result.error("账户已禁用");
        }
        return Result.success(emp);
    }
```

接口验证




2.2.2、未登录时页面拦截

虽然登录成功，但此时不需要登录也是可以访问其他页面。下一步需要对需要登录访问的页面进行拦截。

验证是否登录需要使用session，即登录后将登录信息保存到session中，访问页面时从浏览器获取。如果获取失败或超时，就跳转到登录页面。

获取用户ID

```java
/**
 * 基于ThreadLocal 封装工具类，用于保存和获取当前登录用户ID
 * 作用范围是某一个线程内
 * @author
 * @date 2023/8/8
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
```

定义拦截器

```java
/**
 * @author
 * @date 2023/8/9
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取本次请求的URL
        String requestURI = request.getRequestURI();

        // 定义不需要拦截的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        // 判断是否需要处理
        boolean check = check(urls, requestURI);
        // 如果不需要处理，则放行
        if (check){
            log.info("本次请求不需要处理, 路径:{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // 如果是已登录，则放行
        if (request.getSession().getAttribute("employee") !=null){
            log.info("用户已登录, id: {}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }
      	
        // 如果未登录，则返回到登录页面
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    /**
     * 检查路径
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String[] urls, String requestURL){
        for (String url: urls){
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }
}
```

登录控制器增加 将 ID 写入到session

```java
request.getSession().setAttribute("employee", emp.getId());
```

页面验证

浏览器输入 xxx/backend/index.html 会发现跳转到登录页面



2.3、登出接口开发

2.3.1 需求分析

从前端可以获取登出时 post employee/logout 接口

前面提到验证用户是否登录依据session中是否包含employee字段，所以登出只需要清理session即可。

```java
/**
 * 退出接口
 * @param request
 * @return
 */
@PostMapping("/logout")
public Result<String> logout(HttpServletRequest request){
    request.getSession().removeAttribute("employee");
    return Result.success("退出成功");
    
}
```



2.4、员工管理接口开发

2.4.1 添加员工

2.4.1.1 需求分析

点击保存按钮时，会触发 post /employee接口，提交的数据内容

{
  "name": "test001",
  "phone": "13911111111",
  "sex": "1",
  "idNumber": "111111111111111111",
  "username": "test001"
}

2.4.1.2 代码实现

注意，接口请求字段中不包含 createTime, updateTime, createUser, updateUser, 所以需要补齐

```java
/**
 * 添加员工
 * @param request
 * @param employee
 * @return
 */
@PostMapping
public Result<String> save(HttpServletRequest request, @RequestBody Employee employee){
    // 获取管理员工ID
    Long empId = (Long) request.getSession().getAttribute("employee");
    // 设置初始密码
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
    employee.setCreateTime(LocalDateTime.now());
    employee.setUpdateTime(LocalDateTime.now());
    employee.setCreateUser(empId);
    employee.setUpdateUser(empId);
    employeeService.save(employee);
    return Result.success("添加员工成功");
}
```



2.4.1.3 代码优化

注意到  createTime, updateTime, createUser, updateUser 在其他表中也存在，所以最好让这些字段自己补充，mybatis-plus提供这样的功能-共用字段自行填充。

员工实体类修改

```java
// 公共字段自动填充
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;

@TableField(fill = FieldFill.INSERT)
private Long createUser;

@TableField(fill = FieldFill.INSERT_UPDATE)
private Long updateUser;
```

公共字段处理方法

定义common/MyMetaObjectHandler

```java
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        log.info("共用字段自动填充");
    }

    /**
     * 更改操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        long pid = Thread.currentThread().getId();
        log.info("线程ID: {}", pid);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
```

添加员工save接口优化

删除以下四行

```java
employee.setCreateTime(LocalDateTime.now());
employee.setUpdateTime(LocalDateTime.now());
employee.setCreateUser(empId);
employee.setUpdateUser(empId);
```

功能验证


优化错误

测试时会发现对于相同用户名只能添加一次，后台报错，但前台只报出500，不利于排查问题，所以当用户重复操作时需要抛出异常。


定义全局错误包括SQL异常，自定义业务异常

common/CustomException

```java
/**
 * 自定义业务异常
 * @author
 * @date 2023/8/9
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
```

common/GlobalExceptionHandler

```java
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * SQL相关的异常错误
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if (ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            String msg = s[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error(ex.getMessage());
    }

    /**
     * 自定义业务异常
     * 数据表关联时使用
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex){
        return Result.error(ex.getMessage());

    }
}
```

异常验证




2.4.2 员工页面展示

2.4.2.1 需求分析

从前端页面查看接口 GET /employee/page 获取员工列表信息，这里涉及到分页，可以使用mybatisplus page接口功能。

2.4.2.2 代码实现

```java
/**
 * 分页查询
 * @param page
 * @param pageSize
 * @param name
 * @return
 */
@GetMapping("/page")
public Result<Page> page(int page, int pageSize, String name){
    // 分页构造器
    Page pageInfo = new Page(page, pageSize);
    // 查询构造器
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
    queryWrapper.orderByDesc(Employee::getUpdateTime);
    // 执行查询
    employeeService.page(pageInfo, queryWrapper);
    return Result.success(pageInfo);
    
}
```

2.4.2.3 功能验证

此时刷新页面，会看到员工信息


2.4.3 修改员工信息

2.4.3.1 需求分析

点击编辑按钮，页面请求 get /employee/idxxxx， 此时需要获取某个ID的员工信息。

点击保存按钮，页面请求 put /employee, 此时保存更新的员工信息。

点击禁用、启用 按钮，页面请求 put /employee, 保存员工状态信息。

所以这里需要2个接口，用来展示员工信息的getById 接口， 用来修改员工信息的update接口。



2.4.3.2 代码实现

获取员工信息接口

```java
/**
 * 通过ID获取员工信息
 * @param id
 * @return
 */
@GetMapping("/{id}")
public Result<Employee> getById(@PathVariable Long id){
    log.info("id: {}", id);
    Employee emp = employeeService.getById(id);
    log.info(emp.toString());
    if (emp != null){
        return Result.success(emp);
    }
    return Result.error("员工信息为空");

}
```

注意：

这里会有一个问题，后端传给前端的id过长时，JS的精度会出现丢失


实际后端返回的ID


解决方法

使用MVC消息转换器

定义common/JacksonObjectMapper

```java
/**
 * 对象映射器:基于jackson将Java对象转为json，或者将json转为Java对象
 * 将JSON解析为Java对象的过程称为 [从JSON反序列化Java对象]
 * 从Java对象生成JSON的过程称为 [序列化Java对象到JSON]
 */
public class JacksonObjectMapper extends ObjectMapper {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public JacksonObjectMapper() {
        super();
        //收到未知属性时不报异常
        this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        //反序列化时，属性不存在的兼容处理
        this.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        SimpleModule simpleModule = new SimpleModule()
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)))

                .addSerializer(BigInteger.class, ToStringSerializer.instance)
                .addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)))
                .addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)))
                .addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        //注册功能模块 例如，可以添加自定义序列化器和反序列化器
        this.registerModule(simpleModule);
    }
}
```

配置使用消息转换器

config/WebMvcConfig

```java
/**
 * 扩展MVC的消息转换器
 * @param converters
 */
@Override
protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    log.info("扩展消息转换器...");
    // 创建消息转换器
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    // 设置对象映射器
    messageConverter.setObjectMapper(new JacksonObjectMapper());
    // 通过索引让新增的转换器放在前面
    //super.extendMessageConverters(converters);
    converters.add(0, messageConverter);
}
```



修改员工信息

```java
/**
 * 修改员工信息
 * 注意id过长，js会丢失精度
 * 处理: 后端将数据转换成字符串
 * 这里使用MVC的消息转换器jackson
 * @param employee
 * @return
 */
@PutMapping
public Result<String> update(@RequestBody Employee employee){

    employeeService.updateById(employee);
    return Result.success("修改成功");
}
```



2.4.3.3 功能验证

启用禁用员工

修改员工信息




### 3、分类管理接口开发

3.1 新增菜品分类、套餐分类 接口

3.1.1 需求分析

数据结构中菜品分类、套餐分类属于同一张表 category，只是type不同（1 菜品分类，2 套餐分类）

前端调用返回获知，点击新增菜品分类、新增套餐分类时，都是 post /category， 添加字段分类名称和排序字段。

页面展示， get /category/page 同时可以获取到菜品分类和套餐分类信息。

点击保存按钮，更新菜品分类或套餐分类信息，put category。

点击删除按钮，删除菜品分类或套餐分类信息，这里需要注意，只能删除空的菜品信息或套餐信息，如果有关联到具体的菜品信息，则不能删除， Delete category/ids=xxxx



3.1.2 代码实现

新增菜品分类实体类entriy/Category

```java
/**
 * @Author
 * @Date
 * @Description 分类管理
 */
@Data
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //类型 1 菜品分类 2 套餐分类
    private Integer type;

    //分类名称
    private String name;

    //顺序
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除
    private Integer isDeleted;

}
```

定义菜品分类Mapper

mapper/CategoryMapper

```java
/**
 * @author
 * @date 2023/8/9
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
```

定义菜品分类接口

service/CategoryService

```java
/**
 * @author
 * @date 2023/8/9
 */
public interface CategoryService extends IService<Category> {
}
```

定义菜品分类接口实现类

service/impl/CategoryServiceImpl

```java
/**
 * @author
 * @date 2023/8/9
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
```

定义菜品分来控制器

controller/CategoryController

```java
/**
 * @author
 * @date 2023/8/9
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("新增菜品分类: {}", category);
        categoryService.save(category);
        return Result.success("新增菜品分类成功");
    }
}
```



3.1.3 新增接口验证


3.1.4 菜品分类、套餐分类页面展示接口

controller/CategoryController

```java
/**
 * 分页查询菜品分类、套餐分类
 * @param page
 * @param pageSize
 * @param name
 * @return
 */
@GetMapping("/page")
public Result<Page> page(int page, int pageSize, String name){
    // 分页构造器
    Page<Category> pageInfo = new Page<>(page, pageSize);

    // 查询构造器
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.orderByDesc(Category::getUpdateTime);
    // 查询
    categoryService.page(pageInfo, queryWrapper);
    return Result.success(pageInfo);

}
```

3.1.5 接口验证




3.1.6 修改菜品分类和套餐分类信息

```java
/**
 * 修改分类信息
 * @param category
 * @return
 */
@PutMapping
public Result<String> update(@RequestBody Category category){
    log.info("修改分类信息: {}", category);
    categoryService.updateById(category);
    return Result.success("修改分类信息成功");
}
```



3.1.7 接口验证




3.1.8 删除分类接口

```java
/**
 * 根据ID删除分类信息
 * @param ids
 * @return
 */
@DeleteMapping
public Result<String> delete(Long ids){
    log.info("要删除的分类ID: {}", ids);
    categoryService.removeById(ids);
    return Result.success("删除分类信息成功");
}
```

3.1.9 删除分类信息接口优化

上面的删除仅仅实现了删除的功能，但当菜品或者套餐有依赖时，不能删除，并且提示有依赖。

a. 重定义删除接口方法

service/CategoryService

```java
public void remove(Long id);
```

b. 定义删除接口实现类

根据菜品或套餐ID查询是否包含依赖，这里需要查询菜品表 dish， 套餐表 setmeal。

定义菜品和套餐实现类

entity/Dish

```java
/**
 * @Author
 * @Date
 * @Description 菜品
 */
@Data
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //菜品名称
    private String name;

    //菜品分类id
    private Long categoryId;

    //菜品价格
    private BigDecimal price;

    //商品码
    private String code;

    //图片
    private String image;

    //描述信息
    private String description;

    //0 停售 1 起售
    private Integer status;

    //顺序
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除
    private Integer isDeleted;


}
```

entity/Setmeal

```java
/**
 * 套餐
 */
@Data
public class Setmeal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //分类id
    private Long categoryId;

    //套餐名称
    private String name;

    //套餐价格
    private BigDecimal price;

    //状态 0:停用 1:启用
    private Integer status;

    //编码
    private String code;

    //描述信息
    private String description;

    //图片
    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除
    private Integer isDeleted;
}
```



c. 定义菜品和套餐mapper

mapper/DishMapper

```java
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
```

mapper/SetmealMapper

```java
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
}
```



d. 定义菜品和套餐service接口

service/DishService

```java
public interface DishService extends IService<Dish> {
}
```

service/SetmealService

```java
public interface SetmealService extends IService<Setmeal> {
}
```



e.定义菜品和套餐service 接口实现类

service/impl/DishServiceImpl

```java
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
```

service/impl/SetmealServiceImpl

```java
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
```



f.定义remove 接口实现类

```java
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类ID 验证菜品分类或套餐分类是否包含依赖，如果包含则提示不能删除，如果不包含可直接删除。
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 菜品分类查询构造器
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        // 查询菜品分类是否关联菜品
        long dishCount = dishService.count(dishQueryWrapper);
        if (dishCount>0){
            throw new CustomException("该菜品分类关联菜品，不能删除");
        }

        // 套餐分类查询构造器
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        long setCount = setmealService.count(setmealQueryWrapper);
        if (setCount>0){
            throw new CustomException("该套餐分类关联菜品，不能删除");
        }
        // 无关联，则直接删除
        this.removeById(id);
    }
}
```

g.修改删除控制类方法

controller/CategoryController

```java
    /**
     * 根据ID删除分类信息， 并判断分类是否包含菜品，如果包含则提示不能删除，反之直接删除。
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("要删除的分类ID: {}", ids);
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return Result.success("删除分类信息成功");
    }
```



3.1.10 删除接口优化验证




### 4、菜品管理接口开发

#### 4.1 新增菜品

##### 4.1.1 新增菜品需求分析

新增菜品 get category/list 获取菜品分类列表

新增菜品 上传和下载菜品图片 common/upload, common/download

新增菜品 保存菜品信息、菜品和口味关联信息、菜品和菜品分类关联信息，涉及表dish，dish_flavor。

##### 4.1.2 新增菜品代码实现

定义口味实现类

entity/DishFlavor

```java
/**
 * 菜品口味信息
 * @author
 * @date 2023/8/9
 */
@Data
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //菜品id
    private Long dishId;

    //口味名称
    private String name;

    //口味数据list
    private String value;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    //是否删除
    private Integer isDeleted;
}
```



定义口味Mapper

mapper/DishFlavorMapper

```java
/**
 * @author
 * @date 2023/8/9
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
```



定义口味接口

service/DishFlavorService

```java
/**
 * @author
 * @date 2023/8/9
 */
public interface DishFlavorService extends IService<DishFlavor> {
}
```



定义口味接口实现类

service/impl/DishFlavorServiceImpl

```java
/**
 * @author
 * @date 2023/8/9
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
```



在上一节删除分类信息是定义了菜品的实现类、接口等信息，这里直接修改。

获取菜品分类信息

controller/CategoryController

```java
@GetMapping("/list")
public Result<List<Category>> list(Category category){
    // 条件构造器
    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
    // 添加过滤条件u
    queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
    // 使用排序字段和更新时间排序
    queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
    // 查询
    List<Category> list = categoryService.list(queryWrapper);
    return Result.success(list);
}
```

上传、下载图片接口

```java
/**
 * 上传下载，公共控制器
 * @author
 * @date 2023/8/9
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    // 定义上传路径
    @Value("${basePath.uploadPath}")
    private String uploadPath;

    /**
     * 上传文件到指定目录，并以UUID重命名文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        // file 为临时文件，需要转存到其他目录
        log.info("upload file: {}", file.toString());

        // 获取原始文件名
        String filename = file.getOriginalFilename();
        String subStr = ".jpg";

        if (filename != null){
            subStr = filename.substring(filename.lastIndexOf("."));
        }
        // 使用UUID 重新生成文件名，避免重复文件覆盖
        String newFilename = UUID.randomUUID().toString()+ subStr;

        File dir = new File(uploadPath + newFilename);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(uploadPath + newFilename));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Result.success(newFilename);

    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];

        try {
            // 输入流， 读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(uploadPath + name));
            // 输出流，写回浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();

            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
```



菜品分类接口验证



菜品图片接口验证


添加菜品接口

controller/DishController

```java
/**
 * @author
 * @date 2023/8/9
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result<String> save(@RequestBody Dish dish){
        log.info("Dish : {}", dish);
//        dishService.save(dish);
        return null;
    }
}
```

从日志中可以看到提交的数据缺失口味信息：

Dish(id=null, name=小炒西兰花, categoryId=1397844303408574465, price=2000, code=, image=5dc417d5-3711-47a1-ad90-c7b7522491c2.jpg, description=无, status=1, sort=null, createTime=null, updateTime=null, createUser=null, updateUser=null, isDeleted=null)

所以使用Dish 实例无法满足该接口，需要在Dish 的基础上扩展字段，定义DishDto实例

dto/DishDto

```java
/**
 * @author
 * @date 2023/8/9
 */
@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
```



优化保存菜品接口

```java
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        log.info("DishDto : {}", dishDto);
//        dishService.save(dish);
        return null;
    }
```

DishDto(flavors=[DishFlavor(id=null, dishId=null, name=辣度, value=["不辣","微辣","中辣","重辣"], createTime=null, updateTime=null, createUser=null, updateUser=null, isDeleted=null)], categoryName=null, copies=null)

定义操作 dish 和 dish_flavor 两张表的方法

service/DishService

```java
// 扩展方法，同时插入 菜品 和口味信息
public void saveWithFlavor(DishDto dishDto);
```

service/DishServiceImpl

```java
/**
 * @author
 * @date 2023/8/9
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 添加菜品信息，同时修改 dish , dish_flavor 表，注意添加事务
     * @param dishDto
     */
    @Override
  	@Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品信息
        this.save(dishDto);

        // 获取菜品ID
        Long dishId = dishDto.getId();
        // 通过菜品id 获取菜品口味, 拼接 菜品口味 和菜品ID 的关联关系
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item->{
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());

        log.info("菜品口味和菜品ID关联信息: {}", flavors);
        // 保存口味信息到 dish_flavor
        dishFlavorService.saveBatch(flavors);
        
    }
}
```



开启事务

controller/WebMvcConfig

```java
@EnableTransactionManagement
```

优化菜品保存接口

菜品保存接口验证

日志


dish 表

dish_flavor 表



#### 4.2 菜品展示

##### 4.2.1 需求分析

前端接口请求 get dish/page, 注意这里还需要展示菜品分类，可以通过dish表中的category_id 查询category表获取。

##### 4.2.2 代码实现

```java
/**
 * 分页查询菜品信息
 * @param page
 * @param pageSize
 * @param name
 * @return
 */
@GetMapping("/page")
public Result<Page> page(int page, int pageSize, String name){
    // 构建分页构造器
    Page<Dish> dishPageInfo = new Page<>(page, pageSize);
    
    // 构建查询构造
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name != null, Dish::getName, name);
    queryWrapper.orderByDesc(Dish::getUpdateTime);
    // 执行分页查询
    dishService.page(dishPageInfo, queryWrapper);

    return Result.success(dishPageInfo);
}
```



效果



菜品展示优化

从上图可以看到菜品分类信息目前为空，所以需要对菜品分页查询接口进行优化，通过菜品表获取菜品分类信息

从前端接口可以获知前端的菜品分类信息是从res.data.records字段获得，所以需要定义一个对象即包含菜品信息，又包含菜品分类信息。

```java
/**
 * 分页查询菜品信息
 * @param page
 * @param pageSize
 * @param name
 * @return
 */
@GetMapping("/page")
public Result<Page> page(int page, int pageSize, String name){
    // 构建分页构造器
    Page<Dish> dishPageInfo = new Page<>(page, pageSize);
    Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

    // 构建查询构造器
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name != null, Dish::getName, name);
    queryWrapper.orderByDesc(Dish::getUpdateTime);
    // 执行分页查询
    dishService.page(dishPageInfo, queryWrapper);

    // 重新构造返回字段
    BeanUtils.copyProperties(dishPageInfo, dishDtoPage, "records");

    List<Dish> records = dishPageInfo.getRecords();
    List<DishDto> list = records.stream().map((item -> {
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(item, dishDto);
        // 获取分类ID
        Long categoryId = item.getCategoryId();
        // 根据分类ID，获取分类名称
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }
        return dishDto;
    })).collect(Collectors.toList());

    dishDtoPage.setRecords(list);

    log.info("重构字段: {}", dishDtoPage);
    
    return Result.success(dishDtoPage);
}
```



优化效果



#### 4.3 修改菜品接口

##### 4.4.1 需求分析

点击修改菜品，首先通过get 、dish/菜品ID 获取菜品信息，菜品分类信息，菜品口味信息。

点击保存按钮，更新菜品表，菜品和口味关联表。

##### 4.4.2 代码实现





8、套餐管理接口开发



9、订单明细接口开发