



# 基于SpringBoot+MyBatisPlus实现的外卖平台开发文档



## 前言说明

参考黑马程序员《瑞吉外卖》项目，学习SpringBoot后端开发的过程梳理，同时完善部分功能接口。

通过该项目学习以下内容：

​	SpringBoot 的搭建和使用，包括前端过滤、用户登录session保存和校验、如何从URL或请求体中或者前端回传的参数等。

​	基于MyBatisPlus框架的数据层抽象操作，包括通过Page构造分页、通过LambdaQueryWrapper 构造各种条件查询，扩展字段、关联字段的使用，以及提交的事务等。



功能包括：

​	登录登出功能；

​	员工管理、分类管理、菜品管理、套餐管理、订单明细页面的增删改查。

完善的功能：

​	菜品管理的停售、启售和关联删除；

​	套餐管理的停售、启售和关联删除；

​	订单基于时间段的查询；





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

效果图略

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



#### 1.2 app增加注入扫描

#### @ServletComponentScan

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

http://localhost:8080/backend/index.html效果：

效果图略


### 2、员工管理



#### 2.1 登录接口开发



##### 2.1.1 需求分析

​	前端接口可以看到登录是post /employee/login 接口



##### 2.1.2 代码开发

###### 2.1.2.1 员工实体类

entity/Employee

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



###### 2.1.2.2 Mapper类

mapper/EmployeeMapper

```java
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
/* 继承BaseMapper获取常见的数据库操作（增删改查）
*/
}
```



###### 2.1.2.3 员工接口和接口实现类

service/EmployeeService

```java
public interface EmployeeService extends IService<Employee> {
	/*继承IService获取dao层交互操作（修改、保存等业务逻辑的抽象）
	*/
}
```



service/impl/EmployeeServiceImpl

```java
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
/*继承ServiceImpl实现更高级的查询，如分页等
*/
}
```



###### 2.1.2.4 返回值类

common/Result

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



###### 2.1.2.5 登录控制器

controller/EmployeeController

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



##### 2.1.3 登录测试



#### 2.2 登录接口优化



##### 2.2.1 从数据库获取账户密码



###### 员工表中插入数据

**INSERT** **INTO** reggie_takeout.employee

(id, name, username, **password**, phone, sex, id_number, status, create_time, update_time, create_user, update_user)

**VALUES**(12, 'test', 'test', md5('123456'), '13812312312', '1', '110101199001010047', 1, '2023-07-27 22:40:01', '2023-07-27 22:40:01', 0, 0);



###### 登录控制器优化

controller/EmployeeController

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

###### 接口验证

效果图略


##### 2.2.2、未登录时页面拦截



虽然登录成功，但此时不需要登录也是可以访问其他页面。下一步需要对需要登录访问的页面进行拦截。

验证是否登录需要使用session，即登录后将登录信息保存到session中，访问页面时从浏览器获取。如果获取失败或超时，就跳转到登录页面。

###### 获取用户ID

common/BaseContext

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

###### 定义拦截器

filter/LoginCheckFilter

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



###### 登录控制器增加 将 ID 写入到session

controller/EmployeeController

```java
request.getSession().setAttribute("employee", emp.getId());
```



###### 页面验证

浏览器输入 xxx/backend/index.html 会发现跳转到登录页面



#### 2.3 登出接口开发

##### 2.3.1 需求分析

从前端可以获取登出时 post employee/logout 接口

前面提到验证用户是否登录依据session中是否包含employee字段，所以登出只需要清理session即可。



##### 2.3.2 代码实现

controller/EmployeeController

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



#### 2.4、员工管理

##### 2.4.1 添加员工

###### 2.4.1.1 需求分析

点击保存按钮时，会触发 post /employee接口，提交的数据内容

{
  "name": "test001",
  "phone": "13911111111",
  "sex": "1",
  "idNumber": "111111111111111111",
  "username": "test001"
}



###### 2.4.1.2 代码实现

注意，接口请求字段中不包含 createTime, updateTime, createUser, updateUser, 所以需要补齐

controller/EmployeeController

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



###### 2.4.1.3 代码优化

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

效果图略

优化错误

测试时会发现对于相同用户名只能添加一次，后台报错，但前台只报出500，不利于排查问题，所以当用户重复操作时需要抛出异常。

效果图略

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

效果图略


##### 2.4.2 员工页面展示



###### 2.4.2.1 需求分析

