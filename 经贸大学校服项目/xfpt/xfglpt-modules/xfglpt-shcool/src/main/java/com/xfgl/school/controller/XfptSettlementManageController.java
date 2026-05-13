package com.xfgl.school.controller;

import java.util.List;

import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptWithdrawApply;
import com.xfgl.school.domain.vo.SettlementManageSettleRequest;
import com.xfgl.school.service.IXfptWithdrawApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学校端结算管理Controller
 *
 * 承接审核通过的申请结算记录，并记录结算资金流水。
 */
@Tag(name = "学校端结算管理")
@RestController
@RequestMapping({"/settlementManage", "/school/settlementManage", "/xfglpt-school/settlementManage"})
public class XfptSettlementManageController extends BaseController {
    @Autowired
    private IXfptWithdrawApplyService xfptWithdrawApplyService;

    /**
     * 查询结算管理列表
     */
    @Operation(summary = "查询结算管理列表")
    @RequiresPermissions("school:settlementManage:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptWithdrawApply xfptWithdrawApply) {
        startPage();
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectSettlementManageList(xfptWithdrawApply);
        return getDataTable(list);
    }

    /**
     * 获取结算管理详细信息
     */
    @Operation(summary = "获取结算管理详细信息")
    @RequiresPermissions("school:settlementManage:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptWithdrawApplyService.getById(id));
    }

    /**
     * 执行结算
     */
    @Operation(summary = "执行结算")
    @RequiresPermissions("school:settlementManage:settle")
    @Log(title = "结算管理", businessType = BusinessType.UPDATE)
    @PutMapping("/settle")
    public AjaxResult settle(@RequestBody SettlementManageSettleRequest request) {
        if (request == null || request.getId() == null) {
            return error("结算记录ID不能为空");
        }
        try {
            return success(xfptWithdrawApplyService.settleSettlementManage(
                    request.getId(),
                    request.getPayChannel(),
                    request.getTradeNo(),
                    request.getRemark()
            ));
        } catch (ServiceException e) {
            return error(e.getMessage());
        }
    }
}
