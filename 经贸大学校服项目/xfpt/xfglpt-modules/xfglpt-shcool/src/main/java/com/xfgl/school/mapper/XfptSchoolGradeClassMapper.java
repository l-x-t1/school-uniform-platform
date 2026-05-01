package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.system.api.domain.SysDept;
import org.apache.ibatis.annotations.Param;

/**
 * 学校年级班级Mapper接口
 *
 * @author yinet
 * @date 2025-04-24
 */
public interface XfptSchoolGradeClassMapper extends BaseMapper<XfptSchoolGradeClass> {
    /**
     * 查询学校年级班级列表
     *
     * @param xfptSchoolGradeClass 学校年级班级
     * @return 学校年级班级集合
     */
    public List<XfptSchoolGradeClass> selectXfptSchoolGradeClassList(XfptSchoolGradeClass xfptSchoolGradeClass);

    public SysDept selectDeptId(@Param("id") String id);
}
