package com.xfgl.school.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户角色关联表 Mapper（仅用于审核通过后分配角色）
 */
@Mapper
public interface SysUserRoleMapper {

    @Insert("INSERT INTO sys_user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("SELECT COUNT(1) > 0 FROM sys_user_role WHERE user_id = #{userId} AND role_id = 115")
    boolean isSupplierUser(@Param("userId") Long userId);
}
