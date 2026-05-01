package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptParentsFeedbackMapper;
import com.xfgl.school.domain.XfptParentsFeedback;
import com.xfgl.school.service.IXfptParentsFeedbackService;

/**
 * 家长反馈Service业务层处理
 *
 * @author yinet
 * @date 2025-05-13
 */
@Service
public class XfptParentsFeedbackServiceImpl extends ServiceImpl<XfptParentsFeedbackMapper, XfptParentsFeedback> implements IXfptParentsFeedbackService {

    @Autowired
    private XfptParentsFeedbackMapper xfptParentsFeedbackMapper;

    /**
     * 查询家长反馈列表
     *
     * @param xfptParentsFeedback 家长反馈
     * @return 家长反馈
     */
    @Override
    public List<XfptParentsFeedback> selectXfptParentsFeedbackList(XfptParentsFeedback xfptParentsFeedback)
    {
        return xfptParentsFeedbackMapper.selectXfptParentsFeedbackList(xfptParentsFeedback);
    }

    @Override
    public List<XfptParentsFeedback> selectXfptParentsFeedbackList2(XfptParentsFeedback xfptParentsFeedback) {
        return xfptParentsFeedbackMapper.selectXfptParentsFeedbackList2(xfptParentsFeedback);
    }


}
