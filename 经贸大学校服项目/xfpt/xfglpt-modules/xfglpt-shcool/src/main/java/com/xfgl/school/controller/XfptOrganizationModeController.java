package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.*;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import lombok.extern.slf4j.Slf4j;
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
 * 模式一组织确认选购模式Controller
 *
 * @author yinet
 * @date 2025-05-08
 */
@Tag(name = "模式一组织确认选购模式管理")
@RestController
@RequestMapping("/organization")
@Slf4j
public class XfptOrganizationModeController extends BaseController {
    @Autowired
    private IXfptOrganizationModeService xfptOrganizationModeService;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptQuestionnaireSurveyService iXfptQuestionnaireSurveyService;
    @Autowired
    private IXfptQuestionnaireConfigService iXfptQuestionnaireConfigService;
    @Autowired
    private IXfptSupplierListService iXfptSupplierListService;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;


    /**
     * 查询模式一组织确认选购模式列表
     */

    @Operation(summary = "查询模式一组织确认选购模式列表")
    //@RequiresPermissions("school:organization:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptOrganizationMode xfptOrganizationMode) {
        startPage();
        List<XfptOrganizationMode> list = xfptOrganizationModeService.selectXfptOrganizationModeList(xfptOrganizationMode);
        return getDataTable(list);
    }

    /**
     * 导出模式一组织确认选购模式列表
     */
    @Operation(summary = "导出模式一组织确认选购模式列表")
    //@RequiresPermissions("school:organization:export")
    @Log(title = "模式一组织确认选购模式", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptOrganizationMode xfptOrganizationMode) {
        List<XfptOrganizationMode> list = xfptOrganizationModeService.selectXfptOrganizationModeList(xfptOrganizationMode);
        ExcelUtil<XfptOrganizationMode> util = new ExcelUtil<XfptOrganizationMode>(XfptOrganizationMode.class);
        util.exportExcel(response, list, "模式一组织确认选购模式数据");
    }

    /**
     * 获取模式一组织确认选购模式详细信息
     */
    @Operation(summary = "获取模式一组织确认选购模式详细信息")
    //@RequiresPermissions("school:organization:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
//        //查询供应商列表
//        List<XfptSupplierList> supplierList = iXfptSupplierListService
//                .list(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, id+"-"+PurchaseType.ZZXJ.getCode()));
        XfptOrganizationMode xfptOrganizationMode = xfptOrganizationModeService.getById(id);
//        xfptOrganizationMode.setSupplierList(supplierList);
        return success(xfptOrganizationMode);
    }

    /**
     * 新增模式一组织确认选购模式
     */
