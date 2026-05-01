package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
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
 * 选用组织委托招标机构确定企业模式Controller
 *
 * @author yinet
 * @date 2025-04-23
 */
@Tag(name = "选用组织委托招标机构确定企业模式管理")
@RestController
@RequestMapping("/mode")
@Slf4j
public class XfptEnterpriseModeController extends BaseController {
    @Autowired
    private IXfptEnterpriseModeService xfptEnterpriseModeService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private IXfptReportModeService iXfptReportModeService;
    @Autowired
    private IXfptSupplierListService iXfptSupplierListService;
    @Autowired
    private RemoteDeptService remoteDeptService;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;


    /**
     * 查询选用组织委托招标机构确定企业模式列表
     */

    @Operation(summary = "查询选用组织委托招标机构确定企业模式列表")
    //@RequiresPermissions("school:mode:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptEnterpriseMode xfptEnterpriseMode) {
        startPage();
        xfptEnterpriseMode.setDeptId(null);
        List<XfptEnterpriseMode> list = xfptEnterpriseModeService.selectXfptEnterpriseModeList(xfptEnterpriseMode);
        return getDataTable(list);
    }


    /**
     * 导出选用组织委托招标机构确定企业模式列表
     */
    @Operation(summary = "导出选用组织委托招标机构确定企业模式列表")
    // @RequiresPermissions("school:mode:export")
    @Log(title = "选用组织委托招标机构确定企业模式", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptEnterpriseMode xfptEnterpriseMode) {
        List<XfptEnterpriseMode> list = xfptEnterpriseModeService.selectXfptEnterpriseModeList(xfptEnterpriseMode);
        ExcelUtil<XfptEnterpriseMode> util = new ExcelUtil<XfptEnterpriseMode>(XfptEnterpriseMode.class);
        util.exportExcel(response, list, "选用组织委托招标机构确定企业模式数据");
    }

    /**
     * 获取选用组织委托招标机构确定企业模式详细信息
     */
    @Operation(summary = "获取选用组织委托招标机构确定企业模式详细信息")
    //@RequiresPermissions("school:mode:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
//        //查询供应商列表
//        List<XfptSupplierList> xfptSupplierLists = iXfptSupplierListService
//                .list(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, id+"-"+PurchaseType.DSFZB.getCode()));
        XfptEnterpriseMode xfptEnterpriseMode = xfptEnterpriseModeService.getById(id);
//        xfptEnterpriseMode.setSupplierList(xfptSupplierLists);
        return success(xfptEnterpriseMode);
    }

    /**
     * 新增选用组织委托招标机构确定企业模式
     */
