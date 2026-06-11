package com.xfgl.school.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.utils.uuid.Seq;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptEduSettlement;
import com.xfgl.school.domain.XfptFinanceFlow;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.EduSettlementStatisticsVO;
import com.xfgl.school.mapper.XfptEduSettlementMapper;
import com.xfgl.school.mapper.XfptFinanceFlowMapper;
import com.xfgl.school.mapper.XfptOrderInfoMapper;
import com.xfgl.school.service.IXfptEduSettlementService;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;
    @Autowired
    private IXfptSchoolSupplierRelService schoolSupplierRelService;
    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;
    @Autowired
    private XfptFinanceFlowMapper xfptFinanceFlowMapper;

    /**
     * 查询教育局结算管理列表
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算管理集合
     */
    @Override
    public List<XfptEduSettlement> selectXfptEduSettlementList(XfptEduSettlement xfptEduSettlement) {
        if (xfptEduSettlement == null) {
            xfptEduSettlement = new XfptEduSettlement();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptEduSettlement.setDeptId(currentSchool.getDeptId());
            xfptEduSettlement.getParams().put("schoolId", currentSchool.getId());
        }
        return xfptEduSettlementMapper.selectXfptEduSettlementList(xfptEduSettlement);
    }

    @Override
    public XfptEduSettlement selectXfptEduSettlementById(Long id) {
        if (id == null) {
            return null;
        }
        XfptEduSettlement query = new XfptEduSettlement();
        query.setId(id);
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (currentSchool != null) {
            query.setDeptId(currentSchool.getDeptId());
            query.getParams().put("schoolId", currentSchool.getId());
        }
        XfptEduSettlement settlement = xfptEduSettlementMapper.selectXfptEduSettlementById(query);
        settlement = requireAccessibleSettlement(settlement);
        settlement.setOrderList(buildSettlementOrderList(settlement, currentSchool));
        return settlement;
    }

    /**
     * 查询教育局结算统计
     *
     * @param xfptEduSettlement 教育局结算管理筛选条件
     * @return 教育局结算统计
     */
    @Override
    public EduSettlementStatisticsVO selectXfptEduSettlementStatistics(XfptEduSettlement xfptEduSettlement) {
        if (xfptEduSettlement == null) {
            xfptEduSettlement = new XfptEduSettlement();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return buildEmptyStatistics();
        }
        if (currentSchool != null) {
            xfptEduSettlement.setDeptId(currentSchool.getDeptId());
            xfptEduSettlement.getParams().put("schoolId", currentSchool.getId());
        }
        EduSettlementStatisticsVO statistics = xfptEduSettlementMapper.selectXfptEduSettlementStatistics(xfptEduSettlement);
        if (statistics == null) {
            statistics = buildEmptyStatistics();
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public XfptEduSettlement executeSettlement(XfptEduSettlement xfptEduSettlement) {
        if (xfptEduSettlement == null) {
            throw new ServiceException("结算参数不能为空");
        }
        if (xfptEduSettlement.getSupplierId() == null) {
            throw new ServiceException("请选择需要结算的供应商");
        }
        if (StringUtils.isBlank(xfptEduSettlement.getSettlementCycle())) {
            throw new ServiceException("请选择结算周期");
        }

        XfptSchoolMessage currentSchool = requireCurrentSchool();
        if (!hasActiveCooperation(currentSchool.getId(), xfptEduSettlement.getSupplierId())) {
            throw new ServiceException("当前供应商不是本校合作中的供应商，无法结算");
        }

        Long totalOrderCount = xfptOrderInfoMapper.selectCount(Wrappers.<XfptOrderInfo>lambdaQuery()
                .eq(XfptOrderInfo::getSchoolId, currentSchool.getId())
                .eq(XfptOrderInfo::getSupplierId, xfptEduSettlement.getSupplierId())
                .eq(XfptOrderInfo::getDelFlag, "0")
                .in(XfptOrderInfo::getPayStatus, "1", "2", "3"));
        if (totalOrderCount == null || totalOrderCount <= 0) {
            throw new ServiceException("当前学校与该供应商暂无可结算订单");
        }

        List<XfptOrderInfo> candidateOrders = selectSettlementOrders(
                currentSchool.getId(),
                xfptEduSettlement.getSupplierId(),
                xfptEduSettlement.getSettlementCycle(),
                currentSchool.getDeptId()
        );
        if (candidateOrders.isEmpty()) {
            throw new ServiceException("当前周期没有可结算的家长订单");
        }

        List<XfptOrderInfo> pendingOrders = new ArrayList<>();
        for (XfptOrderInfo orderInfo : candidateOrders) {
            if (!"1".equals(orderInfo.getSettlementStatus())) {
                pendingOrders.add(orderInfo);
            }
        }
        if (pendingOrders.isEmpty()) {
            throw new ServiceException("当前周期订单已全部完成结算，请勿重复操作");
        }

        Date now = new Date();
        String currentUsername = SecurityUtils.getUsername();
        for (XfptOrderInfo orderInfo : pendingOrders) {
            XfptFinanceFlow financeFlow = buildSettlementFlow(orderInfo, currentSchool, xfptEduSettlement.getSettlementCycle(), now, currentUsername);
            xfptFinanceFlowMapper.insert(financeFlow);
        }

        XfptEduSettlement scopedQuery = new XfptEduSettlement();
        scopedQuery.setSupplierId(xfptEduSettlement.getSupplierId());
        scopedQuery.setSettlementCycle(xfptEduSettlement.getSettlementCycle());
        scopedQuery.setDeptId(currentSchool.getDeptId());
        scopedQuery.getParams().put("schoolId", currentSchool.getId());
        List<XfptEduSettlement> settlementList = xfptEduSettlementMapper.selectXfptEduSettlementList(scopedQuery);
        if (settlementList == null || settlementList.isEmpty()) {
            throw new ServiceException("结算完成，但未能查询到最新结算结果");
        }
        return settlementList.get(0);
    }

    private XfptEduSettlement requireAccessibleSettlement(XfptEduSettlement settlement) {
        if (settlement == null || !"0".equals(settlement.getDelFlag())) {
            throw new ServiceException("结算记录不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser()) {
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法查看结算记录");
            }
            if (!currentSchool.getDeptId().equals(settlement.getDeptId())) {
                throw new ServiceException("无权查看其他学校的结算记录");
            }
            if (settlement.getSupplierId() == null || !hasActiveCooperation(currentSchool.getId(), settlement.getSupplierId())) {
                throw new ServiceException("无权查看非本校合作厂商的结算记录");
            }
        }
        return settlement;
    }

    private boolean hasActiveCooperation(Long schoolId, Long supplierId) {
        if (schoolId == null || supplierId == null) {
            return false;
        }
        return schoolSupplierRelService.lambdaQuery()
                .eq(com.xfgl.school.domain.XfptSchoolSupplierRel::getSchoolId, schoolId)
                .eq(com.xfgl.school.domain.XfptSchoolSupplierRel::getSupplierId, supplierId)
                .eq(com.xfgl.school.domain.XfptSchoolSupplierRel::getCooperateStatus, "1")
                .eq(com.xfgl.school.domain.XfptSchoolSupplierRel::getDelFlag, "0")
                .count() > 0;
    }

    private EduSettlementStatisticsVO buildEmptyStatistics() {
        EduSettlementStatisticsVO statistics = new EduSettlementStatisticsVO();
        statistics.setSettlementAmount(BigDecimal.ZERO);
        statistics.setTaxAmount(BigDecimal.ZERO);
        statistics.setPaidAmount(BigDecimal.ZERO);
        statistics.setUnpaidAmount(BigDecimal.ZERO);
        statistics.setTotalCount(0L);
        statistics.setPendingCount(0L);
        statistics.setPartialCount(0L);
        statistics.setSettledCount(0L);
        return statistics;
    }

    private XfptFinanceFlow buildSettlementFlow(
            XfptOrderInfo orderInfo,
            XfptSchoolMessage currentSchool,
            String settlementCycle,
            Date tradeTime,
            String currentUsername
    ) {
        BigDecimal netAmount = resolveNetAmount(orderInfo);
        XfptFinanceFlow financeFlow = new XfptFinanceFlow();
        financeFlow.setFlowNo("JS" + Seq.getId());
        financeFlow.setBusinessType("4");
        financeFlow.setFlowDirection("1");
        financeFlow.setOrderId(orderInfo.getId());
        financeFlow.setOrderNo(orderInfo.getOrderNo());
        financeFlow.setOrderType(orderInfo.getOrderType());
        financeFlow.setSchoolId(orderInfo.getSchoolId());
        financeFlow.setSupplierId(orderInfo.getSupplierId());
        financeFlow.setUserId(orderInfo.getUserId());
        financeFlow.setAmount(netAmount);
        financeFlow.setPayChannel("学校结算");
        financeFlow.setTradeNo("ST" + Seq.getId());
        financeFlow.setPayeeType("2");
        financeFlow.setPayeeId(orderInfo.getSupplierId());
        financeFlow.setPayeeName(orderInfo.getSupplierName());
        financeFlow.setStatus("2");
        financeFlow.setTradeTime(tradeTime);
        financeFlow.setDeptId(currentSchool.getDeptId());
        financeFlow.setRemark(settlementCycle + "家长端订单结算");
        financeFlow.setCreateBy(currentUsername);
        financeFlow.setCreateTime(tradeTime);
        financeFlow.setUpdateBy(currentUsername);
        financeFlow.setUpdateTime(tradeTime);
        financeFlow.setDelFlag("0");
        return financeFlow;
    }

    private BigDecimal resolveNetAmount(XfptOrderInfo orderInfo) {
        if (orderInfo == null) {
            return BigDecimal.ZERO;
        }
        if (orderInfo.getSettlementNetAmount() != null) {
            return orderInfo.getSettlementNetAmount().compareTo(BigDecimal.ZERO) > 0
                    ? orderInfo.getSettlementNetAmount()
                    : BigDecimal.ZERO;
        }
        BigDecimal payAmount = orderInfo.getPayAmount() == null ? BigDecimal.ZERO : orderInfo.getPayAmount();
        BigDecimal refundAmount = orderInfo.getRefundAmount() == null ? BigDecimal.ZERO : orderInfo.getRefundAmount();
        BigDecimal netAmount = payAmount.subtract(refundAmount);
        return netAmount.compareTo(BigDecimal.ZERO) > 0 ? netAmount : BigDecimal.ZERO;
    }

    private XfptSchoolMessage requireCurrentSchool() {
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (currentSchool == null) {
            throw new ServiceException("当前登录学校信息不存在，无法执行结算");
        }
        return currentSchool;
    }

    private List<XfptOrderInfo> buildSettlementOrderList(XfptEduSettlement settlement, XfptSchoolMessage currentSchool) {
        if (settlement == null || settlement.getSupplierId() == null || StringUtils.isBlank(settlement.getSettlementCycle())) {
            return Collections.emptyList();
        }
        Long schoolId = currentSchool != null ? currentSchool.getId() : settlement.getSchoolId();
        if (schoolId == null) {
            return Collections.emptyList();
        }
        Long deptId = currentSchool != null ? currentSchool.getDeptId() : settlement.getDeptId();
        return selectSettlementOrders(schoolId, settlement.getSupplierId(), settlement.getSettlementCycle(), deptId);
    }

    private List<XfptOrderInfo> selectSettlementOrders(Long schoolId, Long supplierId, String settlementCycle, Long deptId) {
        if (schoolId == null || supplierId == null || StringUtils.isBlank(settlementCycle)) {
            return Collections.emptyList();
        }
        List<XfptOrderInfo> orderList = xfptOrderInfoMapper.selectSettlementOrderList(schoolId, supplierId, settlementCycle, deptId);
        return orderList == null ? Collections.emptyList() : orderList;
    }

    private XfptSchoolMessage getCurrentSchoolIfScoped() {
        SysUser currentUser = getCurrentUser();
        if (!schoolAccessSupport.isSchoolUser(currentUser)) {
            return null;
        }
        return schoolAccessSupport.resolveCurrentSchool(currentUser);
    }

    private boolean isSchoolScopedUser() {
        return schoolAccessSupport.isSchoolUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
    }

    private SysUser getCurrentUser() {
        return SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getSysUser();
    }
}
