package com.xfgl.school.mapper;

import com.xfgl.school.domain.vo.KeyValueVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

public interface IndexStatisticsMapper {

    @Select("SELECT  COALESCE(SUM(count), 0) FROM XFPT_PROC_SUMMARY where date_format(buy_time,'%Y') =#{format} " +
            "and del_flag='0'  and (dept_id = #{dept} or find_in_set(#{dept},ancestors))")
    Long orderCount(@Param("format") String format,@Param("dept") Long dept);

    @Select("SELECT  COALESCE(SUM(money), 0) FROM XFPT_PROC_SUMMARY where date_format(buy_time,'%Y') =#{format} " +
            "and del_flag='0'  and (dept_id = #{dept} or find_in_set(#{dept},ancestors))")
    Long orderMoneyCount(@Param("format") String format,@Param("dept") Long dept);

    @Select("SELECT dept.dept_name as `key`,COALESCE(SUM(summ.money), 0) AS value FROM SYS_DEPT dept  \n" +
            "left JOIN  (select * from XFPT_PROC_SUMMARY where EXTRACT(YEAR FROM buy_time) = EXTRACT(YEAR FROM CURRENT_DATE) and del_flag='0') summ\n" +
            "ON  find_in_set(dept.dept_id,summ.ancestors)  where dept.parent_id=100 group by dept.dept_name order by value desc")
    List<KeyValueVo> getQxMoney();


    @Select("SELECT dept.dept_name as `key`,COALESCE(SUM(summ.money), 0) AS value FROM SYS_DEPT dept  \n" +
            "left JOIN  (select * from XFPT_PROC_SUMMARY WHERE EXTRACT(YEAR FROM buy_time) = EXTRACT(YEAR FROM CURRENT_DATE) and del_flag='0') summ\n" +
            "ON  summ.dept_id = dept.dept_id  where dept.parent_id != 100   \n" +
            "group by dept.dept_name order by value desc")
    List<KeyValueVo> getSchoolMoney();


    @Select("SELECT dept.dept_name as `key`,COALESCE(SUM(summ.count), 0) AS value FROM SYS_DEPT dept  \n" +
            "left JOIN  (select * from XFPT_PROC_SUMMARY where EXTRACT(YEAR FROM buy_time) = EXTRACT(YEAR FROM CURRENT_DATE) and del_flag='0') summ\n" +
            "ON  find_in_set(dept.dept_id,summ.ancestors)  where dept.parent_id=100  group by dept.dept_name order by value desc")
    List<KeyValueVo> getQxCount();


    @Select("SELECT dept.dept_name as `key`,COALESCE(SUM(summ.count), 0) AS value FROM SYS_DEPT dept  \n" +
            "left JOIN  (select * from XFPT_PROC_SUMMARY WHERE EXTRACT(YEAR FROM buy_time) = EXTRACT(YEAR FROM CURRENT_DATE) and del_flag='0') summ\n" +
            "ON  summ.dept_id = dept.dept_id  where dept.parent_id != 100   \n" +
            "group by dept.dept_name order by value desc")
    List<KeyValueVo> getSchoolCount();

    @Select("SELECT COALESCE(COUNT(DISTINCT u.user_id), 0) " +
            "FROM sys_user u " +
            "LEFT JOIN sys_dept d ON u.dept_id = d.dept_id " +
            "WHERE u.del_flag = '0' " +
            "AND (d.dept_id = #{deptId} OR FIND_IN_SET(#{deptId}, d.ancestors))")
    Long getDistrictPlatformUserCount(@Param("deptId") Long deptId);

    @Select("SELECT COALESCE(COUNT(DISTINCT oi.school_id), 0) " +
            "FROM xfpt_order_info oi " +
            "LEFT JOIN sys_dept d ON oi.school_id = d.dept_id " +
            "WHERE oi.school_id IS NOT NULL " +
            "AND (d.dept_id = #{deptId} OR FIND_IN_SET(#{deptId}, d.ancestors))")
    Long getSchoolPlatformCount(@Param("deptId") Long deptId);

    @Select("SELECT COALESCE(COUNT(DISTINCT oi.student_id), 0) " +
            "FROM xfpt_order_info oi " +
            "LEFT JOIN sys_dept d ON oi.school_id = d.dept_id " +
            "WHERE oi.student_id IS NOT NULL " +
            "AND (d.dept_id = #{deptId} OR FIND_IN_SET(#{deptId}, d.ancestors))")
    Long getStudentPlatformCount(@Param("deptId") Long deptId);

    List<KeyValueVo> getPriceTrendData(@Param("deptId") Long deptId);


    List<KeyValueVo> getSchool5YearCountData(Long deptId);

    List<KeyValueVo> getSchool5YearMoneyData(Long deptId);

    List<KeyValueVo> schoolUniformType(Long deptId);
}
