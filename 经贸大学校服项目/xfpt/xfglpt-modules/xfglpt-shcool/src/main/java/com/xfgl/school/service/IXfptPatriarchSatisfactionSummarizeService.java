package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptPatriarchSatisfactionSummarize;

/**
 * 选用组织家长满意度总结报告报备与公开Service接口
 *
 * @author yinet
 * @date 2025-04-27
 */
public interface IXfptPatriarchSatisfactionSummarizeService extends IService<XfptPatriarchSatisfactionSummarize> {

    /**
     * 查询选用组织家长满意度总结报告报备与公开列表
     *
     * @param xfptPatriarchSatisfactionSummarize 选用组织家长满意度总结报告报备与公开
     * @return 选用组织家长满意度总结报告报备与公开集合
     */
    public List<XfptPatriarchSatisfactionSummarize> selectXfptPatriarchSatisfactionSummarizeList(XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize);

}
