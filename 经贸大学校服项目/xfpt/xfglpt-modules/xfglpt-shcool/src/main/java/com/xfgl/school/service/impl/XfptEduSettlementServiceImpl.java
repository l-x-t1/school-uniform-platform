package com.xfgl.school.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.school.domain.XfptEduSettlement;
import com.xfgl.school.domain.vo.EduSettlementStatisticsVO;
import com.xfgl.school.mapper.XfptEduSettlementMapper;
import com.xfgl.school.service.IXfptEduSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 教育局结算管理Service业务层处理
 *
 * @author codex
 * @date 2026-05-17
 */
@Service
public class XfptEduSettlementServiceImpl extends ServiceImpl<XfptEduSettlementMapper, XfptEduSettlement>
        implements IXfptEduSettlementService {
    @Autowired
    private XfptEduSettlementMapper xfptEduSettlementMapper;

    /**
     * 查询教育局结算管理列表
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算管理集合
     */
    @Override
    public List<XfptEduSettlement> selectXfptEduSettlementList(XfptEduSettlement xfptEduSettlement) {
        return xfptEduSettlementMapper.selectXfptEduSettlementList(xfptEduSettlement);
    }

    /**
     * 查询教育局结算统计
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算统计
     */
    @Override
    public EduSettlementStatisticsVO selectXfptEduSettlementStatistics(XfptEduSettlement xfptEduSettlement) {
        EduSettlementStatisticsVO statistics = xfptEduSettlementMapper.selectXfptEduSettlementStatistics(xfptEduSettlement);
        if (statistics == null) {
            statistics = new EduSettlementStatisticsVO();
        }
        if (statistics.getSettlementAmount() == null) {
            statistics.setSettlementAmount(BigDecimal.ZERO);
        }
        if (statistics.getTaxAmount() == null) {
            statistics.setTaxAmount(BigDecimal.ZERO);
        }
        if (statistics.getPaidAmount() == null) {
            statistics.setPaidAmount(BigDecimal.ZERO);
        }
        if (statistics.getUnpaidAmount() == null) {
            statistics.setUnpaidAmount(BigDecimal.ZERO);
        }
        if (statistics.getTotalCount() == null) {
            statistics.setTotalCount(0L);
        }
        if (statistics.getPendingCount() == null) {
            statistics.setPendingCount(0L);
        }
        if (statistics.getPartialCount() == null) {
            statistics.setPartialCount(0L);
        }
        if (statistics.getSettledCount() == null) {
            statistics.setSettledCount(0L);
        }
        return statistics;
    }
}