从前端页面查看接口 GET /employee/page 获取员工列表信息，这里涉及到分页，可以使用mybatisplus page接口功能。



###### 2.4.2.2 代码实现

分页查询控制器

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

配置分页配置类

config/MybatisPlusConfig

```java
/**
 * MP 分页插件
 * @author
 * @date 2023/8/9
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
```

###### 2.4.2.3 功能验证

此时刷新页面，会看到员工信息

效果图略


##### 2.4.3 修改员工信息

###### 2.4.3.1 需求分析

点击编辑按钮，页面请求 get /employee/idxxxx， 此时需要获取某个ID的员工信息。

点击保存按钮，页面请求 put /employee, 此时保存更新的员工信息。

点击禁用、启用 按钮，页面请求 put /employee, 保存员工状态信息。

所以这里需要2个接口，用来展示员工信息的getById 接口， 用来修改员工信息的update接口。



###### 2.4.3.2 代码实现

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

效果图略

实际后端返回的ID

效果图略

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



###### 2.4.3.3 功能验证

启用禁用员工

效果图略

修改员工信息

效果图略




### 3、分类管理

#### 3.1 新增分类接口

##### 3.1.1 需求分析

数据结构中菜品分类、套餐分类属于同一张表 category，只是type不同（1 菜品分类，2 套餐分类）

前端调用返回获知，点击新增菜品分类、新增套餐分类时，都是 post /category， 添加字段分类名称和排序字段。

页面展示， get /category/page 同时可以获取到菜品分类和套餐分类信息。

点击保存按钮，更新菜品分类或套餐分类信息，put category。

点击删除按钮，删除菜品分类或套餐分类信息，这里需要注意，只能删除空的菜品信息或套餐信息，如果有关联到具体的菜品信息，则不能删除， Delete category/ids=xxxx



##### 3.1.2 代码实现

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



##### 3.1.3 新增接口验证

效果图略

#### 3.2 分类页面展示接口

##### 3.2.1 需求分析

GET category/page 接口获取菜品分类和套餐分类信息



##### 3.2.2 代码实现

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

##### 3.2.3 接口验证

效果图略


#### 3.3 修改分类信息

##### 3.3.1 需求分析

点击修改按钮，获取分类信息（直接查category表）

点击保存按钮, POST category，保存修改信息。

##### 3.3.2 代码实现

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



##### 3.3.3 接口验证

效果图略


#### 3.4 删除分类接口

##### 3.4.1 需求分析

点击删除按钮，发起DELETE category请求，传递ids

删除时需要确认分类是否有依赖，如果存在菜品依赖则提示不能删除，否则可删

##### 3.4.2 代码实现

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

##### 3.4.3 删除分类接口优化

上面的删除仅仅实现了删除的功能，但当菜品或者套餐有依赖时，不能删除，并且提示有依赖。

###### a. 重定义删除接口方法

service/CategoryService

```java
public void remove(Long id);
```

###### b. 定义删除接口实现类

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



###### c. 定义菜品和套餐mapper

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



###### d. 定义菜品和套餐service接口

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



###### e.定义菜品和套餐service 接口实现类

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



###### f.定义remove 接口实现类

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

###### g.修改删除控制类方法

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



##### 3.4.4 删除接口优化验证

效果图略


### 4、菜品管理

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

效果图略


菜品图片接口验证

效果图略

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



##### 4.1.3 优化保存菜品接口

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
     * 添加菜品信息，同时修改 dish , dish_flavor 表，注意注入事务
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

ReggieTakeout2Application.java

```java
@EnableTransactionManagement
```



##### 4.1.4 菜品保存接口验证

日志

效果图略

dish 表

效果图略

dish_flavor 表

效果图略



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

效果图略


##### 4.2.3 菜品展示优化

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



##### 4.2.4 接口验证

效果图略


#### 4.3 修改菜品接口

##### 4.3.1 需求分析

点击修改菜品，首先通过get 、dish/菜品ID 获取菜品信息，菜品分类信息，菜品口味信息。

点击保存按钮，更新菜品表，菜品和口味关联表，PUT /dish。

点击停售、起售、批量停售、批量起售，发起Post  请求/dish/status/0?ids=xxx 修改菜品状态

##### 4.3.2 代码实现

定义通过菜品ID查询菜品分类信息和口味信息的接口

service/DishService 增加

