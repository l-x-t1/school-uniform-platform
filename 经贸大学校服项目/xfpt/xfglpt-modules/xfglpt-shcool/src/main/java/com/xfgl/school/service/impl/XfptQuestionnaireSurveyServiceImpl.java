package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptQuestionnaireSurveyMapper;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;
import com.xfgl.school.service.IXfptQuestionnaireSurveyService;

/**
 * 问卷调查库Service业务层处理
 *
 * @author yinet
 * @date 2025-04-24
 */
@Service
public class XfptQuestionnaireSurveyServiceImpl extends ServiceImpl<XfptQuestionnaireSurveyMapper, XfptQuestionnaireSurvey> implements IXfptQuestionnaireSurveyService {

    @Autowired
    private XfptQuestionnaireSurveyMapper xfptQuestionnaireSurveyMapper;

    /**
     * 查询问卷调查库列表
     *
     * @param xfptQuestionnaireSurvey 问卷调查库
     * @return 问卷调查库
     */
    @Override
    public List<XfptQuestionnaireSurvey> selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey xfptQuestionnaireSurvey)
    {
        return xfptQuestionnaireSurveyMapper.selectXfptQuestionnaireSurveyList(xfptQuestionnaireSurvey);
    }


}
