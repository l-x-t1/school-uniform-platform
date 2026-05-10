package com.xfgl.school.controller;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.Map;
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
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 学校供应商合作关系Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="学校供应商合作关系管理")
@RestController
@RequestMapping("/schoolSupplierRel")
public class XfptSchoolSupplierRelController extends BaseController {
    @Autowired
    private IXfptSchoolSupplierRelService xfptSchoolSupplierRelService;

/**
 * 查询学校供应商合作关系列表
 */

@Operation(summary = "查询学校供应商合作关系列表")
@RequiresPermissions("school:schoolSupplierRel:list")
@GetMapping("/list")
    public TableDataInfo list(XfptSchoolSupplierRel xfptSchoolSupplierRel) {
        startPage();
        List<XfptSchoolSupplierRel> list = xfptSchoolSupplierRelService.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
        return getDataTable(list);
    }

    /**
     * 导出学校供应商合作关系列表
     */
    @Operation(summary = "导出学校供应商合作关系列表")
    @RequiresPermissions("school:schoolSupplierRel:export")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSchoolSupplierRel xfptSchoolSupplierRel) {
        List<XfptSchoolSupplierRel> list = xfptSchoolSupplierRelService.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
        ExcelUtil<XfptSchoolSupplierRel> util = new ExcelUtil<XfptSchoolSupplierRel>(XfptSchoolSupplierRel. class);
        util.exportExcel(response, list, "学校供应商合作关系数据");
    }

    /**
     * 获取学校供应商合作关系详细信息
     */
    @Operation(summary = "获取学校供应商合作关系详细信息")
    @RequiresPermissions("school:schoolSupplierRel:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptSchoolSupplierRelService.selectXfptSchoolSupplierRelById(id));
    }

    /**
     * 新增学校供应商合作关系
     */
    @Operation(summary = "新增学校供应商合作关系")
    @RequiresPermissions("school:schoolSupplierRel:add")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSchoolSupplierRel xfptSchoolSupplierRel) {
            return toAjax(xfptSchoolSupplierRelService.insertXfptSchoolSupplierRel(xfptSchoolSupplierRel));
    }

    /**
     * 修改学校供应商合作关系
     */
    @Operation(summary = "修改学校供应商合作关系")
    @RequiresPermissions("school:schoolSupplierRel:edit")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSchoolSupplierRel xfptSchoolSupplierRel) {
            return toAjax(xfptSchoolSupplierRelService.updateXfptSchoolSupplierRel(xfptSchoolSupplierRel));
    }

    /**
     * 取消学校供应商合作
     */
    @Operation(summary = "取消学校供应商合作")
    @RequiresPermissions("school:schoolSupplierRel:edit")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/cancel")
    public AjaxResult cancel(@PathVariable("id") Long id) {
            return toAjax(xfptSchoolSupplierRelService.cancelCooperation(id));
    }

    /**
     * 设置学校供应商合作服务电话
     */
    @Operation(summary = "设置学校供应商合作服务电话")
    @RequiresPermissions("school:schoolSupplierRel:edit")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/servicePhone")
    public AjaxResult servicePhone(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
            return toAjax(xfptSchoolSupplierRelService.updateServicePhone(id, body.get("servicePhone")));
    }

    /**
     * 替换学校供应商合作商品
     */
    @Operation(summary = "替换学校供应商合作商品")
    @RequiresPermissions("school:schoolSupplierRel:edit")
    @Log(title = "学校供应商合作商品" , businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/goods")
    public AjaxResult goods(@PathVariable("id") Long id, @RequestBody List<XfptSchoolSupplierGoodsRel> goodsRelList) {
            return toAjax(xfptSchoolSupplierRelService.replaceGoodsRel(id, goodsRelList));
    }


    /**
     * 删除学校供应商合作关系
     */
    @Operation(summary = "删除学校供应商合作关系")
    @RequiresPermissions("school:schoolSupplierRel:remove")
    @Log(title = "学校供应商合作关系" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptSchoolSupplierRelService.deleteXfptSchoolSupplierRelByIds(ids));
    }
}