```java
// 扩展方法，通过菜品ID，获取菜品和菜品口味信息
public DishDto getByIdWithFlavor(Long id);
```

service/impl/DishServiceImpl 增加

```java
/**
 * 通过菜品ID，获取菜品信息和菜品口味信息
 * @param id
 * @return
 */
@Override
public DishDto getByIdWithFlavor(Long id) {
    // 获取菜品信息
    log.info("dish id:{}", id);
    Dish dish = this.getById(id);
    log.info("dish:{}", dish);
    // 定义一个新的对象，用于构建包含口味信息的对象
    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish, dishDto);
    //通过菜品ID 获取菜品分类信息

    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
    List<DishFlavor> list = dishFlavorService.list(queryWrapper);
    dishDto.setFlavors(list);
    log.info("dish service impl dishdto: {}", dishDto);
    return dishDto;
}


```

controller/DishController 增加

```java
/**
 * 通过菜品ID 获取菜品信息和口味信息
 * @param id
 * @return
 */
@GetMapping("/{id}")
public Result<DishDto> getById(@PathVariable Long id){
    log.info("dish ID: {}", id);
    DishDto dishDto = dishService.getByIdWithFlavor(id);
    log.info("dishDto: ", dishDto);
    return Result.success(dishDto);
}
```



定义修改菜品信息和关联菜品接口

service/DishService 增加更新菜品及菜品口味方法

```java
public void updateWithFlavor(DishDto dishDto);
```

service/impl/DishServiceImpl 增加接口实现类

```java
/**
 * 更加菜品ID，更新菜品信息和口味信息，这里更新两张表，注意增加事务
 * @param dishDto
 */
@Override
@Transactional
public void updateWithFlavor(DishDto dishDto) {
    // 更新菜品信息
    this.updateById(dishDto);
    // 查询口味信息并删除
    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
    dishFlavorService.remove(queryWrapper);
    // 插入新的口味信息
    List<DishFlavor> flavors = dishDto.getFlavors();
    flavors.stream().map((item) -> {
        item.setDishId(dishDto.getId());
        return item;
    }).collect(Collectors.toList());
    log.info("新的口味信息: {}", flavors);
    dishFlavorService.saveBatch(flavors);
}
```



增加更新菜品控制器

controller/DishController

```java
/**
 * 修改菜品信息和口味信息
 * @param dishDto
 * @return
 */
@PutMapping
public Result<String> update(@RequestBody DishDto dishDto){
    log.info("获取更新的菜品信息: {}", dishDto);
    dishService.updateWithFlavor(dishDto);
    return Result.success("更新菜品及口味信息");
}
```



#### 4.4 启售、停售接口

##### 4.4.1 需求分析

包含批量操作和单条操作，POST status/0?ids=xxx

##### 4.4.2 代码实现

定义通过菜品ID单条、批量更新菜品状态接口

service/DishService 增加

```java
// 扩展方法，通过菜品ID， 更新菜品状态
public void updateStatus(List<Long> ids, Integer status);
```

service/impl/DishServiceImpl 增加接口实现类

    /**
     * 修改菜品状态，这里使用forEach方法，也可以 stream().map()
     * @param ids
     * @param status 获取的值即为要修改的值
     */
    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        if (ids.size()<=0){
            throw new CustomException("要修改的菜品ids为空");
        }
        ids.forEach(item->{
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId, item);
            Dish dish = new Dish();
            dish.setStatus(status);
            this.update(dish, queryWrapper);
        });
    
    }

增加修改菜品状态控制器

controller/DishController

    /**
     * 修改菜品状态
     * @param ids
     * @param status 获取的值即为要修改的值
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@RequestParam List<Long> ids, @PathVariable Integer status){
    
        dishService.updateStatus(ids, status);
        return Result.success("修改菜品状态成功");
    
    }



#### 4.4 单条、批量删除菜品

##### 4.4.1 需求分析

当点击批量删除或删除按钮时，发起DELETE 请求 dish?ids=id1,id2

删除前需要确定该菜品是否是停售状态，如果是则删除，如果不是，则提示该菜品不能删除。

删除菜品是需要同步删除关联的口味信息。

##### 4.4.2 代码实现

定义删除菜品接口和接口实现类

service/DishService

```java
// 扩展方法，通过菜品ID，删除菜品信息以及关联的口味信息和套餐信息
public void deleteByIdWithFlavorAndSetmeal(List<Long> ids);
```

service/impl/DishServiceImpl 

```java
/**
 * 根据菜单ID，删除菜单和口味信息
 * 需要判断当前菜品是否为停售
 * 涉及 dish, dish_flavor 表，注意添加事务
 * @param ids
 */
