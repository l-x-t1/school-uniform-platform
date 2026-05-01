package com.xfgl.supplier.controller;

import java.util.List;
import java.util.Arrays;
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
import com.xfgl.supplier.domain.XfptUserAddress;
import com.xfgl.supplier.service.IXfptUserAddressService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 用户收货地址Controller
 *
 * @author yinet
 * @date 2026-05-01
 */
@Tag(name="用户收货地址管理")
@RestController
@RequestMapping("/user_address")
public class XfptUserAddressController extends BaseController {
    @Autowired
    private IXfptUserAddressService xfptUserAddressService;

/**
 * 查询用户收货地址列表
 */

@Operation(summary = "查询用户收货地址列表")
@RequiresPermissions("supplier:user_address:list")
@GetMapping("/list")
    public TableDataInfo list(XfptUserAddress xfptUserAddress) {
        startPage();
        List<XfptUserAddress> list = xfptUserAddressService.selectXfptUserAddressList(xfptUserAddress);
        return getDataTable(list);
    }

    /**
     * 导出用户收货地址列表
     */
    @Operation(summary = "导出用户收货地址列表")
    @RequiresPermissions("supplier:user_address:export")
    @Log(title = "用户收货地址" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptUserAddress xfptUserAddress) {
        List<XfptUserAddress> list = xfptUserAddressService.selectXfptUserAddressList(xfptUserAddress);
        ExcelUtil<XfptUserAddress> util = new ExcelUtil<XfptUserAddress>(XfptUserAddress. class);
        util.exportExcel(response, list, "用户收货地址数据");
    }

    /**
     * 获取用户收货地址详细信息
     */
    @Operation(summary = "获取用户收货地址详细信息")
    @RequiresPermissions("supplier:user_address:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptUserAddressService.getById(id));
    }

    /**
     * 新增用户收货地址
     */
    @Operation(summary = "新增用户收货地址")
    @RequiresPermissions("supplier:user_address:add")
    @Log(title = "用户收货地址" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptUserAddress xfptUserAddress) {
            return toAjax(xfptUserAddressService.save(xfptUserAddress));
    }

    /**
     * 修改用户收货地址
     */
    @Operation(summary = "修改用户收货地址")
    @RequiresPermissions("supplier:user_address:edit")
    @Log(title = "用户收货地址" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptUserAddress xfptUserAddress) {
            return toAjax(xfptUserAddressService.updateById(xfptUserAddress));
    }


    /**
     * 删除用户收货地址
     */
    @Operation(summary = "删除用户收货地址")
    @RequiresPermissions("supplier:user_address:remove")
    @Log(title = "用户收货地址" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptUserAddressService.removeByIds(Arrays.asList(ids)));
    }
}
