package com.xfgl.school.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.TeamMeetingSon;
import com.xfgl.school.domain.XfptAnnoResults;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.vo.ProcurementNoticeVo;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.school.service.ITeamMeetingSonService;
import com.xfgl.school.service.IXfptAnnoResultsService;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 选用组织选购结果公示报备与公开 Controller
 *
 * @author yinet
 * @date 2025-04-23
 */
@Tag(name = "选用组织选购结果公示报备与公开管理")
@RestController
@RequestMapping("/results")
@Slf4j
public class XfptAnnoResultsController extends BaseController {
    @Autowired
    private IXfptAnnoResultsService xfptAnnoResultsService;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private ITeamMeetingSonService teamMeetingSonServiceImpl;
    @Autowired
    private RemoteDeptService remoteDeptService;

    @Operation(summary = "查询选用组织选购结果公示报备与公开列表")
    @GetMapping("/list")
    public TableDataInfo list(XfptAnnoResults xfptAnnoResults) {
        startPage();
        xfptAnnoResults.setDeptId(null);
        List<XfptAnnoResults> list = xfptAnnoResultsService.selectXfptAnnoResultsList(xfptAnnoResults);
        if (CollUtil.isEmpty(list)) {
            return getDataTable(list);
        }
        fillReportMode(list);
        return getDataTable(list);
    }

    private static String getMenu(String purchaseMode) {
        if (StrUtil.isBlank(purchaseMode)) {
            return "";
        }
        PurchaseType[] values = PurchaseType.values();
        for (PurchaseType value : values) {
            if (purchaseMode.equals(value.getCode())) {
                return value.getInfo();
            }
        }
        return "";
    }

