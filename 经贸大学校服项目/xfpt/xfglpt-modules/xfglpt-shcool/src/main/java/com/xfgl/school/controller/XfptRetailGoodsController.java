package com.xfgl.school.controller;

import java.util.List;

import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.vo.XfptRetailGoodsVo;
import com.xfgl.school.service.IXfptRetailGoodsService;
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
 * 学校端零售商品上下架Controller
 */
@Tag(name = "学校端零售商品上下架管理")
@RestController
@RequestMapping("/retailGoods")
public class XfptRetailGoodsController extends BaseController {
    @Autowired
    private IXfptRetailGoodsService xfptRetailGoodsService;

    /**
     * 查询学校端零售商品列表
     */
    @Operation(summary = "查询学校端零售商品列表")
    @RequiresPermissions("school:retailGoods:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptRetailGoodsVo query) {
        Long schoolId = getCurrentSchoolId();
        query.setSchoolId(schoolId);
        startPage();
        List<XfptRetailGoodsVo> list = xfptRetailGoodsService.selectXfptRetailGoodsList(query);
        return getDataTable(list);
    }

    /**
     * 获取学校端零售商品详情
     */
    @Operation(summary = "获取学校端零售商品详情")
    @RequiresPermissions("school:retailGoods:query")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptRetailGoodsService.selectXfptRetailGoodsById(id, getCurrentSchoolId()));
    }

    /**
     * 上架/下架
     */
    @Operation(summary = "更新学校端零售商品上下架状态")
    @RequiresPermissions("school:retailGoods:edit")
    @Log(title = "学校端零售商品上下架", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public AjaxResult updateStatus(@PathVariable("id") Long id, @RequestBody XfptRetailGoodsVo retailGoodsVo) {
        if (retailGoodsVo == null || retailGoodsVo.getStatus() == null) {
            throw new ServiceException("上下架状态不能为空");
        }
        return toAjax(xfptRetailGoodsService.updateXfptRetailGoodsStatus(
                id,
                getCurrentSchoolId(),
                retailGoodsVo.getStatus(),
                SecurityUtils.getUsername()));
    }

    private Long getCurrentSchoolId() {
        Long schoolId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        if (schoolId == null) {
            throw new ServiceException("未获取到学校信息");
        }
        return schoolId;
    }
}
