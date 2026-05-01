package com.xfgl.school.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.service.*;
import com.xfgl.system.api.domain.SysDept;
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
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 采购流程Controller
 *
 * @author yinet
 * @date 2025-08-06
 */
@Tag(name = "采购流程管理")
@RestController
@RequestMapping("/cglcprocess")
public class XfptPurchaseProcessController extends BaseController {
    @Autowired
    private IXfptPurchaseProcessService xfptPurchaseProcessService;
    @Autowired
    private IXfptSchoolGradeClassService xfptSchoolGradeClassService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;

    /**
     * 查询采购流程列表
     */

    @Operation(summary = "查询采购流程列表")
    //@RequiresPermissions("school:cglcprocess:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptPurchaseProcess xfptPurchaseProcess) {
        startPage();
        if (xfptPurchaseProcess.getDeptId() == null) {
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            xfptPurchaseProcess.setDeptId(dept.getDeptId());
        }
        List<XfptPurchaseProcess> list = xfptPurchaseProcessService.selectXfptPurchaseProcessList(xfptPurchaseProcess);
        for (XfptPurchaseProcess purchaseProcess : list) {
            List<TeamMeeting> list1 = iTeamMeetingService.lambdaQuery().eq(TeamMeeting::getTeamId, purchaseProcess.getId()).list();
            if (CollUtil.isNotEmpty(list1)) {
                purchaseProcess.setTeamId(list1.get(0).getId());
            }

        }

