package com.xfgl.school.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.enums.ReportModeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.school.domain.XfptAnnoResults;
import com.xfgl.school.domain.XfptEnterpriseMode;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.school.domain.XfptParentMode;
import com.xfgl.school.domain.XfptReportMode;
import com.xfgl.school.domain.XfptYxwfMode;
import com.xfgl.school.domain.vo.ProcurementNoticeVo;
import com.xfgl.school.mapper.XfptAnnoResultsMapper;
import com.xfgl.school.service.IXfptAnnoResultsService;
import com.xfgl.school.service.IXfptEnterpriseModeService;
import com.xfgl.school.service.IXfptOrganizationModeService;
import com.xfgl.school.service.IXfptParentModeService;
import com.xfgl.school.service.IXfptReportModeService;
import com.xfgl.school.service.IXfptYxwfModeService;
import com.xfgl.system.api.RemoteNoticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 选用组织选购结果公示报备与公开 Service 业务层
 *
 * @author yinet
 * @date 2025-04-23
 */
@Service
@Slf4j
public class XfptAnnoResultsServiceImpl extends ServiceImpl<XfptAnnoResultsMapper, XfptAnnoResults> implements IXfptAnnoResultsService {

    @Autowired
    private XfptAnnoResultsMapper xfptAnnoResultsMapper;
    @Autowired
    private IXfptReportModeService iXfptReportModeService;
    @Autowired
    private IXfptEnterpriseModeService iXfptEnterpriseModeService;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private IXfptParentModeService iXfptParentModeService;
    @Autowired
    private IXfptOrganizationModeService iXfptOrganizationModeService;
    @Autowired
    private IXfptYxwfModeService iXfptYxwfModeService;

    @Override
    public List<XfptAnnoResults> selectXfptAnnoResultsList(XfptAnnoResults xfptAnnoResults) {
        return xfptAnnoResultsMapper.selectXfptAnnoResultsList(xfptAnnoResults);
    }

    @Override
    public String resolvePurchaseMode(Long meetId) {
        if (meetId == null) {
            return null;
        }

        List<XfptReportMode> reportModes = iXfptReportModeService.lambdaQuery()
                .eq(XfptReportMode::getTeamId, meetId)
                .orderByDesc(XfptReportMode::getCreateTime)
                .list();
        if (CollUtil.isNotEmpty(reportModes)) {
            String purchaseMode = reportModes.stream()
                    .map(XfptReportMode::getPurchaseMode)
                    .filter(StrUtil::isNotBlank)
                    .findFirst()
                    .orElse(null);
            if (StrUtil.isNotBlank(purchaseMode)) {
                return purchaseMode;
            }
            log.warn("teamId={} 的 report_mode 记录存在但 purchaseMode 为空，改为按业务数据推断", meetId);
        }

        Set<String> inferredModes = new LinkedHashSet<>();
        if (hasEnterpriseMode(meetId)) {
            inferredModes.add(PurchaseType.DSFZB.getCode());
        }
        if (hasJzxjMode(meetId)) {
            inferredModes.add(PurchaseType.JZXJ.getCode());
        }
        if (hasOrganizationMode(meetId)) {
            inferredModes.add(PurchaseType.ZZXJ.getCode());
        }

        if (inferredModes.size() == 1) {
            return inferredModes.iterator().next();
        }
        if (inferredModes.size() > 1) {
            log.warn("teamId={} 存在多个可推断的选购模式：{}", meetId, inferredModes);
        }
        return null;
    }

    @Override
    public List<ProcurementNoticeVo> procurementNoticeList(Long meetId) {
        String purchaseMode = resolvePurchaseMode(meetId);
        List<ProcurementNoticeVo> procurementNoticeVoList = collectProcurementNoticeList(meetId, purchaseMode);
        if (CollUtil.isEmpty(procurementNoticeVoList) && StrUtil.isNotBlank(purchaseMode)) {
            log.warn("teamId={} 根据选购模式 {} 未查到公告来源，回退为全量来源扫描", meetId, purchaseMode);
            procurementNoticeVoList = collectProcurementNoticeList(meetId, null);
        }
        if (CollUtil.isEmpty(procurementNoticeVoList)) {
            log.warn("teamId={} 未查到任何可用的采购公告来源", meetId);
        }
        return procurementNoticeVoList;
    }