@Override
@Transactional
public void deleteByIdWithFlavorAndSetmeal(List<Long> ids) {
    if (ids.size()<=0){
        throw new CustomException("要删除的菜品id为空");
    }
    // 构建查询构造器
    LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
    // 增加查询条件
    dishQueryWrapper.in(Dish::getId, ids);
    // 增加查询条件, 状态为1
    dishQueryWrapper.eq(Dish::getStatus, 1);
    log.info("统计包含提交的要删除菜品ID以及菜品状态为售卖: {}", this.count(dishQueryWrapper));
    if (this.count(dishQueryWrapper)>0){
        throw new CustomException("该菜品还在售卖，不能删除");
    }

    // 删除菜品信息
    this.removeBatchByIds(ids);

    // 通过菜品id 获取口味信息, 并删除
    LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
    flavorQueryWrapper.in(DishFlavor::getDishId, ids);
    dishFlavorService.remove(flavorQueryWrapper);

}
```



定义删除菜品控制器类

controller/DishController

```java
/**
 * 通过ID删除菜品和菜品口味信息
 * 需要判断菜品是否停售
 * 需要同时删除3张表，dish, dish_flavor
 * @param ids
 * @return
 */
@DeleteMapping
public Result<String> delete(@RequestParam List<Long> ids){
    log.info("要删除的菜品id: {}", ids);
    dishService.deleteByIdWithFlavorAndSetmeal(ids);
    return Result.success("删除菜品成功");
}
```



##### 4.4.3 结果验证



删除失败

效果图略

删除成功

效果图略




### 5、套餐管理

#### 5.1 新增套餐

##### 5.1.1 需求分析

点击新增套餐，需要获取套餐分类信息 

点击添加菜品，需要获取菜品分类及菜品信息 GET dish/list?categoryid=xxx

点击上传图片，需要上传图片和下载图片功能 GET update/down

点击保存，需要更新套餐信息，套餐和菜品关联信息 POST setmeal



##### 5.1.2 代码实现



###### 5.1.2.1 定义接口和实例

定义套餐实例、套餐和菜品关联实例

套餐实例在菜品管理中已定义。

套餐和菜品关联实例

entity/SetmealDish

```java
/**
 * 套餐和菜品关联信息
 * @author
 * @date 2023/8/10
 */
@Data
public class SetmealDish implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    //套餐id
    private Long setmealId;

    //菜品id
    private Long dishId;

    //菜品名称 （冗余字段）
    private String name;

    //菜品原价
    private BigDecimal price;

    //份数
    private Integer copies;

    //排序
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



定义套餐、套餐和菜品关联mapper

套餐mapper在菜品管理时已定义。

套餐和菜品关联mapper

mapper/SetmealDishMapper

```java
/**
 * @author
 * @date 2023/8/10
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
```

定义套餐、套餐和菜品关联接口

套餐接口在菜品管理时已定义

套餐和菜品关联接口

service/SetmealDishService

```java
/**
 * @author
 * @date 2023/8/10
 */
public interface SetmealDishService extends IService<SetmealDish> {
}
```

定义套餐、套餐和菜品关联接口实现类

套餐实现接口在菜品管理中已定义

套餐和菜品关联接口实现类

service/impl/SetmealDishServiceImpl

```java
/**
 * @author
 * @date 2023/8/10
 */
@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

}
```

定义套餐控制器

controller/SetmealController

```java
/**
 * @author
 * @date 2023/8/10
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

}
```



###### 5.1.2.2 增加菜品接口

该接口用于在增加套餐弹窗中显示菜品分类和菜品信息

controller/DishController

```java
/**
 * 通过条件查询菜品信息
 * 该接口用于后期的套餐管理以及接口
 * @param dish
 * @return
 */
@GetMapping("/list")
public Result<List<Dish>> list(Dish dish){

    // 构建查询条件
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
    // 增加过滤条件，菜品status 为1
    queryWrapper.eq(Dish::getStatus, 1);
    // 排序
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    // 查询
    List<Dish> list = dishService.list(queryWrapper);
    return Result.success(list);
}
```



