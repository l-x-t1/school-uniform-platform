package com.xfgl.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.SupplierUserRel;
import com.xfgl.system.api.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 供应商用户关联表 Mapper
 *
 * @date 2026-05-31
 */
@Mapper
public interface SupplierUserRelMapper extends BaseMapper<SupplierUserRel> {

    @Select("SELECT supplier_id FROM xfpt_supplier_user_rel WHERE user_id = #{userId}")
    List<Long> findSupplierIdsByUserId(@Param("userId") Long userId);

    @Select("SELECT user_id FROM xfpt_supplier_user_rel WHERE supplier_id = #{supplierId}")
    List<Long> findUserIdsBySupplierId(@Param("supplierId") Long supplierId);

    @Select("SELECT supplier_id FROM xfpt_supplier_user_rel WHERE user_id = #{userId} LIMIT 1")
    Long findFirstSupplierIdByUserId(@Param("userId") Long userId);

    @Select("SELECT u.user_id, u.user_name, u.nick_name, u.phonenumber FROM xfpt_supplier_user_rel r JOIN sys_user u ON r.user_id = u.user_id WHERE r.supplier_id = #{supplierId}")
    List<SysUser> findUsersBySupplierId(@Param("supplierId") Long supplierId);

    @Select("<script>" +
        "SELECT r.supplier_id, r.user_id, u.user_name, u.nick_name, u.phonenumber " +
        "FROM xfpt_supplier_user_rel r " +
        "LEFT JOIN sys_user u ON r.user_id = u.user_id " +
        "WHERE r.supplier_id IN " +
        "<foreach collection='supplierIds' item='supplierId' open='(' separator=',' close=')'>" +
        "#{supplierId}" +
        "</foreach>" +
        "</script>")
    List<SupplierUserRel> findUserRelsBySupplierIds(@Param("supplierIds") List<Long> supplierIds);

    @Select("SELECT COUNT(1) > 0 FROM xfpt_supplier_user_rel WHERE user_id = #{userId}")
    boolean existsByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM xfpt_supplier_user_rel WHERE supplier_id = #{supplierId} AND user_id = #{userId}")
    int deleteBySupplierAndUser(@Param("supplierId") Long supplierId, @Param("userId") Long userId);

    @Select("<script>" +
        "SELECT u.user_id, u.user_name, u.nick_name, u.phonenumber FROM sys_user u " +
        "INNER JOIN sys_user_role ur ON u.user_id = ur.user_id AND ur.role_id = 115 " +
        "WHERE u.del_flag = '0' " +
        "AND u.user_id NOT IN (SELECT user_id FROM xfpt_supplier_user_rel) " +
        "<if test='keyword != null and keyword != \"\"'> " +
        "AND (u.user_name LIKE CONCAT('%', #{keyword}, '%') OR u.nick_name LIKE CONCAT('%', #{keyword}, '%') OR u.phonenumber LIKE CONCAT('%', #{keyword}, '%')) " +
        "</if>" +
        "ORDER BY u.create_time DESC" +
        "</script>")
    List<SysUser> findAvailableSupplierUsers(@Param("keyword") String keyword);
}
