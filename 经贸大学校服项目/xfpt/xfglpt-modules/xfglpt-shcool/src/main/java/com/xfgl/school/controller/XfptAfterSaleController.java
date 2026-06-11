package com.xfgl.school.controller;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.math.BigDecimal;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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
import com.xfgl.school.domain.XfptAfterSale;
import com.xfgl.school.service.IXfptAfterSaleService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 售后主Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="售后主管理")
@RestController
@RequestMapping("/afterSale")
public class XfptAfterSaleController extends BaseController {
    @Autowired
    private IXfptAfterSaleService xfptAfterSaleService;

/**
 * 查询售后主列表
 */

@Operation(summary = "查询售后主列表")
@GetMapping("/list")
    public TableDataInfo list(XfptAfterSale xfptAfterSale) {
        startPage();
        List<XfptAfterSale> list = xfptAfterSaleService.selectXfptAfterSaleList(xfptAfterSale);
        return getDataTable(list);
    }

    /**
     * 导出售后主列表
     */
    @Operation(summary = "导出售后主列表")
    @RequiresPermissions("school:afterSale:export")
    @Log(title = "售后主" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptAfterSale xfptAfterSale) {
        List<XfptAfterSale> list = xfptAfterSaleService.selectXfptAfterSaleList(xfptAfterSale);
        ExcelUtil<XfptAfterSale> util = new ExcelUtil<XfptAfterSale>(XfptAfterSale. class);
        util.exportExcel(response, list, "售后主数据");
    }

    /**
     * 获取售后主详细信息
     */
    @Operation(summary = "获取售后主详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptAfterSaleService.getById(id));
    }

    /**
     * 新增售后主
     */
    @Operation(summary = "新增售后主")
    @Log(title = "售后主" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptAfterSale xfptAfterSale) {
            autoApproveParentApply(xfptAfterSale);
            return toAjax(xfptAfterSaleService.save(xfptAfterSale));
    }

    private void autoApproveParentApply(XfptAfterSale xfptAfterSale) {
        if (xfptAfterSale == null) {
            return;
        }
        Date now = new Date();
        boolean isRefundType = "1".equals(xfptAfterSale.getApplyType()) || "2".equals(xfptAfterSale.getApplyType());
        xfptAfterSale.setStatus("4");
        if (isBlank(xfptAfterSale.getAuditBy())) {
            xfptAfterSale.setAuditBy("系统自动通过");
        }
        if (xfptAfterSale.getAuditTime() == null) {
            xfptAfterSale.setAuditTime(now);
        }
        if (isBlank(xfptAfterSale.getHandleResult())) {
            xfptAfterSale.setHandleResult(isRefundType
                    ? "家长端提交后系统自动完成退款售后处理"
                    : "家长端提交后系统自动完成换货售后处理");
        }
        if (xfptAfterSale.getFinishTime() == null) {
            xfptAfterSale.setFinishTime(now);
        }
        if (isRefundType) {
            if (xfptAfterSale.getActualRefundAmount() == null
                    || BigDecimal.ZERO.compareTo(xfptAfterSale.getActualRefundAmount()) == 0) {
                xfptAfterSale.setActualRefundAmount(xfptAfterSale.getApplyAmount());
            }
            xfptAfterSale.setRefundStatus("2");
            if (xfptAfterSale.getRefundTime() == null) {
                xfptAfterSale.setRefundTime(now);
            }
        } else {
            xfptAfterSale.setRefundStatus("0");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 修改售后主
     */
    @Operation(summary = "修改售后主")
    @RequiresPermissions("school:afterSale:edit")
    @Log(title = "售后主" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptAfterSale xfptAfterSale) {
            return toAjax(xfptAfterSaleService.updateById(xfptAfterSale));
    }


    /**
     * 删除售后主
     */
    @Operation(summary = "删除售后主")
    @RequiresPermissions("school:afterSale:remove")
    @Log(title = "售后主" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptAfterSaleService.removeByIds(Arrays.asList(ids)));
    }
}
