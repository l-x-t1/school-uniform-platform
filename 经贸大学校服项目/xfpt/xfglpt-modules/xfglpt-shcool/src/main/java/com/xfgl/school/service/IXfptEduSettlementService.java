package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptEduSettlement;
import com.xfgl.school.domain.vo.EduSettlementStatisticsVO;

/**
 * 教育局结算管理Service接口
 *
 * @author codex
 * @date 2026-05-17
 */
public interface IXfptEduSettlementService extends IService<XfptEduSettlement> {
    /**
     * 查询教育局结算管理列表
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算管理集合
     */
    public List<XfptEduSettlement> selectXfptEduSettlementList(XfptEduSettlement xfptEduSettlement);

    public XfptEduSettlement selectXfptEduSettlementById(Long id);

    /**
     * 查询教育局结算统计
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算统计
     */
    public EduSettlementStatisticsVO selectXfptEduSettlementStatistics(XfptEduSettlement xfptEduSettlement);

    /**
     * 执行学校结算，并为供应商生成结算资金流水。
     *
     * @param xfptEduSettlement 结算条件
     * @return 最新结算结果
     */
    public XfptEduSettlement executeSettlement(XfptEduSettlement xfptEduSettlement);
}