###### 5.1.2.2 定义保存方法

这里需要保存套餐以及套餐和菜品管理表，单独的套餐实例无法满足，所以需要重新定义一个实例对象用于保存套餐和菜品信息。

dto/SetmealDto

```java
/**
 * 套餐扩展字段
 * @author
 * @date 2023/8/10
 */
@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
```



定义保存套餐控制器

controller/SetmealController

```java
/**
 * 保存套餐信息 以及套餐和菜品关联信息
 * @param setmealDto
 * @return
 */
@PostMapping
public Result<String> save(@RequestBody SetmealDto setmealDto){
    log.info("要保存的套餐相关信息: {}", setmealDto.toString());
    return null;
}
```



通过触发保存按钮，从日志中获取的信息说明已获取需要的两张表的字段，下一步就是拼接和保存。

SetmealDto(setmealDishes=[SetmealDish(id=null, setmealId=null, dishId=1689232484623024129, name=小炒西兰花, price=2500, copies=1, sort=null, createTime=null, updateTime=null, createUser=null, updateUser=null, isDeleted=null), SetmealDish(id=null, setmealId=null, dishId=1397854652581064706, name=麻辣水煮鱼, price=14800, copies=1, sort=null, createTime=null, updateTime=null, createUser=null, updateUser=null, isDeleted=null)], categoryName=null)



定义保存套餐方法，同时插入 setmeal 和 setmeal_dish 表。

service/SetmealService

```java
// 扩展方法， 通过套餐接口，保存套餐信息和套餐以及菜品关联信息
public void saveWithDish(SetmealDto setmealDto);
```

service/impl/SetmealServiceImpl

```java
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时插入套餐表、套餐和菜品关联表，注意添加事务
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 插入套餐表信息
        this.save(setmealDto);

        // 获取套餐和菜品关联信息，并拼接字段 在每条记录上补充套餐ID
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        // 保存套餐和菜品关联信息
        setmealDishService.saveBatch(setmealDishes);
    }
}
```



###### 5.1.2.3 修改保存控制器

controller/SetmealController

```java
/**
 * 保存套餐信息 以及套餐和菜品关联信息
 * @param setmealDto
 * @return
 */
@PostMapping
public Result<String> save(@RequestBody SetmealDto setmealDto){
    log.info("要保存的套餐相关信息: {}", setmealDto.toString());
    setmealService.saveWithDish(setmealDto);
    return Result.success("保存套餐和关联菜品信息成功");
}
```



##### 5.1.3 功能验证

效果图略


#### 5.2 套餐管理页面展示

##### 5.2.1 需求分析

通过前端请求获悉套餐管理页面 GET setmeal/page 接口

同时还需要通过category_id获取套餐分类信息，拼接到records字段，用于前端页面显示。



##### 5.2.2 代码实现

定义获取套餐信息的page控制接口

controller/SetmealController

```java
/**
 * 分页查询，获取套餐信息
 * 这里还需要获取套餐的分类信息
 * @param page
 * @param pageSize
 * @param name
 * @return
 */
@GetMapping("/page")
public Result<Page> page(int page, int pageSize, String name){
    // 构建分页构造器
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> setmealDtoPage = new Page<>();

    // 构建查询构造器
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.like(name !=null, Setmeal::getName, name);
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);
    setmealService.page(pageInfo, queryWrapper);

    // 获取套餐分类信息
    BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
    List<Setmeal> records = pageInfo.getRecords();
    // 拼接 records 字段
    List<SetmealDto> list = records.stream().map((item) -> {
        SetmealDto setmealDto = new SetmealDto();
        // 对象拷贝
        BeanUtils.copyProperties(item, setmealDto);
        // 获取分类ID
        Long categoryId = item.getCategoryId();
        // 获取分类信息
        Category category = categoryService.getById(categoryId);

        if (category != null) {
            setmealDto.setCategoryName(category.getName());
        }
        return setmealDto;
    }).collect(Collectors.toList());

    setmealDtoPage.setRecords(list);
    log.info("获取的套餐信息: {}", setmealDtoPage.toString());
    return Result.success(setmealDtoPage);
}
```

##### 5.2.3 页面展示

效果图略


#### 5.3 套餐修改接口

##### 5.3.1 需求分析

通过套餐ID 获取套餐信息, GET setmeal/id

通过套餐ID 获取关联的菜品信息

