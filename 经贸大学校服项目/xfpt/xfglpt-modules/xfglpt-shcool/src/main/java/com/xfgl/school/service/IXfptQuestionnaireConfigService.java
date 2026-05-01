package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptQuestionnaireConfig;

/**
 * 问卷调查主配置Service接口
 *
 * @author wxm
 * @date 2025-04-27
 */
public interface IXfptQuestionnaireConfigService extends IService<XfptQuestionnaireConfig> {

    /**
     * 查询问卷调查主配置列表
     *
     * @param xfptQuestionnaireConfig 问卷调查主配置
     * @return 问卷调查主配置集合
     */
    public List<XfptQuestionnaireConfig> selectXfptQuestionnaireConfigList(XfptQuestionnaireConfig xfptQuestionnaireConfig);

    /**
     * 查询模板配置
     * @return
     */
    public List<XfptQuestionnaireConfig> selectTemplateQuestionnaireConfigList();

    List<XfptQuestionnaireConfig> selectXfptQuestionnaireConfigList2(XfptQuestionnaireConfig xfptQuestionnaireConfig);
}
