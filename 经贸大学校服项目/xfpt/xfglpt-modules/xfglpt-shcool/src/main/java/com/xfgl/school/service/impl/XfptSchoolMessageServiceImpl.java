package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.datascope.annotation.DataScope;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysDictData;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSchoolMessageMapper;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.service.IXfptSchoolMessageService;

/**
 * 学校信息Service业务层处理
 *
 * @author yinet
 * @date 2025-05-08
 */
@Service
public class XfptSchoolMessageServiceImpl extends ServiceImpl<XfptSchoolMessageMapper, XfptSchoolMessage> implements IXfptSchoolMessageService {

    @Autowired
    private XfptSchoolMessageMapper xfptSchoolMessageMapper;

    /**
     * 查询学校信息列表
     *
     * @param xfptSchoolMessage 学校信息
     * @return 学校信息
     */
    @Override
    public List<XfptSchoolMessage> selectXfptSchoolMessageList(XfptSchoolMessage xfptSchoolMessage)
    {
        return xfptSchoolMessageMapper.selectXfptSchoolMessageList(xfptSchoolMessage);
    }

    @Override
    public    List<SysDictData> selectDictDate(String dictType){
        return xfptSchoolMessageMapper.selectDictDate(dictType);
    };

    @Override
//    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return xfptSchoolMessageMapper.selectDeptList(dept);
    }

    @Override
//    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList1(SysDept dept)
    {
        return xfptSchoolMessageMapper.selectDeptList1(dept);
    }
    public String selectSchoolNumber(String deptId){
        return xfptSchoolMessageMapper.selectSchoolNumber(deptId);
    };


}