    private List<ProcurementNoticeVo> collectProcurementNoticeList(Long meetId, String purchaseMode) {
        List<ProcurementNoticeVo> procurementNoticeVoList = new ArrayList<>();
        boolean scanAllSources = StrUtil.isBlank(purchaseMode);

        if (scanAllSources || PurchaseType.DSFZB.getCode().equals(purchaseMode)) {
            appendEnterpriseNotices(meetId, procurementNoticeVoList);
        }
        if (scanAllSources || PurchaseType.JZXJ.getCode().equals(purchaseMode)) {
            appendParentNotices(meetId, procurementNoticeVoList);
            appendYxwfNotices(meetId, procurementNoticeVoList);
        }
        if (scanAllSources || PurchaseType.ZZXJ.getCode().equals(purchaseMode)) {
            appendOrganizationNotices(meetId, procurementNoticeVoList);
        }

        procurementNoticeVoList.sort(Comparator.comparing(
                ProcurementNoticeVo::getReleaseTime,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return procurementNoticeVoList;
    }

    private void appendEnterpriseNotices(Long meetId, List<ProcurementNoticeVo> procurementNoticeVoList) {
        List<XfptEnterpriseMode> enterpriseModes = iXfptEnterpriseModeService.lambdaQuery()
                .eq(XfptEnterpriseMode::getTeamId, meetId)
                .eq(XfptEnterpriseMode::getType, ReportModeStatus.XGGG.getCode())
                .list();
        enterpriseModes.forEach(item -> procurementNoticeVoList.add(
                buildProcurementNoticeVo(item.getNoticeName(), item.getId(), PurchaseType.DSFZB.getCode(), item.getReleaseTime())
        ));
    }

    private void appendParentNotices(Long meetId, List<ProcurementNoticeVo> procurementNoticeVoList) {
        List<XfptParentMode> parentModes = iXfptParentModeService.lambdaQuery()
                .eq(XfptParentMode::getTeamId, meetId)
                .list();
        parentModes.forEach(item -> procurementNoticeVoList.add(
                buildProcurementNoticeVo(item.getTitle(), item.getId(), PurchaseType.JZXJ.getCode(), item.getCreateTime())
        ));
    }

    private void appendYxwfNotices(Long meetId, List<ProcurementNoticeVo> procurementNoticeVoList) {
        List<XfptYxwfMode> yxwfModes = iXfptYxwfModeService.lambdaQuery()
                .eq(XfptYxwfMode::getTeamId, meetId)
                .list();
        yxwfModes.forEach(item -> procurementNoticeVoList.add(
                buildProcurementNoticeVo(item.getTitle(), item.getId(), PurchaseType.JZXJ.getCode(), item.getCreateTime())
        ));
    }

    private void appendOrganizationNotices(Long meetId, List<ProcurementNoticeVo> procurementNoticeVoList) {
        List<XfptOrganizationMode> organizationModes = iXfptOrganizationModeService.lambdaQuery()
                .eq(XfptOrganizationMode::getTeamId, meetId)
                .list();
        organizationModes.forEach(item -> procurementNoticeVoList.add(
                buildProcurementNoticeVo(item.getTitle(), item.getId(), PurchaseType.ZZXJ.getCode(), item.getCreateTime())
        ));
    }

    private ProcurementNoticeVo buildProcurementNoticeVo(String noticeName, Long id, String purchaseMode, Date releaseTime) {
        ProcurementNoticeVo procurementNoticeVo = new ProcurementNoticeVo();
        procurementNoticeVo.setNoticeName(noticeName);
        procurementNoticeVo.setId(id);
        procurementNoticeVo.setPurchaseMode(purchaseMode);
        procurementNoticeVo.setReleaseTime(releaseTime);
        return procurementNoticeVo;
    }

    private boolean hasEnterpriseMode(Long meetId) {
        return iXfptEnterpriseModeService.lambdaQuery()
                .eq(XfptEnterpriseMode::getTeamId, meetId)
                .eq(XfptEnterpriseMode::getType, ReportModeStatus.XGGG.getCode())
                .count() > 0;
    }

    private boolean hasJzxjMode(Long meetId) {
        return iXfptParentModeService.lambdaQuery().eq(XfptParentMode::getTeamId, meetId).count() > 0
                || iXfptYxwfModeService.lambdaQuery().eq(XfptYxwfMode::getTeamId, meetId).count() > 0;
    }

    private boolean hasOrganizationMode(Long meetId) {
        return iXfptOrganizationModeService.lambdaQuery()
                .eq(XfptOrganizationMode::getTeamId, meetId)
                .count() > 0;
    }

    @Override
    @Transactional
    public Boolean withdraw(Long id) {
        boolean update = lambdaUpdate().eq(XfptAnnoResults::getId, id).set(XfptAnnoResults::getIsRelease, NoticeStatus.CX.getCode()).update();
        if (!update) {
            log.error("修改选举公告状态错误：{}", id);
            throw new ServiceException("系统错误");
        }
        R<Boolean> booleanR = remoteNoticService.updateStatus(id, NoticeStatus.CX.getCode(), SecurityConstants.INNER);
        if (booleanR == null || booleanR.getCode() != 200) {
            log.error("修改选举公告状态错误：{}", id);
            throw new ServiceException("系统错误");
        }
        return update;
    }
}
