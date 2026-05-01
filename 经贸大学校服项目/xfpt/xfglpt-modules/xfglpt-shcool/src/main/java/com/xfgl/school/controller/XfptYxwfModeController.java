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
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.NoticeTypeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.enums.YesOrNoStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.XfptSupplierList;
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
import com.xfgl.school.domain.XfptYxwfMode;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 一校为主模式（模式二）Controller
 *
 * @author yinet
 * @date 2025-05-23
 */
@Tag(name = "一校为主模式（模式二）管理")
@RestController
@RequestMapping("/yxwzmode")
@Slf4j
public class XfptYxwfModeController extends BaseController {
    @Autowired
    private IXfptYxwfModeService xfptYxwfModeService;
    @Autowired
    private IXfptSupplierListService iXfptSupplierListService;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptOrganizationModeService iXfptOrganizationModeService;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;

    /**
     * 查询一校为主模式（模式二）列表
     */

    @Operation(summary = "查询一校为主模式（模式二）列表")
    //@RequiresPermissions("school:yxwzmode:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptYxwfMode xfptYxwfMode) {
        startPage();
        List<XfptYxwfMode> list = xfptYxwfModeService.selectXfptYxwfModeList(xfptYxwfMode);
        return getDataTable(list);
    }

    /**
     * 导出一校为主模式（模式二）列表
     */
    @Operation(summary = "导出一校为主模式（模式二）列表")
    //@RequiresPermissions("school:yxwzmode:export")
    @Log(title = "一校为主模式（模式二）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptYxwfMode xfptYxwfMode) {
        List<XfptYxwfMode> list = xfptYxwfModeService.selectXfptYxwfModeList(xfptYxwfMode);
        ExcelUtil<XfptYxwfMode> util = new ExcelUtil<XfptYxwfMode>(XfptYxwfMode.class);
        util.exportExcel(response, list, "一校为主模式（模式二）数据");
    }

    /**
     * 获取一校为主模式（模式二）详细信息
     */
    @Operation(summary = "获取一校为主模式（模式二）详细信息")
    //@RequiresPermissions("school:yxwzmode:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptYxwfMode xfptYxwfMode = xfptYxwfModeService.getById(id);
        //查询供应商列表
        List<XfptSupplierList> supplierList = iXfptSupplierListService
                .list(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, id+"-"+PurchaseType.JZXJ.getCode()));

        xfptYxwfMode.setSupplierList(supplierList);
        return success(xfptYxwfMode);
    }

    /**
     * 新增一校为主模式（模式二）
     */
    @Operation(summary = "新增一校为主模式（模式二）")
   // @RequiresPermissions("school:yxwzmode:add")
    @Log(title = "一校为主模式（模式二）", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult add(@RequestBody XfptYxwfMode xfptYxwfMode) {
        Date date = new Date();
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptYxwfMode.setDeptId(dept.getDeptId());
        xfptYxwfMode.setAncestors(dept.getAncestors());
        xfptYxwfMode.setReleaseTime(new Date());
        boolean save = xfptYxwfModeService.save(xfptYxwfMode);
        if(!save){
            throw new ServiceException("保存失败");
        }
        //保存供应商列表
        List<XfptSupplierList> supplierList = xfptYxwfMode.getSupplierList();
        if(CollUtil.isEmpty(supplierList)){
            return AjaxResult.warn("请填写供应商列表");
        }
        supplierList.stream().forEach(item->{
            item.setModeId(xfptYxwfMode.getId()+"-"+PurchaseType.JZXJ.getCode());
            item.setAncestors(dept.getAncestors());
            item.setDeptId(dept.getDeptId());
            item.setRemark(xfptYxwfMode.getTeamId()+"");
        });
        boolean b = iXfptSupplierListService.saveBatch(supplierList);
        if (!b) {
            log.error("保存供应商列表失败：{}", xfptYxwfMode.toString());
            throw new ServiceException("系统错误");
        }
        //判断是否发布到公告页面
        if(NoticeStatus.FB.getCode().equals(xfptYxwfMode.getIsRelease())){
            Long parentId = dept.getParentId();
            XfptNotice xfptNotice = getXfptNotice(xfptYxwfMode);
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
        }
        //保存时间轴
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptYxwfMode.getTeamId(), ProceConstants.YXWZ + "#" + format);
        if(i==0){
            log.error("保存时间轴失败：{}",xfptYxwfMode.toString());
            throw new ServiceException("系统错误");
        }
        //保存采购信息报备
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptYxwfMode.getTeamId());
        xfptProcSummary.setSchoolName(dept.getDeptName());
        xfptProcSummary.setInviteFile(xfptYxwfMode.getNoticeFile());
        xfptProcSummary.setDeptId(dept.getDeptId());
        xfptProcSummary.setAncestors(dept.getAncestors());
        if(StrUtil.isNotEmpty(xfptYxwfMode.getSamplesPic())){
            xfptProcSummary.setSchoolUniformFile(xfptYxwfMode.getSamplesPic());
        }
        Boolean b1 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b1){
            log.error("保存采购信息报备失败：{}", xfptYxwfMode.toString());
            throw new ServiceException("系统错误");
        }

        return toAjax(save);
    }

    /**
     * 修改一校为主模式（模式二）
     */
    @Operation(summary = "修改一校为主模式（模式二）")
    //@RequiresPermissions("school:yxwzmode:edit")
    @Log(title = "一校为主模式（模式二）", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public AjaxResult edit(@RequestBody XfptYxwfMode xfptYxwfMode) {
        if(StrUtil.isNotEmpty(xfptYxwfMode.getIsRelease())){
            remoteNoticService.updateStatus(xfptYxwfMode.getId(),xfptYxwfMode.getIsRelease(), SecurityConstants.INNER);
        }
        //删除供应商列表
        boolean remove = iXfptSupplierListService.remove(new LambdaQueryWrapper<XfptSupplierList>()
                .eq(XfptSupplierList::getModeId, xfptYxwfMode.getId()+"-"+PurchaseType.JZXJ.getCode()));
        if(!remove){
            throw new ServiceException("系统错误");
        }
        //重新插入供应商列表
        List<XfptSupplierList> supplierList = xfptYxwfMode.getSupplierList();
        supplierList.stream().forEach(item->{
            item.setId(null);
            item.setModeId(xfptYxwfMode.getId()+"-"+PurchaseType.JZXJ.getCode());
            item.setAncestors(xfptYxwfMode.getAncestors());
            item.setDeptId(xfptYxwfMode.getDeptId());
            item.setRemark(xfptYxwfMode.getTeamId()+"");
        });
        boolean b = iXfptSupplierListService.saveBatch(supplierList);
        if(!b){
            throw new ServiceException("系统错误");
        }
        //保存采购信息报备
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(xfptYxwfMode.getTeamId());
        xfptProcSummary.setSchoolName(SecurityUtils.getLoginUser().getSysUser().getDept().getDeptName());
        xfptProcSummary.setInviteFile(xfptYxwfMode.getNoticeFile());
        if(StrUtil.isNotEmpty(xfptYxwfMode.getSamplesPic())){
            xfptProcSummary.setSchoolUniformFile(xfptYxwfMode.getSamplesPic());
        }
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b2){
            log.error("保存采购信息报备失败：{}", xfptYxwfMode.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(xfptYxwfModeService.updateById(xfptYxwfMode));
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
        return toAjax(xfptYxwfModeService.withdraw(id,isRelease));
    }
    /**
     * 删除一校为主模式（模式二）
     */
    @Operation(summary = "删除一校为主模式（模式二）")
    //@RequiresPermissions("school:yxwzmode:remove")
    @Log(title = "一校为主模式（模式二）", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Transactional
    public AjaxResult remove(@PathVariable Long[] ids) {

        List<XfptYxwfMode> list = xfptYxwfModeService.lambdaQuery().in(XfptYxwfMode::getId, ids).list();
        //删除公告
        for (XfptYxwfMode xfptYxwfMode : list) {
            R<Boolean> booleanR = remoteNoticService.removeNotice(xfptYxwfMode.getId(), SecurityConstants.INNER);
            if (booleanR == null || booleanR.getCode() != 200) {
                log.error("远程调用删除公告失败：{}", booleanR.getMsg());
                throw new ServiceException("远程调用删除公告失败");
            }
            //删除供应商列表
            boolean remove = iXfptSupplierListService
                    .remove(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId,
                            xfptYxwfMode.getId()+"-"+PurchaseType.JZXJ.getCode()));
            if (!remove) {
                log.error("删除供应商列表失败：{}", xfptYxwfMode.toString());
                throw new ServiceException("系统错误");
            }
        }
        return toAjax(xfptYxwfModeService.removeByIds(Arrays.asList(ids)));
    }
    @Autowired
    private RemoteDeptService remoteDeptService;
    private static XfptNotice getXfptNotice(XfptYxwfMode xfptYxwfMode) {
        //保存公告信息
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptYxwfMode.getTitle());
        xfptNotice.setCreateTime(new Date());
        xfptNotice.setType(NoticeTypeStatus.CGGG.getCode());
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        xfptNotice.setXgType(PurchaseType.JZXJ.getCode());
        xfptNotice.setReadCount(0L);
        //xfptNotice.setFileType(xfptAnnoResults.getFileType());
        xfptNotice.setGgFile(xfptYxwfMode.getNoticeFile());
        xfptNotice.setFile(xfptYxwfMode.getOtherFile());
        xfptNotice.setCatagory(Constants.CGGG);
        if(YesOrNoStatus.YES.getCode().equals(xfptYxwfMode.getIsSamplesPic())){
            xfptNotice.setSamplesPic(xfptYxwfMode.getSamplesPic());
        }
        xfptNotice.setEnteId(xfptYxwfMode.getId());
        xfptNotice.setRemark(String.valueOf(xfptYxwfMode.getDeptId()));

        return xfptNotice;
    }
}
