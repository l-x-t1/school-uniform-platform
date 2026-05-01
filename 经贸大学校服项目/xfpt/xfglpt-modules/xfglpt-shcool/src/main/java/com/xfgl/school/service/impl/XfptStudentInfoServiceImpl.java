package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptStudentInfoMapper;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.service.IXfptStudentInfoService;

/**
 * 家长绑定学生信息Service业务层处理
 *
 * @author yinet
 * @date 2025-05-13
 */
@Service
public class XfptStudentInfoServiceImpl extends ServiceImpl<XfptStudentInfoMapper, XfptStudentInfo> implements IXfptStudentInfoService {

    @Autowired
    private XfptStudentInfoMapper xfptStudentInfoMapper;

    /**
     * 查询家长绑定学生信息列表
     *
     * @param xfptStudentInfo 家长绑定学生信息
     * @return 家长绑定学生信息
     */
    @Override
    public List<XfptStudentInfo> selectXfptStudentInfoList(XfptStudentInfo xfptStudentInfo)
    {
        return xfptStudentInfoMapper.selectXfptStudentInfoList(xfptStudentInfo);
    }


}
