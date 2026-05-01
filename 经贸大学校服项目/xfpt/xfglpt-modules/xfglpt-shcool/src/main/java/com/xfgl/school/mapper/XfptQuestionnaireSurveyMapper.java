package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;

/**
 * 问卷调查库Mapper接口
 *
 * @author yinet
 * @date 2025-04-24
 */
public interface XfptQuestionnaireSurveyMapper extends BaseMapper<XfptQuestionnaireSurvey> {
    /**
     * 查询问卷调查库列表
     *
     * @param xfptQuestionnaireSurvey 问卷调查库
     * @return 问卷调查库集合
     */
    public List<XfptQuestionnaireSurvey> selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey xfptQuestionnaireSurvey);
}
