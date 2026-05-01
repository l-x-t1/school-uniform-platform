package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysDictData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学校信息Mapper接口
 *
 * @author yinet
 * @date 2025-05-08
 */
public interface XfptSchoolMessageMapper extends BaseMapper<XfptSchoolMessage> {
    /**
     * 查询学校信息列表
     *
     * @param xfptSchoolMessage 学校信息
     * @return 学校信息集合
     */
    public List<XfptSchoolMessage> selectXfptSchoolMessageList(XfptSchoolMessage xfptSchoolMessage);


    @Select("select count(*) from sys_dept  WHERE  find_in_set(#{deptId},ancestors) and is_school='1'")
    Long getCount(Long deptId);

    @Select("SELECT dept.* FROM XFPT_SCHOOL_GRADE_CLASS school \n" +
            "LEFT JOIN SYS_DEPT dept ON school.dept_id=dept.dept_id \n" +
            "WHERE  find_in_set(#{deptId},dept.ancestors)")
    List<XfptSchoolGradeClass> classListByDept(Long deptId);

    public List<SysDictData> selectDictDate(@Param("dictType") String dictType);

    public List<SysDept> selectDeptList(SysDept dept);
    public List<SysDept> selectDeptList1(SysDept dept);

    public String selectSchoolNumber(@Param("deptId")String deptId);
}
