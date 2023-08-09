package com.reggie2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie2.entity.Employee;
import com.reggie2.mapper.EmployeeMapper;
import com.reggie2.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2023/8/8
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