保存套餐信息和关联菜品信息 POST setmeal， 需要修改套餐信息表setmeal和套餐及菜品管理表setmeal_dish

##### 5.3.2 代码实现

定义通过套餐ID 获取套餐信息和关联菜品信息的方法接口

service/SetmealService

```java
// 扩展方法，通过套餐ID，获取套餐信息和套餐管理的菜品信息
public SetmealDto getByIdWithDish(Long id);
```

service/impl/SetmealServiceImpl

```
/**
 * 通过套餐ID获取套餐信息以及菜品信息
 * @param id
 * @return
 */
@Override
public SetmealDto getByIdWithDish(Long id) {
    // 查询套餐信息
    Setmeal setmeal = this.getById(id);
    // 构建新对象用于存放套餐及菜品信息
    SetmealDto setmealDto = new SetmealDto();
    BeanUtils.copyProperties(setmeal, setmealDto);

    // 构建查询构造器
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    // 构建过滤条件
    queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
    List<SetmealDish> list = setmealDishService.list(queryWrapper);
    setmealDto.setSetmealDishes(list);

    return setmealDto;
}
```

定义获取套餐信息控制器

controller/SetmealController

```java
/**
 * 通过套餐ID 获取套餐信息和关联的菜品信息
 * @param id
 * @return
 */
@GetMapping("/{id}")
public Result<SetmealDto> getById(@PathVariable Long id){
    log.info("获取套餐id: {}", id);
    SetmealDto setmealDto = setmealService.getByIdWithDish(id);
    return Result.success(setmealDto);
}
```



定义通过套餐ID 保存套餐信息和关联菜品方法接口

service/SetmealService

```java
// 扩展方法，修改套餐信息以及关联的菜品信息
public void updateWithDish(SetmealDto setmealDto);
```

service/impl/SetmealServiceImpl

```java
   /**
     * 修改套餐信息和关联度的菜品信息 setmeal, setmeal_dish
     * 对setmeal_dish 的操作为先删除，后添加
     * 注意添加事务
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {

        // 定义一个新对象用于保存关联的菜品信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        // 修改套餐信息
        log.info("更新的套餐信息: {}", setmeal);
        this.updateById(setmeal);

        // 删除套餐关联的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        setmealDishService.remove(queryWrapper);
        // 拼接要保存的关联菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().peek((item) -> {
//            log.info("获取套餐id: {}", setmealDto.getId());
            item.setSetmealId(setmeal.getId());
        }).collect(Collectors.toList());

        log.info("更新后的菜品信息: {}", setmealDishes.toArray());

        setmealDishService.saveBatch(setmealDishes);
    }
```

定义更改套餐信息控制器

controller/SetmealController

```java
/**
 * 更新套餐信息以及套餐关联的菜品信息
 * @param setmealDto
 * @return
 */
@PutMapping
public Result<String> update(@RequestBody SetmealDto setmealDto){
    log.info("提交的套餐信息: {}", setmealDto.toString());
    setmealService.updateWithDish(setmealDto);
    return Result.success("更新套餐信息和管理菜品信息成功");
}
```



##### 5.3.3 功能验证

日志

效果图略

数据表

效果图略


#### 5.4 停售、启售

##### 5.4.1 需求分析

点击停售、启售或批量停售、批量启售按钮，发起POST请求 http://127.0.0.1:8080/setmeal/status/0?ids=1689468237508542466

从URL可以解析要修改的套餐状态值 0： 停售， 1 启售， ids 是要变更的套餐ID，这里只需要修改setmeal表。

##### 5.4.2 代码实现

定义批量修改状态接口和实现类

service/SetmealService

```java
// 扩展方法，修改套餐状态
public void updateStatus(List<Long> ids, Integer status);
```

service/impl/SetmealServiceImpl

```java
/**
 * 修改套餐状态，包含单条和批量
 * @param ids
 * @param status
 */
@Override
public void updateStatus(List<Long> ids, Integer status) {
    log.info("获取到的ids: {}, 要修改的状态: {}", ids, status);

    if (ids.size()<=0){
        throw new CustomException("要修改的套餐ID为空");
    }
    ids.forEach(item->{
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId, item);
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        this.update(setmeal, queryWrapper);
    });
}
```

定义批量修改状态接口的控制器

controller/SetmealController

