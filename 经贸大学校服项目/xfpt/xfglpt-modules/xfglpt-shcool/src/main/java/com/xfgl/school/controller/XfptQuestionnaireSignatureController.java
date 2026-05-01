package com.xfgl.school.controller;

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
import com.xfgl.school.domain.XfptQuestionnaireSignature;
import com.xfgl.school.service.IXfptQuestionnaireSignatureService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 问卷签名Controller
 *
 * @author yinet
 * @date 2025-06-04
 */
@Tag(name="问卷签名管理")
@RestController
@RequestMapping("/signature")
public class XfptQuestionnaireSignatureController extends BaseController {
    @Autowired
    private IXfptQuestionnaireSignatureService xfptQuestionnaireSignatureService;

/**
 * 查询问卷签名列表
 */

@Operation(summary = "查询问卷签名列表")
@RequiresPermissions("school:signature:list")
@GetMapping("/list")
    public TableDataInfo list(XfptQuestionnaireSignature xfptQuestionnaireSignature) {
        startPage();
        List<XfptQuestionnaireSignature> list = xfptQuestionnaireSignatureService.selectXfptQuestionnaireSignatureList(xfptQuestionnaireSignature);
        return getDataTable(list);
    }

    /**
     * 导出问卷签名列表
     */
    @Operation(summary = "导出问卷签名列表")
    @RequiresPermissions("school:signature:export")
    @Log(title = "问卷签名" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptQuestionnaireSignature xfptQuestionnaireSignature) {
        List<XfptQuestionnaireSignature> list = xfptQuestionnaireSignatureService.selectXfptQuestionnaireSignatureList(xfptQuestionnaireSignature);
        ExcelUtil<XfptQuestionnaireSignature> util = new ExcelUtil<XfptQuestionnaireSignature>(XfptQuestionnaireSignature. class);
        util.exportExcel(response, list, "问卷签名数据");
    }

    /**
     * 获取问卷签名详细信息
     */
    @Operation(summary = "获取问卷签名详细信息")
    @RequiresPermissions("school:signature:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptQuestionnaireSignatureService.getById(id));
    }

    /**
     * 新增问卷签名
     */
    @Operation(summary = "新增问卷签名")
    @RequiresPermissions("school:signature:add")
    @Log(title = "问卷签名" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptQuestionnaireSignature xfptQuestionnaireSignature) {
            return toAjax(xfptQuestionnaireSignatureService.save(xfptQuestionnaireSignature));
    }

    /**
     * 修改问卷签名
     */
    @Operation(summary = "修改问卷签名")
    @RequiresPermissions("school:signature:edit")
    @Log(title = "问卷签名" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptQuestionnaireSignature xfptQuestionnaireSignature) {
            return toAjax(xfptQuestionnaireSignatureService.updateById(xfptQuestionnaireSignature));
    }


    /**
     * 删除问卷签名
     */
    @Operation(summary = "删除问卷签名")
    @RequiresPermissions("school:signature:remove")
    @Log(title = "问卷签名" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptQuestionnaireSignatureService.removeByIds(Arrays.asList(ids)));
    }
}
