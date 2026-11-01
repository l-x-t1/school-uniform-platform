package com.xfgl.school.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptEduSettlement;
import com.xfgl.school.domain.vo.EduSettlementStatisticsVO;

/**
 * 教育局结算管理Mapper接口
 *
 * @author codex
 * @date 2026-05-17
 */
public interface XfptEduSettlementMapper extends BaseMapper<XfptEduSettlement> {
    /**
     * 查询教育局结算管理列表
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算管理集合
     */
    public List<XfptEduSettlement> selectXfptEduSettlementList(XfptEduSettlement xfptEduSettlement);

    public XfptEduSettlement selectXfptEduSettlementById(XfptEduSettlement xfptEduSettlement);

    /**
     * 查询教育局结算统计
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算统计
     */
    public EduSettlementStatisticsVO selectXfptEduSettlementStatistics(XfptEduSettlement xfptEduSettlement);
}
