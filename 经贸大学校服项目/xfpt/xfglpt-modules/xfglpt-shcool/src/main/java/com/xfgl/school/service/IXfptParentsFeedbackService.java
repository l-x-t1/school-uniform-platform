package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptParentsFeedback;

/**
 * 家长反馈Service接口
 *
 * @author yinet
 * @date 2025-05-13
 */
public interface IXfptParentsFeedbackService extends IService<XfptParentsFeedback> {

    /**
     * 查询家长反馈列表
     *
     * @param xfptParentsFeedback 家长反馈
     * @return 家长反馈集合
     */
    public List<XfptParentsFeedback> selectXfptParentsFeedbackList(XfptParentsFeedback xfptParentsFeedback);

    List<XfptParentsFeedback> selectXfptParentsFeedbackList2(XfptParentsFeedback xfptParentsFeedback);
}
