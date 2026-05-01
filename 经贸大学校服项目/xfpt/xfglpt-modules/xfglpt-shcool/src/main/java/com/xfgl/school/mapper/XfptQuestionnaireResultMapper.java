package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptQuestionnaireResult;

/**
 * 问卷调查结果Mapper接口
 *
 * @author yinet
 * @date 2025-05-07
 */
public interface XfptQuestionnaireResultMapper extends BaseMapper<XfptQuestionnaireResult> {
    /**
     * 查询问卷调查结果列表
     *
     * @param xfptQuestionnaireResult 问卷调查结果
     * @return 问卷调查结果集合
     */
    public List<XfptQuestionnaireResult> selectXfptQuestionnaireResultList(XfptQuestionnaireResult xfptQuestionnaireResult);
}
