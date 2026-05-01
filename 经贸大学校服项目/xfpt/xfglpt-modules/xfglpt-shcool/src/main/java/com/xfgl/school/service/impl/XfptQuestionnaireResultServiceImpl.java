package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptQuestionnaireResultMapper;
import com.xfgl.school.domain.XfptQuestionnaireResult;
import com.xfgl.school.service.IXfptQuestionnaireResultService;

/**
 * 问卷调查结果Service业务层处理
 *
 * @author yinet
 * @date 2025-05-07
 */
@Service
public class XfptQuestionnaireResultServiceImpl extends ServiceImpl<XfptQuestionnaireResultMapper, XfptQuestionnaireResult> implements IXfptQuestionnaireResultService {

    @Autowired
    private XfptQuestionnaireResultMapper xfptQuestionnaireResultMapper;

    /**
     * 查询问卷调查结果列表
     *
     * @param xfptQuestionnaireResult 问卷调查结果
     * @return 问卷调查结果
     */
    @Override
    public List<XfptQuestionnaireResult> selectXfptQuestionnaireResultList(XfptQuestionnaireResult xfptQuestionnaireResult)
    {
        return xfptQuestionnaireResultMapper.selectXfptQuestionnaireResultList(xfptQuestionnaireResult);
    }


}