//    @Operation(summary = "新增选用组织委托招标机构确定企业模式")
//    //@RequiresPermissions("school:mode:add")
//    @Log(title = "选用组织委托招标机构确定企业模式", businessType = BusinessType.INSERT)
//    @PostMapping
//    @Transactional
//    public AjaxResult add(@RequestBody XfptEnterpriseMode xfptEnterpriseMode) {
//
//        Date date = new Date();
//        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
//        xfptEnterpriseMode.setAncestors(dept.getAncestors());
//        xfptEnterpriseMode.setDeptId(dept.getDeptId());
//        xfptEnterpriseMode.setReleaseTime(date);
//        xfptEnterpriseMode.setLookStatus(LookStatus.YSE.getCode());
//        boolean save1 = xfptEnterpriseModeService.save(xfptEnterpriseMode);
//        if(!save1){
//            log.error("保存选用组织委托招标机构确定企业模式失败：{}",xfptEnterpriseMode.toString());
//        }
//
//        XfptNotice xfptNotice=new XfptNotice();
//        //招标合同类型，可以选择是否发布到公告，如果选择发布，则发布状态就是已发布，选择不发布就是未发布，发布以后以后可以撤回公告
//        if(ReportModeStatus.XGGG.getCode().equals(xfptEnterpriseMode.getType())){
//            xfptNotice.setTitle(xfptEnterpriseMode.getNoticeName());
//            xfptNotice.setCreateTime(date);
//            xfptNotice.setType(NoticeTypeStatus.CGGG.getCode());
//            xfptNotice.setCatagory(Constants.CGGG);
//            //判断是否发布到公告
//            if(NoticeStatus.FB.getCode().equals(xfptEnterpriseMode.getIsRelease())){
//                //设置公告发布状态
//                xfptNotice.setStatus(NoticeStatus.FB.getCode());
//                //设置已发布时间
//                xfptEnterpriseMode.setReleaseTime(date);
//            }else {
//                xfptNotice.setStatus(NoticeStatus.WFB.getCode());
//            }
//            //是否提供校服图
//            if(YesOrNoStatus.YES.getCode().equals(xfptEnterpriseMode.getIsSamplesPic())){
//                xfptNotice.setSamplesPic(xfptEnterpriseMode.getSamplesPic());
//            }
//            //选举方式
//            xfptNotice.setXgType(PurchaseType.DSFZB.getCode());
//            xfptNotice.setEnteId(xfptEnterpriseMode.getId());
//            //采购公告文件
//            xfptNotice.setGgFile(xfptEnterpriseMode.getNoticeFile());
//            //文件类型
//            //xfptNotice.setFileType(xfptEnterpriseMode.getFileType());
//            if(StrUtil.isNotEmpty(xfptEnterpriseMode.getSamplesPic())){
//                xfptNotice.setSamplesPic(xfptEnterpriseMode.getSamplesPic());
//            }
//            xfptNotice.setCreateBy(SecurityUtils.getLoginUser().getSysUser().getNickName());
//            xfptNotice.setRemark(String.valueOf(dept.getDeptId()));
//        }
//        R<Boolean> save = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
//        if(save==null || save.getCode()!=200){
//            log.error("远程调用保存公告失败：{}",save.getMsg());
//            throw new ServiceException("远程调用保存公告失败");
//        }
//        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
//        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptEnterpriseMode.getTeamId(), ProceConstants.DSFZB + "#" + format);
//        if(i==0){
//            log.error("保存时间轴失败：{}",xfptEnterpriseMode.toString());
//            throw new ServiceException("系统错误");
//        }
//        return toAjax(save1);
//    }
    @Operation(summary = "新增选用组织委托招标机构确定企业模式")
    //@RequiresPermissions("school:mode:add")
    @Log(title = "选用组织委托招标机构确定企业模式", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult add(@RequestBody XfptEnterpriseMode xfptEnterpriseMode) {

        Date date = new Date();
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptEnterpriseMode.setAncestors(dept.getAncestors());
        xfptEnterpriseMode.setDeptId(dept.getDeptId());
        xfptEnterpriseMode.setReleaseTime(date);
        boolean save1 = xfptEnterpriseModeService.save(xfptEnterpriseMode);

        if (!save1) {
            log.error("保存选用组织委托招标机构确定企业模式失败：{}", xfptEnterpriseMode.toString());
            throw new ServiceException("系统错误");
        }
//        //保存供应商列表
//        List<XfptSupplierList> supplierList = xfptEnterpriseMode.getSupplierList();
//        if(CollUtil.isEmpty(supplierList)){
//            return AjaxResult.warn("请填写供应商列表");
//        }
//        supplierList.stream().forEach(item->{
//            item.setModeId(xfptEnterpriseMode.getId()+"-"+PurchaseType.DSFZB.getCode());
//            item.setAncestors(dept.getAncestors());
//            item.setDeptId(dept.getDeptId());
//            item.setRemark(xfptEnterpriseMode.getTeamId()+"");
//        });
//        boolean b = iXfptSupplierListService.saveBatch(supplierList);
//        if (!b) {
//            log.error("保存供应商列表失败：{}", xfptEnterpriseMode.toString());
//            throw new ServiceException("系统错误");
//        }

        XfptNotice xfptNotice = new XfptNotice();
        //招标合同类型，可以选择是否发布到公告，如果选择发布，则发布状态就是已发布，选择不发布就是未发布，发布以后以后可以撤回公告
        xfptNotice.setTitle(xfptEnterpriseMode.getNoticeName());
        xfptNotice.setCreateTime(date);
        xfptNotice.setType(NoticeTypeStatus.CGGG.getCode());
        xfptNotice.setCatagory(Constants.CGGG);
        //判断是否发布到公告
        if (NoticeStatus.FB.getCode().equals(xfptEnterpriseMode.getIsRelease())) {
            //设置公告发布状态
            xfptNotice.setStatus(NoticeStatus.FB.getCode());
            //设置已发布时间
            xfptEnterpriseMode.setReleaseTime(date);
        } else {
            xfptNotice.setStatus(NoticeStatus.WFB.getCode());
        }
        //是否提供校服图
        if (YesOrNoStatus.YES.getCode().equals(xfptEnterpriseMode.getIsSamplesPic())) {
            xfptNotice.setSamplesPic(xfptEnterpriseMode.getSamplesPic());
        }
        //选举方式
        xfptNotice.setXgType(PurchaseType.DSFZB.getCode());
        xfptNotice.setEnteId(xfptEnterpriseMode.getId());
        //采购公告文件
        xfptNotice.setGgFile(xfptEnterpriseMode.getNoticeFile());
        //文件类型
        //xfptNotice.setFileType(xfptEnterpriseMode.getFileType());
        if (StrUtil.isNotEmpty(xfptEnterpriseMode.getSamplesPic())) {
            xfptNotice.setSamplesPic(xfptEnterpriseMode.getSamplesPic());
        }
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
        xfptNotice.setCreateBy(SecurityUtils.getLoginUser().getSysUser().getNickName());
        xfptNotice.setRemark(String.valueOf(dept.getDeptId()));
        R<Boolean> save = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
        if (save == null || save.getCode() != 200) {
            log.error("远程调用保存公告失败：{}", save.getMsg());
            throw new ServiceException("远程调用保存公告失败");
        }
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptEnterpriseMode.getTeamId(), ProceConstants.DSFZB + "#" + format);
        if (i == 0) {
            log.error("保存时间轴失败：{}", xfptEnterpriseMode.toString());
            throw new ServiceException("系统错误");
        }
        //保存采购信息报备
        XfptProcSummary xfptProcSummary = new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptEnterpriseMode.getTeamId());
        xfptProcSummary.setSchoolName(dept.getDeptName());
        xfptProcSummary.setInviteFile(xfptEnterpriseMode.getNoticeFile());
        xfptProcSummary.setDeptId(dept.getDeptId());
        xfptProcSummary.setAncestors(dept.getAncestors());
        if (StrUtil.isNotEmpty(xfptEnterpriseMode.getSamplesPic())) {
            xfptProcSummary.setSchoolUniformFile(xfptEnterpriseMode.getSamplesPic());
        }
        Boolean b1 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if (!b1) {
            log.error("保存采购信息报备失败：{}", xfptEnterpriseMode.toString());
            throw new ServiceException("系统错误");
        }

        return toAjax(save1);
    }

    /**
     * 修改选用组织委托招标机构确定企业模式
     */
    @Operation(summary = "修改选用组织委托招标机构确定企业模式")
    //@RequiresPermissions("school:mode:edit")
    @Log(title = "选用组织委托招标机构确定企业模式", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public AjaxResult edit(@RequestBody XfptEnterpriseMode xfptEnterpriseMode) {
        if (StrUtil.isNotEmpty(xfptEnterpriseMode.getIsRelease())) {
            remoteNoticService.updateStatus(xfptEnterpriseMode.getId(), xfptEnterpriseMode.getIsRelease(), SecurityConstants.INNER);
        }
        boolean b1 = xfptEnterpriseModeService.updateById(xfptEnterpriseMode);
//        //删除供应商列表
//        iXfptSupplierListService.remove(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, xfptEnterpriseMode.getId()+"-"+PurchaseType.DSFZB.getCode()));
//        //重新添加供应商列表
//        List<XfptSupplierList> supplierList = xfptEnterpriseMode.getSupplierList();
//        if(CollUtil.isEmpty(supplierList)){
//            log.error("供应商列表未添加");
//            return AjaxResult.error("系统错误");
//        }
//        supplierList.stream().forEach(item->{
//            item.setId(null);
//            item.setModeId(xfptEnterpriseMode.getId()+"-"+PurchaseType.DSFZB.getCode());
//            item.setAncestors(xfptEnterpriseMode.getAncestors());
//            item.setDeptId(xfptEnterpriseMode.getDeptId());
//            item.setRemark(xfptEnterpriseMode.getTeamId()+"");
//        });
//        boolean b = iXfptSupplierListService.saveBatch(supplierList);
//        if (!b) {
//            log.error("保存供应商列表失败：{}", xfptEnterpriseMode.toString());
//            throw new ServiceException("系统错误");
//        }
        if (NoticeStatus.FB.getCode().equals(xfptEnterpriseMode.getIsRelease())) {
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            XfptNotice xfptNotice = new XfptNotice();
            //招标合同类型，可以选择是否发布到公告，如果选择发布，则发布状态就是已发布，选择不发布就是未发布，发布以后以后可以撤回公告
            xfptNotice.setTitle(xfptEnterpriseMode.getNoticeName());
            xfptNotice.setType(NoticeTypeStatus.CGGG.getCode());
            xfptNotice.setCatagory(Constants.CGGG);
            //判断是否发布到公告

            //设置公告发布状态
            xfptNotice.setStatus(NoticeStatus.FB.getCode());
            //设置已发布时间
            xfptEnterpriseMode.setReleaseTime(new Date());

            //选举方式
            xfptNotice.setXgType(PurchaseType.DSFZB.getCode());
            xfptNotice.setEnteId(xfptEnterpriseMode.getId());
            //采购公告文件
            xfptNotice.setGgFile(xfptEnterpriseMode.getNoticeFile());
            //文件类型
            //xfptNotice.setFileType(xfptEnterpriseMode.getFileType());
            if (StrUtil.isNotEmpty(xfptEnterpriseMode.getSamplesPic())) {
                xfptNotice.setSamplesPic(xfptEnterpriseMode.getSamplesPic());
            }
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
            xfptNotice.setCreateBy(SecurityUtils.getLoginUser().getSysUser().getNickName());
            xfptNotice.setRemark(String.valueOf(dept.getDeptId()));
            R<Boolean> save = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
            if (save == null || save.getCode() != 200) {
                log.error("远程调用保存公告失败：{}", save.getMsg());
                throw new ServiceException("远程调用保存公告失败");
            }
        }

        //保存采购信息报备
        XfptProcSummary xfptProcSummary = new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptEnterpriseMode.getTeamId());
        xfptProcSummary.setSchoolName(SecurityUtils.getLoginUser().getSysUser().getDept().getDeptName());
        xfptProcSummary.setInviteFile(xfptEnterpriseMode.getNoticeFile());
        if (StrUtil.isNotEmpty(xfptEnterpriseMode.getSamplesPic())) {
            xfptProcSummary.setSchoolUniformFile(xfptEnterpriseMode.getSamplesPic());
        }
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if (!b2) {
            log.error("保存采购信息报备失败：{}", xfptEnterpriseMode.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(b2);
    }


    /**
     * 删除选用组织委托招标机构确定企业模式
     */
    @Operation(summary = "删除选用组织委托招标机构确定企业模式")
    //@RequiresPermissions("school:mode:remove")
    @Log(title = "选用组织委托招标机构确定企业模式", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        // List<XfptEnterpriseMode> list = xfptEnterpriseModeService.lambdaQuery().in(XfptEnterpriseMode::getId, ids).list();
//        //删除公告
//        for (XfptEnterpriseMode xfptEnterpriseMode : list) {
//            R<Boolean> booleanR = remoteNoticService.removeNotice(xfptEnterpriseMode.getId(), SecurityConstants.INNER);
//            if (booleanR == null || booleanR.getCode() != 200) {
//                log.error("远程调用删除公告失败：{}", booleanR.getMsg());
//                throw new ServiceException("远程调用删除公告失败");
//            }
//            //删除供应商列表
//            boolean remove = iXfptSupplierListService
//                    .remove(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId,
//                            xfptEnterpriseMode.getId()+"-"+PurchaseType.DSFZB.getCode()));
//            if (!remove) {
//                log.error("删除供应商列表失败：{}", xfptEnterpriseMode.toString());
//                throw new ServiceException("系统错误");
//            }
//        }
        return toAjax(xfptEnterpriseModeService.removeByIds(Arrays.asList(ids)));
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
    public AjaxResult withdraw(Long id, String isRelease) {
        return toAjax(xfptEnterpriseModeService.withdraw(id, isRelease));
    }

    /**
     * 删除公告
     *
     * @param id
     * @return
     */
    @PostMapping("/removeNotice/{id}")
    public AjaxResult removeNotice(Long id) {
        return toAjax(xfptEnterpriseModeService.removeNotice(id));
    }
}
