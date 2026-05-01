package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.NoticeTypeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.enums.ReportModeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.TeamMeetingSon;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.XfptReportMode;
import com.xfgl.school.domain.vo.ProcurementNoticeVo;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XSLFGradientPaint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptAnnoResults;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 选用组织选购结果公示报备与公开Controller
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
    private IXfptReportModeService iXfptReportModeService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private ITeamMeetingSonService teamMeetingSonServiceImpl;
    /**
     * 查询选用组织选购结果公示报备与公开列表
     */

    @Operation(summary = "查询选用组织选购结果公示报备与公开列表")
    //@RequiresPermissions("school:results:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptAnnoResults xfptAnnoResults) {
        startPage();
        xfptAnnoResults.setDeptId(null);
        List<XfptAnnoResults> list = xfptAnnoResultsService.selectXfptAnnoResultsList(xfptAnnoResults);
        if(CollUtil.isEmpty(list)){
            return getDataTable(list);
        }
        //查询成成交模式
        List<XfptReportMode> list1 = iXfptReportModeService.lambdaQuery().eq(XfptReportMode::getTeamId, list.get(0).getTeamId()).list();
        if(CollUtil.isEmpty(list1)){
            log.error("未查询teamId={}的选购模式", xfptAnnoResults.getTeamId());
            throw new ServiceException("未查询到选购模式");
        }

        //遍历枚举ReportModeStatus找到对应值
        list.stream().forEach(item->{
            String purchaseMode = list1.get(0).getPurchaseMode();
            if(StrUtil.isEmpty(purchaseMode)){
                item.setReportMode("");
            }else {
                item.setReportMode(getMenu(purchaseMode));
            }

        });
        return getDataTable(list);
    }
    /**
     * 获取枚举info
     * @param purchaseMode
     * @return
     */
    private static String getMenu(String purchaseMode) {
        PurchaseType[] values = PurchaseType.values();
        for (int i = 0; i < values.length; i++) {
            String code = values[i].getCode();
            if(purchaseMode.equals(code)){
                return values[i].getInfo();
            }
        }
        return "";
    }
    /**
     * 导出选用组织选购结果公示报备与公开列表
     */
    @Operation(summary = "导出选用组织选购结果公示报备与公开列表")
    //@RequiresPermissions("school:results:export")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptAnnoResults xfptAnnoResults) {
        List<XfptAnnoResults> list = xfptAnnoResultsService.selectXfptAnnoResultsList(xfptAnnoResults);
        ExcelUtil<XfptAnnoResults> util = new ExcelUtil<XfptAnnoResults>(XfptAnnoResults.class);
        util.exportExcel(response, list, "选用组织选购结果公示报备与公开数据");
    }

    /**
     * 获取选用组织选购结果公示报备与公开详细信息
     */
    @Operation(summary = "获取选用组织选购结果公示报备与公开详细信息")
    //@RequiresPermissions("school:results:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptAnnoResults byId = xfptAnnoResultsService.getById(id);
        List<TeamMeetingSon> sonDataList = teamMeetingSonServiceImpl.selectTeamMeetingSonList(TeamMeetingSon.builder().teamMeetingId(byId.getTeamId()).build());
        byId.setSonDataList(sonDataList);
        return success(byId);
    }

    /**
     * 新增选用组织选购结果公示报备与公开
     */
    @Operation(summary = "新增选用组织选购结果公示报备与公开")
    //@RequiresPermissions("school:results:add")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult add(@RequestBody XfptAnnoResults xfptAnnoResults) {
        //查询选购模式
        List<XfptReportMode> list = iXfptReportModeService.lambdaQuery().eq(XfptReportMode::getTeamId, xfptAnnoResults.getTeamId()).list();
        if (CollUtil.isEmpty(list)) {
            log.error("未查询teamId={}的选购模式", xfptAnnoResults.getTeamId());
            throw new ServiceException("未查询到选购模式");
        }
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptAnnoResults.setAncestors(dept.getAncestors());
        xfptAnnoResults.setDeptId(dept.getDeptId());
        xfptAnnoResults.setIsRelease(NoticeStatus.FB.getCode());
        boolean save1 = xfptAnnoResultsService.save(xfptAnnoResults);
        if (!save1) {
            log.error("保存选用组织选购结果公示报备与公开失败：{}", xfptAnnoResults.toString());
            throw new ServiceException("保存失败");
        }
        Long parentId = dept.getParentId();
        R<List<SysDept>> deptDataList = remoteDeptService.getDeptListByDeptId(parentId, SecurityConstants.INNER);
        if (deptDataList == null || deptDataList.getCode() != 200) {
            log.error("远程调用获取部门数据列表失败：{}", deptDataList.getMsg());
            throw new ServiceException("远程调用获取部门数据列表失败");
        }
        SysDept sysDept2 = deptDataList.getData().get(0);
        XfptNotice xfptNotice = getXfptNotice(xfptAnnoResults, list,sysDept2);
        R<Boolean> save = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
        if (save == null || save.getCode() != 200) {
            log.error("远程调用保存公告失败：{}", save.getMsg());
            throw new ServiceException("远程调用保存公告失败");
        }
        //保存时间轴
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptAnnoResults.getTeamId(), ProceConstants.JGGS + "#" + format);
        if (i == 0) {
            log.error("保存时间轴失败：{}", xfptAnnoResults.toString());
            throw new ServiceException("系统错误");
        }
        //保存报备信息
        //保存采购信息报备
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptAnnoResults.getTeamId());
        xfptProcSummary.setWinBidFile(xfptAnnoResults.getNoticeFile());
        xfptProcSummary.setEnterprise(xfptAnnoResults.getTransactionCompanyName());
        xfptProcSummary.setBuyTime(xfptAnnoResults.getCgsj());
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b2){
            log.error("保存采购信息报备失败：{}", xfptAnnoResults.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(save1);
    }
    @Autowired
    private RemoteDeptService remoteDeptService;
    private static XfptNotice getXfptNotice(XfptAnnoResults xfptAnnoResults, List<XfptReportMode> list,SysDept sysDept) {
        //保存公告信息
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptAnnoResults.getNoticeTitle());
        xfptNotice.setCreateTime(new Date());
        xfptNotice.setType("3");
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        xfptNotice.setXgType(list.get(0).getPurchaseMode());
        //xfptNotice.setFileType(xfptAnnoResults.getFileType());
        xfptNotice.setGgFile(xfptAnnoResults.getNoticeFile());
        xfptNotice.setFile(xfptAnnoResults.getOtherFile());
        xfptNotice.setCatagory(Constants.CGGG);
        //封装公告内容
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<p>成交企业名称:" + xfptAnnoResults.getTransactionCompanyName() + "</p>");
        //stringBuilder.append("<p>中标价格:" + xfptAnnoResults.getBidPrice() + "</p>");
        xfptNotice.setContent(stringBuilder.toString());
        xfptNotice.setEnteId(xfptAnnoResults.getId());
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptNotice.setRemark(String.valueOf(dept.getDeptId()));

        xfptNotice.setAreaname(sysDept.getDeptName());
        xfptNotice.setArea(String.valueOf(sysDept.getDeptId()));
        return xfptNotice;
    }

    /**
     * 修改选用组织选购结果公示报备与公开
     */
    @Operation(summary = "修改选用组织选购结果公示报备与公开")
   // @RequiresPermissions("school:results:edit")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptAnnoResults xfptAnnoResults) {
        //保存报备信息
        //保存采购信息报备
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptAnnoResults.getTeamId());
        xfptProcSummary.setWinBidFile(xfptAnnoResults.getAnnouncementFile());
        xfptProcSummary.setEnterprise(xfptAnnoResults.getTransactionCompanyName());
        xfptProcSummary.setBuyTime(xfptAnnoResults.getCgsj());
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b2){
            log.error("保存采购信息报备失败：{}", xfptAnnoResults.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(xfptAnnoResultsService.updateById(xfptAnnoResults));
    }


    /**
     * 删除选用组织选购结果公示报备与公开
     */
    @Operation(summary = "删除选用组织选购结果公示报备与公开")
   // @RequiresPermissions("school:results:remove")
    @Log(title = "选用组织选购结果公示报备与公开", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptAnnoResultsService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 查询选购公告列表
     *
     * @param meetId 班子id
     * @return
     */
    @GetMapping("/procurementNoticeList")
    @Operation(summary = "查询选购公告列表")
    public AjaxResult procurementNoticeList(Long meetId) {
        List<ProcurementNoticeVo> list = xfptAnnoResultsService.procurementNoticeList(meetId);
        return AjaxResult.success(list);
    }


    /**
     * 撤回公告
     * @param id
     * @return
     */
    @PostMapping("/withdraw")
    @Operation(summary = "撤回公告")
    @Log(title = "撤回公告", businessType = BusinessType.UPDATE)
    public AjaxResult withdraw(Long id){
        return toAjax(xfptAnnoResultsService.withdraw(id));
    }
}
