package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptParentsFeedback;

/**
 * 家长反馈Mapper接口
 *
 * @author yinet
 * @date 2025-05-13
 */
public interface XfptParentsFeedbackMapper extends BaseMapper<XfptParentsFeedback> {
    /**
     * 查询家长反馈列表
     *
     * @param xfptParentsFeedback 家长反馈
     * @return 家长反馈集合
     */
    public List<XfptParentsFeedback> selectXfptParentsFeedbackList(XfptParentsFeedback xfptParentsFeedback);

    List<XfptParentsFeedback> selectXfptParentsFeedbackList2(XfptParentsFeedback xfptParentsFeedback);

}
