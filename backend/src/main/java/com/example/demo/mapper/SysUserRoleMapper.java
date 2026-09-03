package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 * <p>
 * 继承BaseMapper：获得MyBatis-Plus提供的基础CRUD方法
 * 功能说明：封装用户角色关联数据访问层操作
 * </p>
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    // 继承BaseMapper已提供基础的增删改查方法
    // 如需自定义复杂查询，可在此添加方法
}