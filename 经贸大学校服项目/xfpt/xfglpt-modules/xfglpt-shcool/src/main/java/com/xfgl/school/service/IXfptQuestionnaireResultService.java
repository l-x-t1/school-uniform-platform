package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptQuestionnaireResult;

/**
 * 问卷调查结果Service接口
 *
 * @author yinet
 * @date 2025-05-07
 */
public interface IXfptQuestionnaireResultService extends IService<XfptQuestionnaireResult> {

    /**
     * 查询问卷调查结果列表
     *
     * @param xfptQuestionnaireResult 问卷调查结果
     * @return 问卷调查结果集合
     */
    public List<XfptQuestionnaireResult> selectXfptQuestionnaireResultList(XfptQuestionnaireResult xfptQuestionnaireResult);

}
