package com.xfgl.school.controller;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.Locale;
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
import org.springframework.web.multipart.MultipartFile;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptGoods;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptGoodsService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.system.api.domain.SysUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 商品信息Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="商品信息管理")
@RestController
@RequestMapping("/goods")
public class XfptGoodsController extends BaseController {
    @Autowired
    private IXfptGoodsService xfptGoodsService;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Autowired
    private ISupplierMessageService supplierMessageService;

/**
 * 查询商品信息列表
 */

@Operation(summary = "查询商品信息列表")
@RequiresPermissions("school:goods:list")
@GetMapping("/list")
    public TableDataInfo list(XfptGoods xfptGoods, String simId) {
        if (simId != null && !"".equals(simId.trim())) {
            xfptGoods.getParams().put("simId", simId.trim());
        }
        startPage();
        List<XfptGoods> list = xfptGoodsService.selectXfptGoodsList(xfptGoods);
        return getDataTable(list);
    }

    /**
     * 导出商品信息列表
     */
    @Operation(summary = "导出商品信息列表")
    @RequiresPermissions("school:goods:export")
    @Log(title = "商品信息" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptGoods xfptGoods) {
        List<XfptGoods> list = xfptGoodsService.selectXfptGoodsList(xfptGoods);
        ExcelUtil<XfptGoods> util = new ExcelUtil<XfptGoods>(XfptGoods. class);
        util.exportExcel(response, list, "商品信息数据");
    }

    /**
     * 导入商品信息
     */
    @Operation(summary = "导入商品信息")
    @RequiresPermissions("school:goods:import")
    @Log(title = "商品信息" , businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        if (file == null || file.isEmpty()) {
            return AjaxResult.warn("请选择需要导入的商品文件");
        }
        String filename = file.getOriginalFilename();
        String lowerName = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        if (!lowerName.endsWith(".xls") && !lowerName.endsWith(".xlsx")) {
            return AjaxResult.warn("请上传xls或者xlsx格式文件");
        }
        ExcelUtil<XfptGoods> util = new ExcelUtil<XfptGoods>(XfptGoods.class);
        List<XfptGoods> goodsList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = xfptGoodsService.importGoods(goodsList, updateSupport, operName);
        return success(message);
    }

    /**
     * 商品导入模板
     */
    @Operation(summary = "商品导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<XfptGoods> util = new ExcelUtil<XfptGoods>(XfptGoods.class);
        util.importTemplateExcel(response, "商品信息数据");
    }

    /**
     * 获取商品信息详细信息
     */
    @Operation(summary = "获取商品信息详细信息")
    @RequiresPermissions("school:goods:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id, Long schoolId, String simId) {
            return success(xfptGoodsService.selectXfptGoodsById(id, schoolId, simId));
    }

    /**
     * 新增商品信息
     */
    @Operation(summary = "新增商品信息")
    @RequiresPermissions("school:goods:add")
    @Log(title = "商品信息" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptGoods xfptGoods) {
            fillCurrentSupplierIfNecessary(xfptGoods);
            return toAjax(xfptGoodsService.insertXfptGoods(xfptGoods));
    }

    /**
     * 修改商品信息
     */
    @Operation(summary = "修改商品信息")
    @RequiresPermissions("school:goods:edit")
    @Log(title = "商品信息" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptGoods xfptGoods) {
            fillCurrentSupplierIfNecessary(xfptGoods);
            return toAjax(xfptGoodsService.updateXfptGoods(xfptGoods));
    }


    /**
     * 删除商品信息
     */
    @Operation(summary = "删除商品信息")
    @RequiresPermissions("school:goods:remove")
    @Log(title = "商品信息" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptGoodsService.deleteXfptGoodsByIds(ids));
    }

    private void fillCurrentSupplierIfNecessary(XfptGoods xfptGoods) {
        SysUser currentUser = SecurityUtils.getLoginUser().getSysUser();
        Long supplierId = supplierAccessSupport.resolveSupplierId(currentUser);
        if (supplierId == null) {
            return;
        }
        SupplierMessage supplier = supplierMessageService.getById(supplierId);
        if (supplier == null) {
            return;
        }
        xfptGoods.setSupplierId(supplier.getId());
        xfptGoods.setSupplierName(supplier.getSupplierName());
    }
}
