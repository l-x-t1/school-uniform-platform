package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSchoolGradeClassMapper;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.school.service.IXfptSchoolGradeClassService;

/**
 * 学校年级班级Service业务层处理
 *
 * @author yinet
 * @date 2025-04-24
 */
@Service
public class XfptSchoolGradeClassServiceImpl extends ServiceImpl<XfptSchoolGradeClassMapper, XfptSchoolGradeClass> implements IXfptSchoolGradeClassService {

    @Autowired
    private XfptSchoolGradeClassMapper xfptSchoolGradeClassMapper;

    /**
     * 查询学校年级班级列表
     *
     * @param xfptSchoolGradeClass 学校年级班级
     * @return 学校年级班级
     */
    @Override
    public List<XfptSchoolGradeClass> selectXfptSchoolGradeClassList(XfptSchoolGradeClass xfptSchoolGradeClass)
    {
        return xfptSchoolGradeClassMapper.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
    }

    @Override
    public SysDept selectDeptId(String id){
        return  xfptSchoolGradeClassMapper.selectDeptId(id);
    }


}