```java
/**
 * 修改套餐状态，包括单条和批量
 * @param status
 * @param ids
 * @return
 */
@PostMapping("/status/{status}")
public Result<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
    log.info("要修改的IDS:{}, 要修改的状态: {}", ids.toArray(), status);
    setmealService.updateStatus(ids, status);
    String msg = "修改套餐状态成功";
    if (ids.size()>1){
        msg = "批量修改套餐状态成功";
    }
    return Result.success(msg);
}
```



##### 5.4.3 功能测试



效果图略


#### 5.5 删除套餐

##### 5.5.1 需求分析

点击批量删除或删除按钮时，发起Delete 请求setmeal?ids=1518510426785161218

删除时需要判断该套餐是否为售卖状态，如果是提示不能删除；如果否则删除套餐和套餐管理的菜品，需要修改setmeal 和 setmeal_dish 表。

##### 5.5.2 代码实现

定义删除接口和接口实现类

service/SetmealService

```java
// 扩展方法，通过套餐ID 删除套餐以及关联的菜品信息
public void removeByIdWithDish(List<Long> ids);
```

service/impl/SetmealServiceImpl

```java
/**
 * 删除套餐以及关联的菜品，需要操作 setmeal 和 setmeal_dish 注意事务
 * 需要判断套餐是否正则售卖
 * @param ids
 */
@Override
@Transactional
public void removeByIdWithDish(List<Long> ids) {
    log.info("获取的ids列表: {}", ids);
    if (ids.size() <=0){
        throw new CustomException("要删除的套餐ID列表为空");
    }
    // 查询套餐是否可删除
    LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
    // 构建查询条件
    setmealQueryWrapper.in(Setmeal::getId, ids);
    //
    setmealQueryWrapper.eq(Setmeal::getStatus, 1);
    // 统计符合条件的套餐
    if (this.count(setmealQueryWrapper) >0){
        throw new CustomException("该套餐正在售卖，不能删除");
    }

    // 删除套餐
    this.removeBatchByIds(ids);

    // 根据套餐获取对应的菜品
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.in(SetmealDish::getSetmealId, ids);
    setmealDishService.remove(queryWrapper);
}
```

定义删除套餐控制器

controller/SetmealController

```java
/**
 * 根据套餐ID 删除套餐信息和关联的菜品信息
 * 需要判断套餐是否为售卖状态
 * @param ids
 * @return
 */
@DeleteMapping
public Result<String> delete(@RequestParam List<Long> ids){
    log.info("要删除的套餐ID: {}", ids);
    setmealService.removeByIdWithDish(ids);
    return null;
}
```

##### 5.5.3 功能验证

删除成功

效果图略

删除失败

效果图略


### 6、订单明细

该项目订单由手机端触发，这里不做具体的接口开发，只做展示、查询等基础功能。

#### 6.1 页面展示

##### 6.1.1 需求分析

刷新订单页面，GET order/page 接口，获取订单信息。

##### 6.1.2 代码实现

创建订单实例类

entity/Orders

```java
/**
 * @author
 * @date 2023/8/10
 */
@Data
public class Orders implements Serializable {


    private static final long serialVersionUID = 1L;

    private Long id;

    //订单号
    private String number;

    //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;

    //下单用户id
    private Long userId;

    //地址id
    private Long addressBookId;

    //下单时间
    private String orderTime;

    //结账时间
    private String checkoutTime;

    //支付方式 1微信，2支付宝
    private Integer payMethod;

    //实收金额
    private BigDecimal amount;

    //备注
    private String remark;

    //用户名
    private String userName;

    //手机号
    private String phone;

    //地址
    private String address;

    //收货人
    private String consignee;
}
```

定义订单Mapper

mapper/OrderMapper

```java
/**
 * @author
 * @date 2023/8/10
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
```

定义订单接口和接口实现类

service/OrderService

```java
/**
 * @author
 * @date 2023/8/10
 */
public interface OrderService extends IService<Orders> {
}
```

service/impl/OrderServiceImpl

```java
/**
 * @author
 * @date 2023/8/10
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

}
```



定义订单查询控制器

```java
/**
 * @author
 * @date 2023/8/10
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 分页查询，增加订单查询和时间端查询功能
     * @param page
     * @param pageSize
     * @param number
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String number, String beginTime,String endTime){
        // 分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        // 查询构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        queryWrapper.ge(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .le(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, queryWrapper);

        return Result.success(pageInfo);
    }
}
```



##### 6.1.3 功能验证

效果图略