//    @Operation(summary = "新增模式一组织确认选购模式")
//    @RequiresPermissions("school:organization:add")
//    @Log(title = "模式一组织确认选购模式", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Transactional
//    public AjaxResult add(@RequestBody XfptOrganizationMode xfptOrganizationMode) {
//        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
//        xfptOrganizationMode.setDeptId(dept.getDeptId());
//        xfptOrganizationMode.setAncestors(dept.getAncestors());
//        //判断选择模式是否是线上
//        if(StrUtil.isEmpty(xfptOrganizationMode.getType())){
//            return AjaxResult.warn("模式未选择");
//        }
//        if(OrgabizationModeType.XSTP.getCode().equals(xfptOrganizationMode.getType())){
//            //问卷模版id
//            Long quesId = xfptOrganizationMode.getQuesId();
//            XfptQuestionnaireConfig xfptQuestionnaireConfig = new XfptQuestionnaireConfig();
//            xfptQuestionnaireConfig.setTitle(xfptOrganizationMode.getTitle());
//            xfptQuestionnaireConfig.setBeginTime(xfptOrganizationMode.getStartTime());
//            xfptQuestionnaireConfig.setEndTime(xfptOrganizationMode.getEndTime());
//            xfptQuestionnaireConfig.setCategory("2");
//            xfptQuestionnaireConfig.setType(ProceConstants.ZZQDMS_TYPE);
//            xfptQuestionnaireConfig.setDeptId(dept.getDeptId());
//            xfptQuestionnaireConfig.setAncestors(dept.getAncestors());
//            xfptQuestionnaireConfig.setFbStatus(Long.valueOf(NoticeStatus.FB.getCode()));
//            boolean save = iXfptQuestionnaireConfigService.save(xfptQuestionnaireConfig);
//            if(!save){
//                throw new ServiceException("保存问卷失败");
//            }
//            //保存新增问卷的id
//            xfptOrganizationMode.setQuesId(xfptQuestionnaireConfig.getId());
//            List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = iXfptQuestionnaireSurveyService.lambdaQuery()
//                    .eq(XfptQuestionnaireSurvey::getQuestionConfigId, quesId)
//                    .list();
//            xfptQuestionnaireSurveys.stream().forEach(e -> {
//                e.setQuestionConfigId(xfptQuestionnaireConfig.getId());
//                e.setId(null);
//            });
//            boolean b = iXfptQuestionnaireSurveyService.saveBatch(xfptQuestionnaireSurveys);
//            if(!b){
//                throw new ServiceException("保存问卷题目失败");
//            }
//
//        }
//
//        boolean save = xfptOrganizationModeService.save(xfptOrganizationMode);
//        if(!save){
//            throw new ServiceException("保存失败");
//        }
//        //判断是否发布到公告页面
//        if(NoticeStatus.FB.getCode().equals(xfptOrganizationMode.getIsRelease())){
//            XfptNotice xfptNotice = getXfptNotice(xfptOrganizationMode);
//            R<Boolean> save1 = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
//            if (save1 == null || save1.getCode() != 200) {
//                log.error("远程调用保存公告失败：{}", save1.getMsg());
//                throw new ServiceException("远程调用保存公告失败");
//            }
//        }
//        //保存时间轴
//        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
//        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptOrganizationMode.getTeamId(), ProceConstants.ZZTP + "#" + format);
//        if(i==0){
//            log.error("保存时间轴失败：{}",xfptOrganizationMode.toString());
//            throw new ServiceException("系统错误");
//        }
//        return toAjax(save);
//    }
    @Autowired
    private RemoteDeptService remoteDeptService;

    @Operation(summary = "新增模式一组织确认选购模式")
    //@RequiresPermissions("school:organization:add")
    @Log(title = "模式一组织确认选购模式", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult add(@RequestBody XfptOrganizationMode xfptOrganizationMode) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptOrganizationMode.setDeptId(dept.getDeptId());
        xfptOrganizationMode.setAncestors(dept.getAncestors());
//        if(CollUtil.isEmpty( xfptOrganizationMode.getSupplierList())){
//            return AjaxResult.warn("供应商名单不能为空");
//        }

        boolean save = xfptOrganizationModeService.save(xfptOrganizationMode);
        if(!save){
            throw new ServiceException("保存失败");
        }
//        //保存供应商列表表
//        List<XfptSupplierList> supplierList = xfptOrganizationMode.getSupplierList();
//        supplierList.stream().forEach(item->{
//            item.setModeId(xfptOrganizationMode.getId()+"-"+PurchaseType.ZZXJ.getCode());
//            item.setAncestors(dept.getAncestors());
//            item.setDeptId(dept.getDeptId());
//            item.setRemark(xfptOrganizationMode.getTeamId()+"");
//        });
//        boolean b = iXfptSupplierListService.saveBatch(supplierList);
//        if(!b){
//            throw new ServiceException("保存供应商列表失败");
//        }
        //判断是否发布到公告页面
        if(NoticeStatus.FB.getCode().equals(xfptOrganizationMode.getIsRelease())){
            XfptNotice xfptNotice = getXfptNotice(xfptOrganizationMode);
            //生成30到100随机数
            int i = RandomUtil.randomInt(30, 100);
            xfptNotice.setReadCount(Long.valueOf(i));
            Long parentId = dept.getParentId();
            R<List<SysDept>> deptDataList = remoteDeptService.getDeptListByDeptId(parentId, SecurityConstants.INNER);
            if (deptDataList == null || deptDataList.getCode() != 200) {
                log.error("远程调用获取部门数据列表失败：{}", deptDataList.getMsg());
                throw new ServiceException("远程调用获取部门数据列表失败");
            }
            SysDept sysDept = deptDataList.getData().get(0);
            xfptNotice.setArea(String.valueOf(sysDept.getDeptId()));
            xfptNotice.setAreaname(sysDept.getDeptName());
            xfptNotice.setDeptname(dept.getDeptName());
            R<Boolean> save1 = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
            if (save1 == null || save1.getCode() != 200) {
                log.error("远程调用保存公告失败：{}", save1.getMsg());
                throw new ServiceException("远程调用保存公告失败");
            }
            saveProcurementNoticeProcess(xfptOrganizationMode.getTeamId());
        }
        //保存采购信息报备
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptOrganizationMode.getTeamId());
        xfptProcSummary.setSchoolName(dept.getDeptName());
        xfptProcSummary.setInviteFile(xfptOrganizationMode.getNoticeFile());
        xfptProcSummary.setDeptId(dept.getDeptId());
        xfptProcSummary.setAncestors(dept.getAncestors());
        if(StrUtil.isNotEmpty(xfptOrganizationMode.getSamplesPic())){
            xfptProcSummary.setSchoolUniformFile(xfptOrganizationMode.getSamplesPic());
        }
        Boolean b1 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b1){
            log.error("保存采购信息报备失败：{}", xfptOrganizationMode.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(save);
    }
    /**
     * 修改模式一组织确认选购模式
     */
    @Operation(summary = "修改模式一组织确认选购模式")
   // @RequiresPermissions("school:organization:edit")
    @Log(title = "模式一组织确认选购模式", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public AjaxResult edit(@RequestBody XfptOrganizationMode xfptOrganizationMode) {
        XfptOrganizationMode dbEntity = xfptOrganizationModeService.getById(xfptOrganizationMode.getId());
        String oldRelease = dbEntity == null ? null : dbEntity.getIsRelease();
//        //查询供应商是否有数据
//        Long count = iXfptSupplierListService.lambdaQuery().eq(XfptSupplierList::getModeId,
//                xfptOrganizationMode.getId() + "-" + PurchaseType.ZZXJ.getCode()).count();
//        if(count>0){
//            //删除供应商列表
//            boolean remove = iXfptSupplierListService
//                    .remove(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId,
//                            xfptOrganizationMode.getId()+"-"+PurchaseType.ZZXJ.getCode()));
//            if(!remove){
//                throw new ServiceException("系统错误");
//            }
//        }
//        //重新插入供应商列表
//        List<XfptSupplierList> supplierList = xfptOrganizationMode.getSupplierList();
//        supplierList.stream().forEach(item->{
//            item.setId(null);
//            item.setModeId(xfptOrganizationMode.getId()+"-"+PurchaseType.ZZXJ.getCode());
//            item.setAncestors(xfptOrganizationMode.getAncestors());
//            item.setDeptId(xfptOrganizationMode.getDeptId());
//            item.setRemark(xfptOrganizationMode.getTeamId()+"");
//        });
//        boolean b = iXfptSupplierListService.saveBatch(supplierList);
//        if(!b){
//            throw new ServiceException("系统错误");
//        }
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        //判断是否发布到公告页面
        if(NoticeStatus.FB.getCode().equals(xfptOrganizationMode.getIsRelease()) &&
                !NoticeStatus.FB.getCode().equals(oldRelease)){
            XfptNotice xfptNotice = getXfptNotice(xfptOrganizationMode);
            Long parentId = dept.getParentId();
            R<List<SysDept>> deptDataList = remoteDeptService.getDeptListByDeptId(parentId, SecurityConstants.INNER);
            if (deptDataList == null || deptDataList.getCode() != 200) {
                log.error("远程调用获取部门数据列表失败：{}", deptDataList.getMsg());
                throw new ServiceException("远程调用获取部门数据列表失败");
            }
            SysDept sysDept = deptDataList.getData().get(0);
            xfptNotice.setArea(String.valueOf(sysDept.getDeptId()));
            xfptNotice.setAreaname(sysDept.getDeptName());
            xfptNotice.setDeptname(dept.getDeptName());
            R<Boolean> save1 = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
            if (save1 == null || save1.getCode() != 200) {
                log.error("远程调用保存公告失败：{}", save1.getMsg());
                throw new ServiceException("远程调用保存公告失败");
            }
            saveProcurementNoticeProcess(xfptOrganizationMode.getTeamId());
        } else if (dbEntity != null && StrUtil.isNotEmpty(xfptOrganizationMode.getIsRelease())
                && NoticeStatus.FB.getCode().equals(oldRelease)) {
            remoteNoticService.updateStatus(xfptOrganizationMode.getId(),xfptOrganizationMode.getIsRelease(), SecurityConstants.INNER);
        }
        //保存采购信息报备
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptOrganizationMode.getTeamId());
        xfptProcSummary.setSchoolName(SecurityUtils.getLoginUser().getSysUser().getDept().getDeptName());
        xfptProcSummary.setInviteFile(xfptOrganizationMode.getNoticeFile());
        if(StrUtil.isNotEmpty(xfptOrganizationMode.getSamplesPic())){
            xfptProcSummary.setSchoolUniformFile(xfptOrganizationMode.getSamplesPic());
        }
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b2){
            log.error("保存采购信息报备失败：{}", xfptOrganizationMode.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(xfptOrganizationModeService.updateById(xfptOrganizationMode));
    }

    private void saveProcurementNoticeProcess(Long teamId) {
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(teamId, ProceConstants.CGGGFB + "#" + format);
        if(i==0){
            throw new ServiceException("保存流程状态失败");
        }
    }

    /**
     * 撤回公告
     *
     * @param id
     * @return
     */
    @PostMapping("/withdraw")
    @Operation(summary = "撤回公告")
    @Log(title = "撤回公告", businessType = BusinessType.UPDATE)
    public AjaxResult withdraw(Long id,String isRelease) {
        return toAjax(xfptOrganizationModeService.withdraw(id,isRelease));
    }

    /**
     * 删除模式一组织确认选购模式
     */
    @Operation(summary = "删除模式一组织确认选购模式")
    //@RequiresPermissions("school:organization:remove")
    @Log(title = "模式一组织确认选购模式", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Transactional
    public AjaxResult remove(@PathVariable Long[] ids) {

        List<XfptOrganizationMode> list = xfptOrganizationModeService.lambdaQuery().in(XfptOrganizationMode::getId, ids).list();
//        //删除公告
//        for (XfptOrganizationMode xfptOrganizationMode : list) {
//            R<Boolean> booleanR = remoteNoticService.removeNotice(xfptOrganizationMode.getId(), SecurityConstants.INNER);
//            if (booleanR == null || booleanR.getCode() != 200) {
//                log.error("远程调用删除公告失败：{}", booleanR.getMsg());
//                throw new ServiceException("远程调用删除公告失败");
//            }
//            //删除供应商列表
//            boolean remove = iXfptSupplierListService
//                    .remove(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId,
//                            xfptOrganizationMode.getId()+"-"+PurchaseType.ZZXJ.getCode()));
//            if (!remove) {
//                log.error("删除供应商列表失败：{}", xfptOrganizationMode.toString());
//                throw new ServiceException("系统错误");
//            }
//        }

        boolean b = xfptOrganizationModeService.removeByIds(Arrays.asList(ids));
        if (!b) {
            throw new ServiceException("系统错误");
        }
        return toAjax(b);
    }

    private static XfptNotice getXfptNotice(XfptOrganizationMode xfptOrganizationMode) {
        //保存公告信息
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptOrganizationMode.getTitle());
        xfptNotice.setCreateTime(new Date());
        xfptNotice.setType(NoticeTypeStatus.CGGG.getCode());
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        xfptNotice.setXgType(PurchaseType.ZZXJ.getCode());
        xfptNotice.setReadCount(0L);
        //xfptNotice.setFileType(xfptAnnoResults.getFileType());
        xfptNotice.setGgFile(xfptOrganizationMode.getNoticeFile());
        xfptNotice.setFile(xfptOrganizationMode.getOtherFile());
        xfptNotice.setCatagory(Constants.CGGG);
        if(YesOrNoStatus.YES.getCode().equals(xfptOrganizationMode.getIsSamplesPic())){
            xfptNotice.setSamplesPic(xfptOrganizationMode.getSamplesPic());
        }
        xfptNotice.setEnteId(xfptOrganizationMode.getId());
        xfptNotice.setRemark(String.valueOf(xfptOrganizationMode.getDeptId()));
        return xfptNotice;
    }

}
