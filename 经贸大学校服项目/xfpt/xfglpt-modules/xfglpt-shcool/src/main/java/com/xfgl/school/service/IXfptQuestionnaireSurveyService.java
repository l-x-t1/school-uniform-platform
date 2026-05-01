package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;

/**
 * 问卷调查库Service接口
 *
 * @author yinet
 * @date 2025-04-24
 */
public interface IXfptQuestionnaireSurveyService extends IService<XfptQuestionnaireSurvey> {

    /**
     * 查询问卷调查库列表
     *
     * @param xfptQuestionnaireSurvey 问卷调查库
     * @return 问卷调查库集合
     */
    public List<XfptQuestionnaireSurvey> selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey xfptQuestionnaireSurvey);

}
