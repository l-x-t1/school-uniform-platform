package com.xfgl.school.controller;

import cn.hutool.core.date.DateUtil;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.NoticeTypeStatus;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptRwggzs;
import com.xfgl.school.domain.XfptSuppliceContract;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.school.service.IXfptSuppliceContractService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 学校与供应商采购合同关联Controller
 *
 * @author yinet
 * @date 2025-04-22
 */
@Tag(name = "学校与供应商采购合同关联管理")
@RestController
@RequestMapping("/suppliceContract")
public class XfptSuppliceContractController extends BaseController {
    @Autowired
    private IXfptSuppliceContractService xfptSuppliceContractService;

    @Autowired
    private ITeamMeetingService teamMeetingService;
    @Autowired
    private ISupplierMessageService iSupplierMessageService;

    /**
     * 查询学校与供应商采购合同关联列表
     */

    @Operation(summary = "查询学校与供应商采购合同关联列表")
//@RequiresPermissions("system:contract:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSuppliceContract xfptSuppliceContract) {
        startPage();
        xfptSuppliceContract.setDeptId(null);
        List<XfptSuppliceContract> list = xfptSuppliceContractService.selectXfptSuppliceContractList(xfptSuppliceContract);
        for (XfptSuppliceContract xfptSuppliceContract1 : list) {
            SupplierMessage supplierMessage = iSupplierMessageService.getById(xfptSuppliceContract1.getSupplierId());
            xfptSuppliceContract1.setLegalRepresentative(supplierMessage.getLegalRepresentative());
        }
        return getDataTable(list);
    }

    /**
     * 导出学校与供应商采购合同关联列表
     */
    @Operation(summary = "导出学校与供应商采购合同关联列表")
//    @RequiresPermissions("system:contract:export")
    @Log(title = "学校与供应商采购合同关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSuppliceContract xfptSuppliceContract) {
        List<XfptSuppliceContract> list = xfptSuppliceContractService.selectXfptSuppliceContractList(xfptSuppliceContract);
        ExcelUtil<XfptSuppliceContract> util = new ExcelUtil<XfptSuppliceContract>(XfptSuppliceContract.class);
        util.exportExcel(response, list, "学校与供应商采购合同关联数据");
    }

    /**
     * 获取学校与供应商采购合同关联详细信息
     */
    @Operation(summary = "获取学校与供应商采购合同关联详细信息")
//    @RequiresPermissions("system:contract:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptSuppliceContractService.getById(id));
    }

    /**
     * 新增学校与供应商采购合同关联
     */
    @Operation(summary = "新增学校与供应商采购合同关联")
//    @RequiresPermissions("system:contract:add")
    @Log(title = "学校与供应商采购合同关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSuppliceContract xfptSuppliceContract) {
        //查看是否是黑名单
        SupplierMessage one = iSupplierMessageService.lambdaQuery().eq(SupplierMessage::getId, xfptSuppliceContract.getSupplierId()).one();
        if(one==null){
            return AjaxResult.warn("未查询到厂商信息");
        }
        if("1".equals(one.getBlacklistStatus())){
            return AjaxResult.warn("企业在黑名单中，禁止选择");
        }
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        Long deptId = dept.getDeptId();
        String ancestors = dept.getAncestors();
        xfptSuppliceContract.setAncestors(ancestors);
        List<XfptSuppliceContract> lists = xfptSuppliceContractService.lambdaQuery()
                .eq(XfptSuppliceContract::getDeptId, xfptSuppliceContract.getDeptId())
                .eq(XfptSuppliceContract::getTeamMeetingId, xfptSuppliceContract.getTeamMeetingId())
                .list();
        if (lists.isEmpty()) {
            teamMeetingService.updateTeamMeetingCurrOrder(Long.valueOf(xfptSuppliceContract.getTeamMeetingId()), ProceConstants.HTGK + "#" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }


        return toAjax(xfptSuppliceContractService.save(xfptSuppliceContract));
    }

    /**
     * 修改学校与供应商采购合同关联
     */
    @Operation(summary = "修改学校与供应商采购合同关联")
//    @RequiresPermissions("system:contract:edit")
    @Log(title = "学校与供应商采购合同关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSuppliceContract xfptSuppliceContract) {
        return toAjax(xfptSuppliceContractService.updateById(xfptSuppliceContract));
    }


    /**
     * 删除学校与供应商采购合同关联
     */
    @Operation(summary = "删除学校与供应商采购合同关联")
//    @RequiresPermissions("system:contract:remove")
    @Log(title = "学校与供应商采购合同关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSuppliceContractService.removeByIds(Arrays.asList(ids)));
    }


}