        return getDataTable(list);
    }

    /**
     * 导出采购流程列表
     */
    @Operation(summary = "导出采购流程列表")
    //@RequiresPermissions("school:cglcprocess:export")
    @Log(title = "采购流程", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptPurchaseProcess xfptPurchaseProcess) {
        List<XfptPurchaseProcess> list = xfptPurchaseProcessService.selectXfptPurchaseProcessList(xfptPurchaseProcess);
        ExcelUtil<XfptPurchaseProcess> util = new ExcelUtil<XfptPurchaseProcess>(XfptPurchaseProcess.class);
        util.exportExcel(response, list, "采购流程数据");
    }

    /**
     * 获取采购流程详细信息
     */
    @Operation(summary = "获取采购流程详细信息")
    //RequiresPermissions("school:cglcprocess:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptPurchaseProcess xfptPurchaseProcess = xfptPurchaseProcessService.getById(id);
        XfptSchoolGradeClass xfptSchoolGradeClass = new XfptSchoolGradeClass();
        xfptSchoolGradeClass.setParentId(0L);
        xfptSchoolGradeClass.setCgId(id);
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (!admin) {
            String deptId = xfptSchoolGradeClass.getDeptId();
            if (StringUtils.isBlank(deptId)) {
                Long deptId_curr = SecurityUtils.getLoginUser().getSysUser().getDeptId();
                if (deptId_curr != null) {
                    xfptSchoolGradeClass.setDeptId(deptId_curr.toString());
                }
            }
        }
        List<XfptSchoolGradeClass> list = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
        for (XfptSchoolGradeClass xfptSchoolGradeClass1 : list) {
            xfptSchoolGradeClass.setParentId(xfptSchoolGradeClass1.getId());
            List<XfptSchoolGradeClass> list1 = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
            List<Classes> classes = new ArrayList<>();
            for (XfptSchoolGradeClass gradeClass : list1) {
                Classes classes1 = new Classes();
                classes1.setId(gradeClass.getId());
                classes1.setDeptId(gradeClass.getDeptId());
                classes1.setDeptName(gradeClass.getDeptName());
                classes1.setGradeClass(gradeClass.getGradeClass());
                classes1.setStatus(gradeClass.getStatus());
                classes1.setNumber(gradeClass.getNumber());
                classes1.setParentId(gradeClass.getParentId());
                classes.add(classes1);
            }
            xfptSchoolGradeClass1.setClasses(classes);
        }
        xfptPurchaseProcess.setXfptSchoolGradeClass(list);
        //查询班子会议id
        List<TeamMeeting> list1 = iTeamMeetingService.lambdaQuery().eq(TeamMeeting::getTeamId, id).list();
        if (CollUtil.isNotEmpty(list1)) {
            xfptPurchaseProcess.setTeamId(list1.get(0).getId());
        }

        return success(xfptPurchaseProcess);
    }

    /**
     * 新增采购流程
     */
    @Operation(summary = "新增采购流程")
    //@RequiresPermissions("school:cglcprocess:add")
    @Log(title = "采购流程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptPurchaseProcess xfptPurchaseProcess) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptPurchaseProcess.setDeptId(dept.getDeptId());
        xfptPurchaseProcess.setAncestors(dept.getAncestors());
        boolean save = xfptPurchaseProcessService.save(xfptPurchaseProcess);
        List<XfptSchoolGradeClass> xfptSchoolGradeClass = xfptPurchaseProcess.getXfptSchoolGradeClass();

        String ancestors = dept.getAncestors();
        for (XfptSchoolGradeClass schoolGradeClass : xfptSchoolGradeClass) {
            List<Classes> classesList = schoolGradeClass.getClasses();
            schoolGradeClass.setAncestors(ancestors);
            schoolGradeClass.setDeptId(dept.getDeptId() + "");
            schoolGradeClass.setCgId(xfptPurchaseProcess.getId());
//        LambdaQueryWrapper<XfptSchoolGradeClass> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(XfptSchoolGradeClass::get)
            xfptSchoolGradeClassService.saveOrUpdate(schoolGradeClass);
            for (Classes classes : classesList) {
                XfptSchoolGradeClass gradeClass = new XfptSchoolGradeClass();
                gradeClass.setId(classes.getId());
                gradeClass.setDeptId(schoolGradeClass.getDeptId());
                gradeClass.setDeptName(schoolGradeClass.getDeptName());
                gradeClass.setGradeClass(classes.getGradeClass());
                gradeClass.setNumber(classes.getNumber());
                gradeClass.setParentId(schoolGradeClass.getId());
                gradeClass.setAncestors(ancestors);
                gradeClass.setStatus(classes.getStatus());
                gradeClass.setCgId(xfptPurchaseProcess.getId());
                xfptSchoolGradeClassService.saveOrUpdate(gradeClass);
            }
        }
        return toAjax(save);
    }

    /**
     * 修改采购流程
     */
    @Operation(summary = "修改采购流程")
    //@RequiresPermissions("school:cglcprocess:edit")
    @Log(title = "采购流程", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptPurchaseProcess xfptPurchaseProcess) {
        //先删除再添加
        LambdaQueryWrapper<XfptSchoolGradeClass> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XfptSchoolGradeClass::getCgId, xfptPurchaseProcess.getId());
        xfptSchoolGradeClassService.remove(lambdaQueryWrapper);

        List<XfptSchoolGradeClass> xfptSchoolGradeClass = xfptPurchaseProcess.getXfptSchoolGradeClass();
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        String ancestors = dept.getAncestors();
        for (XfptSchoolGradeClass schoolGradeClass : xfptSchoolGradeClass) {
            schoolGradeClass.setId(null);
            List<Classes> classesList = schoolGradeClass.getClasses();
            schoolGradeClass.setAncestors(ancestors);
            schoolGradeClass.setDeptId(dept.getDeptId() + "");
            schoolGradeClass.setCgId(xfptPurchaseProcess.getId());
//        LambdaQueryWrapper<XfptSchoolGradeClass> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(XfptSchoolGradeClass::get)
            xfptSchoolGradeClassService.saveOrUpdate(schoolGradeClass);
            for (Classes classes : classesList) {
                XfptSchoolGradeClass gradeClass = new XfptSchoolGradeClass();
                gradeClass.setId(null);
                gradeClass.setDeptId(schoolGradeClass.getDeptId());
                gradeClass.setDeptName(schoolGradeClass.getDeptName());
                gradeClass.setGradeClass(classes.getGradeClass());
                gradeClass.setNumber(classes.getNumber());
                gradeClass.setParentId(schoolGradeClass.getId());
                gradeClass.setAncestors(ancestors);
                gradeClass.setStatus(classes.getStatus());
                gradeClass.setCgId(xfptPurchaseProcess.getId());
                xfptSchoolGradeClassService.saveOrUpdate(gradeClass);
            }
        }

        return toAjax(xfptPurchaseProcessService.updateById(xfptPurchaseProcess));
    }


    @Autowired
    private IXfptJzBuyOpinionsService iXfptJzBuyOpinionsService;
    @Autowired
    private IXfptOrgReportDiscloseService iXfptOrgReportDiscloseService;
    @Autowired
    private IXfptReportModeService xfptReportModeService;
    @Autowired
    private IXfptOrganizationModeService iXfptOrganizationModeService;
    @Autowired
    private IXfptRwggzsService iXfptRwggzsService;
    @Autowired
    private IXfptPdxzSureSupplierService xfptPdxzSureSupplierService;
    @Autowired
    private IXfptAnnoResultsService iXfptAnnoResultsService;
    @Autowired
    private IXfptSuppliceContractService xfptSuppliceContractService;
    @Autowired
    private IXfptSuppliceInspectionReportService iXfptSuppliceInspectionReportService;
    @Autowired
    private IXfptPatriarchSatisfactionSummarizeService iXfptPatriarchSatisfactionSummarizeService;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private IXfptCglcgzzjService iXfptCglcgzzjService;
    @Autowired
    private IXfptSchoolGradeClassService iXfptSchoolGradeClassService;

    /**
     * 删除采购流程
     */
    @Operation(summary = "删除采购流程")
    //@RequiresPermissions("school:cglcprocess:remove")
    @Log(title = "采购流程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Transactional
    public AjaxResult remove(@PathVariable Long[] ids) {
        boolean b = xfptPurchaseProcessService.removeByIds(Arrays.asList(ids));

        List<TeamMeeting> list = iTeamMeetingService.lambdaQuery().in(TeamMeeting::getTeamId, ids).list();

        if (StringUtils.isNotEmpty(list)) {
            List<Long> collect = list.stream().map(teamMeeting -> teamMeeting.getId()).collect(Collectors.toList());


            LambdaQueryWrapper<TeamMeeting> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(TeamMeeting::getTeamId, ids);
            boolean remove = iTeamMeetingService.remove(queryWrapper);
            //删除家长购买意向征集管理
            LambdaQueryWrapper<XfptJzBuyOpinions> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(XfptJzBuyOpinions::getTeamMeetingId, collect);
            boolean remove1 = iXfptJzBuyOpinionsService.remove(queryWrapper1);
            //删除选用组织名单
            LambdaQueryWrapper<XfptOrgReportDisclose> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.in(XfptOrgReportDisclose::getTeamMeetingId, collect);
            boolean remove3 = iXfptOrgReportDiscloseService.remove(queryWrapper2);
            //删除组织确定选购模式
            LambdaQueryWrapper<XfptReportMode> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.in(XfptReportMode::getTeamId, collect);
            boolean remove4 = xfptReportModeService.remove(queryWrapper3);
            //删除发布公告
            LambdaQueryWrapper<XfptOrganizationMode> queryWrapper4 = new LambdaQueryWrapper<>();
            queryWrapper4.in(XfptOrganizationMode::getTeamId, collect);
            boolean remove5 = iXfptOrganizationModeService.remove(queryWrapper4);
            //删除u入围公告
            LambdaQueryWrapper<XfptRwggzs> queryWrapper5 = new LambdaQueryWrapper<>();
            queryWrapper5.in(XfptRwggzs::getTeamId, collect);
            boolean remove6 = iXfptRwggzsService.remove(queryWrapper5);
            //删除评定小组确定厂商
            LambdaQueryWrapper<XfptPdxzSureSupplier> queryWrapper6 = new LambdaQueryWrapper<>();
            queryWrapper6.in(XfptPdxzSureSupplier::getTeamId, collect);
            boolean remove7 = xfptPdxzSureSupplierService.remove(queryWrapper6);
            //删除选用组织选购结果公示报备公开
            LambdaQueryWrapper<XfptAnnoResults> queryWrapper7 = new LambdaQueryWrapper<>();
            queryWrapper7.in(XfptAnnoResults::getTeamId, collect);
            boolean remove8 = iXfptAnnoResultsService.remove(queryWrapper7);
            //删除学校采购合同报备与公开
            LambdaQueryWrapper<XfptSuppliceContract> queryWrapper8 = new LambdaQueryWrapper<>();
            queryWrapper8.in(XfptSuppliceContract::getTeamMeetingId, collect);
            boolean remove9 = xfptSuppliceContractService.remove(queryWrapper8);
            //产品质量检验报告报备与公开
            LambdaQueryWrapper<XfptSuppliceInspectionReport> queryWrapper9 = new LambdaQueryWrapper<>();
            queryWrapper9.in(XfptSuppliceInspectionReport::getTeamMeetingId, collect);
            iXfptSuppliceInspectionReportService.remove(queryWrapper9);
            //删除家长满意度总结
            LambdaQueryWrapper<XfptPatriarchSatisfactionSummarize> queryWrapper10 = new LambdaQueryWrapper<>();
            queryWrapper10.in(XfptPatriarchSatisfactionSummarize::getTeamMeetingId, collect);
            boolean remove10 = iXfptPatriarchSatisfactionSummarizeService.remove(queryWrapper10);
            //删除采购信息报备
            LambdaQueryWrapper<XfptProcSummary> queryWrapper11 = new LambdaQueryWrapper<>();
            queryWrapper11.in(XfptProcSummary::getTeamId, collect);
            boolean remove2 = iXfptProcSummaryService.remove(queryWrapper11);
            //删除班级信息
            LambdaQueryWrapper<XfptSchoolGradeClass> queryWrapper13 = new LambdaQueryWrapper<>();
            queryWrapper13.in(XfptSchoolGradeClass::getCgId, ids);
            boolean remove13 = iXfptSchoolGradeClassService.remove(queryWrapper13);
            if(!remove13){
                throw new RuntimeException("删除班级信息失败");
            }
            LambdaQueryWrapper<XfptCglcgzzj> queryWrapper12 = new LambdaQueryWrapper<>();
            queryWrapper12.in(XfptCglcgzzj::getTeamId, collect);
            boolean remove12 = iXfptCglcgzzjService.remove(queryWrapper12);
        }

        return toAjax(b);
    }
}
