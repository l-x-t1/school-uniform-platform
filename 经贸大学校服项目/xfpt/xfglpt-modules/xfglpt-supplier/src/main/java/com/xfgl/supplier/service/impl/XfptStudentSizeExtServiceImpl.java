package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptStudentSizeExtMapper;
import com.xfgl.supplier.domain.XfptStudentSizeExt;
import com.xfgl.supplier.service.IXfptStudentSizeExtService;

/**
 * 学生尺码扩展Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptStudentSizeExtServiceImpl extends ServiceImpl<XfptStudentSizeExtMapper, XfptStudentSizeExt> implements IXfptStudentSizeExtService {

    @Autowired
    private XfptStudentSizeExtMapper xfptStudentSizeExtMapper;

    /**
     * 查询学生尺码扩展列表
     *
     * @param xfptStudentSizeExt 学生尺码扩展
     * @return 学生尺码扩展
     */
    @Override
    public List<XfptStudentSizeExt> selectXfptStudentSizeExtList(XfptStudentSizeExt xfptStudentSizeExt)
    {
        return xfptStudentSizeExtMapper.selectXfptStudentSizeExtList(xfptStudentSizeExt);
    }


}
