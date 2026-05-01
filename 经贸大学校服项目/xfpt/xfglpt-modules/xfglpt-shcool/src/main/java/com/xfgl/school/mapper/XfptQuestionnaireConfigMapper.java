package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptQuestionnaireConfig;

/**
 * 问卷调查主配置Mapper接口
 *
 * @author wxm
 * @date 2025-04-27
 */
public interface XfptQuestionnaireConfigMapper extends BaseMapper<XfptQuestionnaireConfig> {
    /**
     * 查询问卷调查主配置列表
     *
     * @param xfptQuestionnaireConfig 问卷调查主配置
     * @return 问卷调查主配置集合
     */
    public List<XfptQuestionnaireConfig> selectXfptQuestionnaireConfigList(XfptQuestionnaireConfig xfptQuestionnaireConfig);

    List<XfptQuestionnaireConfig> selectXfptQuestionnaireConfigList2(XfptQuestionnaireConfig xfptQuestionnaireConfig);
}
