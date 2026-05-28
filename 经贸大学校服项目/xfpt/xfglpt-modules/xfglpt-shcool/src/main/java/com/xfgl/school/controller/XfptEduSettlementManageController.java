package com.xfgl.school.controller;

import java.util.List;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptEduSettlement;
import com.xfgl.school.service.IXfptEduSettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教育局结算管理Controller
 *
 * 独立承载教育局结算查询和统计，不修改供应商提现、资金流水和现有结算流程。
 */
@Tag(name = "教育局结算管理")
@RestController
@RequestMapping({"/eduSettlementManage", "/school/eduSettlementManage", "/xfglpt-school/eduSettlementManage"})
public class XfptEduSettlementManageController extends BaseController {
    @Autowired
    private IXfptEduSettlementService xfptEduSettlementService;

    /**
     * 查询教育局结算管理列表
     */
    @Operation(summary = "查询教育局结算管理列表")
    @RequiresPermissions("school:eduSettlementManage:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptEduSettlement xfptEduSettlement) {
        startPage();
        List<XfptEduSettlement> list = xfptEduSettlementService.selectXfptEduSettlementList(xfptEduSettlement);
        return getDataTable(list);
    }

    /**
     * 查询教育局结算统计
     */
    @Operation(summary = "查询教育局结算统计")
    @RequiresPermissions("school:eduSettlementManage:statistics")
    @GetMapping("/statistics")
    public AjaxResult statistics(XfptEduSettlement xfptEduSettlement) {
        return success(xfptEduSettlementService.selectXfptEduSettlementStatistics(xfptEduSettlement));
    }

    /**
     * 获取教育局结算详细信息
     */
    @Operation(summary = "获取教育局结算详细信息")
    @RequiresPermissions("school:eduSettlementManage:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptEduSettlementService.selectXfptEduSettlementById(id));
    }
}