    @Operation(summary = "导出选用组织选购结果公示报备与公开列表")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptAnnoResults xfptAnnoResults) {
        List<XfptAnnoResults> list = xfptAnnoResultsService.selectXfptAnnoResultsList(xfptAnnoResults);
        fillReportMode(list);
        ExcelUtil<XfptAnnoResults> util = new ExcelUtil<>(XfptAnnoResults.class);
        util.exportExcel(response, list, "选用组织选购结果公示报备与公开数据");
    }

    @Operation(summary = "获取选用组织选购结果公示报备与公开详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptAnnoResults byId = xfptAnnoResultsService.getById(id);
        List<TeamMeetingSon> sonDataList = teamMeetingSonServiceImpl.selectTeamMeetingSonList(
                TeamMeetingSon.builder().teamMeetingId(byId.getTeamId()).build()
        );
        byId.setSonDataList(sonDataList);
        return success(byId);
    }

    @Operation(summary = "新增选用组织选购结果公示报备与公开")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult add(@RequestBody XfptAnnoResults xfptAnnoResults) {
        if (StrUtil.isBlank(xfptAnnoResults.getStageType())) {
            xfptAnnoResults.setStageType("1");
        }
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptAnnoResults.setAncestors(dept.getAncestors());
        xfptAnnoResults.setDeptId(dept.getDeptId());
        if (StrUtil.isBlank(xfptAnnoResults.getIsRelease())) {
            xfptAnnoResults.setIsRelease(NoticeStatus.WFB.getCode());
        }
        boolean saved = xfptAnnoResultsService.save(xfptAnnoResults);
        if (!saved) {
            log.error("保存选用组织选购结果公示报备与公开失败：{}", xfptAnnoResults);
            throw new ServiceException("保存失败");
        }
        if (NoticeStatus.FB.getCode().equals(xfptAnnoResults.getIsRelease())) {
            publishResultNotice(xfptAnnoResults, dept);
        }

        XfptProcSummary xfptProcSummary = new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptAnnoResults.getTeamId());
        xfptProcSummary.setWinBidFile(xfptAnnoResults.getNoticeFile());
        xfptProcSummary.setEnterprise(xfptAnnoResults.getTransactionCompanyName());
        xfptProcSummary.setBuyTime(xfptAnnoResults.getCgsj());
        Boolean summarySaved = iXfptProcSummaryService.setDate(xfptProcSummary);
        if (!summarySaved) {
            log.error("保存采购信息报备失败：{}", xfptAnnoResults);
            throw new ServiceException("系统错误");
        }
        return toAjax(saved);
    }

    private static XfptNotice getXfptNotice(XfptAnnoResults xfptAnnoResults, String purchaseMode, SysDept sysDept) {
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptAnnoResults.getNoticeTitle());
        xfptNotice.setCreateTime(new Date());
        xfptNotice.setType("3");
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        if (StrUtil.isNotBlank(purchaseMode)) {
            xfptNotice.setXgType(purchaseMode);
        }
        xfptNotice.setGgFile(xfptAnnoResults.getNoticeFile());
        xfptNotice.setFile(xfptAnnoResults.getOtherFile());
        xfptNotice.setCatagory(Constants.CGGG);
        xfptNotice.setContent("<p>成交企业名称:" + xfptAnnoResults.getTransactionCompanyName() + "</p>");
        xfptNotice.setEnteId(xfptAnnoResults.getId());

        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptNotice.setRemark(String.valueOf(dept.getDeptId()));
        xfptNotice.setAreaname(sysDept.getDeptName());
        xfptNotice.setArea(String.valueOf(sysDept.getDeptId()));
        return xfptNotice;
    }

    @Operation(summary = "修改选用组织选购结果公示报备与公开")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public AjaxResult edit(@RequestBody XfptAnnoResults xfptAnnoResults) {
        if (StrUtil.isBlank(xfptAnnoResults.getStageType())) {
            xfptAnnoResults.setStageType("1");
        }
        XfptAnnoResults dbEntity = xfptAnnoResultsService.getById(xfptAnnoResults.getId());
        String oldRelease = dbEntity == null ? null : dbEntity.getIsRelease();

        XfptProcSummary xfptProcSummary = new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptAnnoResults.getTeamId());
        xfptProcSummary.setWinBidFile(xfptAnnoResults.getAnnouncementFile());
        xfptProcSummary.setEnterprise(xfptAnnoResults.getTransactionCompanyName());
        xfptProcSummary.setBuyTime(xfptAnnoResults.getCgsj());
        Boolean summarySaved = iXfptProcSummaryService.setDate(xfptProcSummary);
        if (!summarySaved) {
            log.error("保存采购信息报备失败：{}", xfptAnnoResults);
            throw new ServiceException("系统错误");
        }

        boolean updated = xfptAnnoResultsService.updateById(xfptAnnoResults);
        if (!updated) {
            return toAjax(false);
        }
        if (NoticeStatus.FB.getCode().equals(xfptAnnoResults.getIsRelease())
                && !NoticeStatus.FB.getCode().equals(oldRelease)) {
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            publishResultNotice(xfptAnnoResultsService.getById(xfptAnnoResults.getId()), dept);
        } else if (StrUtil.isNotBlank(oldRelease) && !oldRelease.equals(xfptAnnoResults.getIsRelease())) {
            remoteNoticService.updateStatus(xfptAnnoResults.getId(), xfptAnnoResults.getIsRelease(), SecurityConstants.INNER);
        }
        return toAjax(true);
    }

    @Operation(summary = "删除选用组织选购结果公示报备与公开")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptAnnoResultsService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("/procurementNoticeList")
    @Operation(summary = "查询选购公告列表")
    public AjaxResult procurementNoticeList(Long meetId) {
        List<ProcurementNoticeVo> list = xfptAnnoResultsService.procurementNoticeList(meetId);
        return AjaxResult.success(list);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "撤回公告")
    @Log(title = "撤回公告", businessType = BusinessType.UPDATE)
    public AjaxResult withdraw(Long id) {
        return toAjax(xfptAnnoResultsService.withdraw(id));
    }

    private void fillReportMode(List<XfptAnnoResults> list) {
        Map<Long, String> purchaseModeMap = new HashMap<>();
        list.stream()
                .map(XfptAnnoResults::getTeamId)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(teamId -> purchaseModeMap.put(teamId, xfptAnnoResultsService.resolvePurchaseMode(teamId)));
        list.forEach(item -> item.setReportMode(getMenu(purchaseModeMap.get(item.getTeamId()))));
    }

    private void publishResultNotice(XfptAnnoResults xfptAnnoResults, SysDept dept) {
        Long parentId = dept.getParentId();
        R<List<SysDept>> deptDataList = remoteDeptService.getDeptListByDeptId(parentId, SecurityConstants.INNER);
        if (deptDataList == null || deptDataList.getCode() != 200) {
            log.error("远程调用获取部门数据列表失败：{}", deptDataList == null ? "" : deptDataList.getMsg());
            throw new ServiceException("远程调用获取部门数据列表失败");
        }
        SysDept sysDept = deptDataList.getData().get(0);
        String purchaseMode = xfptAnnoResultsService.resolvePurchaseMode(xfptAnnoResults.getTeamId());
        XfptNotice xfptNotice = getXfptNotice(xfptAnnoResults, purchaseMode, sysDept);
        R<Boolean> save = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
        if (save == null || save.getCode() != 200) {
            log.error("远程调用保存公告失败：{}", save == null ? "" : save.getMsg());
            throw new ServiceException("远程调用保存公告失败");
        }
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        String processName = "2".equals(xfptAnnoResults.getStageType()) ? ProceConstants.CGJGGG : ProceConstants.TPJGGS;
        int updated = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptAnnoResults.getTeamId(), processName + "#" + format);
        if (updated == 0) {
            throw new ServiceException("保存流程状态失败");
        }
    }
}
