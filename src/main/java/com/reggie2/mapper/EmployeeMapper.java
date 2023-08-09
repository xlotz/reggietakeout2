package com.reggie2.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie2.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author
 * @date 2023/8/8
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
