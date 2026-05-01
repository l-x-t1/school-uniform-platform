package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
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
import com.xfgl.school.domain.XfptSupplierList;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.school.service.IXfptSupplierListService;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.xfgl.school.domain.XfptRwggzs;
import com.xfgl.school.service.IXfptRwggzsService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 企业入围公告Controller
 *
 * @author yinet
 * @date 2025-07-22
 */
@Tag(name = "企业入围公告管理")
@RestController
@RequestMapping("/rwggzs")
@Slf4j
public class XfptRwggzsController extends BaseController {
    @Autowired
    private IXfptRwggzsService xfptRwggzsService;
    @Autowired
    private IXfptSupplierListService iXfptSupplierListService;
    @Autowired
    private RemoteDeptService remoteDeptService;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;

    /**
     * 查询企业入围公告列表
     */

    @Operation(summary = "查询企业入围公告列表")
    //@RequiresPermissions("school:rwggzs:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptRwggzs xfptRwggzs) {
        startPage();
        List<XfptRwggzs> list = xfptRwggzsService.selectXfptRwggzsList(xfptRwggzs);
        return getDataTable(list);
    }

    /**
     * 导出企业入围公告列表
     */
    @Operation(summary = "导出企业入围公告列表")
    //@RequiresPermissions("school:rwggzs:export")
    @Log(title = "企业入围公告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptRwggzs xfptRwggzs) {
        List<XfptRwggzs> list = xfptRwggzsService.selectXfptRwggzsList(xfptRwggzs);
        ExcelUtil<XfptRwggzs> util = new ExcelUtil<XfptRwggzs>(XfptRwggzs.class);
        util.exportExcel(response, list, "企业入围公告数据");
    }

    /**
     * 获取企业入围公告详细信息
     */
    @Operation(summary = "获取企业入围公告详细信息")
    //@RequiresPermissions("school:rwggzs:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        //查询供应商列表
        List<XfptSupplierList> supplierList = iXfptSupplierListService
                .list(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, id+""));
        XfptRwggzs xfptRwggzs = xfptRwggzsService.getById(id);
        xfptRwggzs.setSupplierList(supplierList);
        return success(xfptRwggzs);
    }

    /**
     * 新增企业入围公告
     */
    @Operation(summary = "新增企业入围公告")
    //@RequiresPermissions("school:rwggzs:add")
    @Log(title = "企业入围公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptRwggzs xfptRwggzs) {
        xfptRwggzs.setType("0");
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptRwggzs.setDeptId(dept.getDeptId());
        xfptRwggzs.setAncestors(dept.getAncestors());
        boolean save = xfptRwggzsService.save(xfptRwggzs);
        //插入供应商列表
        //保存供应商列表表
        List<XfptSupplierList> supplierList = xfptRwggzs.getSupplierList();
        supplierList.stream().forEach(item->{
            //item.setModeId(xfptRwggzs.getId()+"-"+ PurchaseType.ZZXJ.getCode());
            item.setModeId(xfptRwggzs.getId()+"");
            item.setAncestors(dept.getAncestors());
            item.setDeptId(dept.getDeptId());
            item.setRemark(xfptRwggzs.getTeamId()+"");
        });
        boolean b = iXfptSupplierListService.saveBatch(supplierList);
        if(!b){
            throw new ServiceException("保存供应商列表失败");
        }

        if(NoticeStatus.FB.getCode().equals(xfptRwggzs.getIsRelease())){
            XfptNotice xfptNotice = getXfptNotice(xfptRwggzs);
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
        }
        //设置时间轴
        //保存时间轴
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptRwggzs.getTeamId(), ProceConstants.RWZS + "#" + format);
        if(i==0){
            log.error("保存时间轴失败：{}",xfptRwggzs.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(save);
    }

    /**
     * 修改企业入围公告
     */
    @Operation(summary = "修改企业入围公告")
   // @RequiresPermissions("school:rwggzs:edit")
    @Log(title = "企业入围公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptRwggzs xfptRwggzs) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        if(NoticeStatus.FB.getCode().equals(xfptRwggzs.getIsRelease())){
            XfptNotice xfptNotice = getXfptNotice(xfptRwggzs);
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
        }
        //查询供应商是否有数据
        Long count = iXfptSupplierListService.lambdaQuery().eq(XfptSupplierList::getModeId,
                String.valueOf(xfptRwggzs.getId())).count();
        if(count>0){
            //删除供应商列表
            boolean remove = iXfptSupplierListService
                    .remove(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId,
                            String.valueOf(xfptRwggzs.getId())));
            if(!remove){
                throw new ServiceException("系统错误");
            }
        }
        //重新插入供应商列表
        List<XfptSupplierList> supplierList = xfptRwggzs.getSupplierList();
        supplierList.stream().forEach(item->{
            item.setId(null);
            item.setModeId(xfptRwggzs.getId()+"");
            item.setAncestors(xfptRwggzs.getAncestors());
            item.setDeptId(xfptRwggzs.getDeptId());
            item.setRemark(xfptRwggzs.getTeamId()+"");
        });
        boolean b = iXfptSupplierListService.saveBatch(supplierList);
        if(!b){
            throw new ServiceException("系统错误");
        }
        return toAjax(xfptRwggzsService.updateById(xfptRwggzs));
    }


    /**
     * 删除企业入围公告
     */
    @Operation(summary = "删除企业入围公告")
    //@RequiresPermissions("school:rwggzs:remove")
    @Log(title = "企业入围公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptRwggzsService.removeByIds(Arrays.asList(ids)));
    }


    private static XfptNotice getXfptNotice(XfptRwggzs xfptRwggzs) {
        //保存公告信息
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptRwggzs.getName());
        xfptNotice.setCreateTime(new Date());
        xfptNotice.setType(NoticeTypeStatus.RWGG.getCode());
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        //xfptNotice.setXgType(PurchaseType.ZZXJ.getCode());
        xfptNotice.setReadCount(0L);
        //xfptNotice.setFileType(xfptAnnoResults.getFileType());
        xfptNotice.setGgFile(xfptRwggzs.getFile());
        xfptNotice.setCatagory(Constants.QYRWGG);
        xfptNotice.setEnteId(xfptRwggzs.getId());
        xfptNotice.setRemark(String.valueOf(xfptRwggzs.getDeptId()));
        return xfptNotice;
    }
}
