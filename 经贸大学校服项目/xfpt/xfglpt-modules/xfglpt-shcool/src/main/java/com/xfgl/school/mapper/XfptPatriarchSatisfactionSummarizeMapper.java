package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptPatriarchSatisfactionSummarize;

/**
 * 选用组织家长满意度总结报告报备与公开Mapper接口
 *
 * @author yinet
 * @date 2025-04-27
 */
public interface XfptPatriarchSatisfactionSummarizeMapper extends BaseMapper<XfptPatriarchSatisfactionSummarize> {
    /**
     * 查询选用组织家长满意度总结报告报备与公开列表
     *
     * @param xfptPatriarchSatisfactionSummarize 选用组织家长满意度总结报告报备与公开
     * @return 选用组织家长满意度总结报告报备与公开集合
     */
    public List<XfptPatriarchSatisfactionSummarize> selectXfptPatriarchSatisfactionSummarizeList(XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize);
}
