package com.xfgl.school.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptEduSettlement;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.EduSettlementStatisticsVO;
import com.xfgl.school.mapper.XfptEduSettlementMapper;
import com.xfgl.school.service.IXfptEduSettlementService;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;
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
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;
    @Autowired
    private IXfptSchoolSupplierRelService schoolSupplierRelService;

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
        XfptEduSettlement settlement = xfptEduSettlementMapper.selectXfptEduSettlementById(id);
        return requireAccessibleSettlement(settlement);
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
