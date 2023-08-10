package com.reggie2.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie2.common.Result;
import com.reggie2.entity.Employee;
import com.reggie2.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.PeriodUnit;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2023/8/8
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

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
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

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
        log.info(employee.toString());
        employeeService.save(employee);
        return Result.success("添加员工成功");
    }

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
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);

    }

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

}
