package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.system.api.domain.SysDept;

/**
 * 学校年级班级Service接口
 *
 * @author yinet
 * @date 2025-04-24
 */
public interface IXfptSchoolGradeClassService extends IService<XfptSchoolGradeClass> {

    /**
     * 查询学校年级班级列表
     *
     * @param xfptSchoolGradeClass 学校年级班级
     * @return 学校年级班级集合
     */
    public List<XfptSchoolGradeClass> selectXfptSchoolGradeClassList(XfptSchoolGradeClass xfptSchoolGradeClass);

    SysDept selectDeptId(String id);

}
