package com.xfgl.school.controller;

import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.vo.XfptSizeExchangeQuery;
import com.xfgl.school.domain.vo.XfptSizeExchangeVo;
import com.xfgl.school.service.IXfptSizeExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Size exchange record controller.
 */
@Tag(name = "尺码调换记录")
@RestController
@RequestMapping("/sizeExchange")
public class XfptSizeExchangeController extends BaseController {

    @Autowired
    private IXfptSizeExchangeService xfptSizeExchangeService;

    @Operation(summary = "查询尺码调换记录列表")
    @RequiresPermissions("school:sxjjl:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSizeExchangeQuery query) {
        startPage();
        List<XfptSizeExchangeVo> list = xfptSizeExchangeService.selectSizeExchangeList(query);
        return getDataTable(list);
    }

    @Operation(summary = "查询尺码调换记录详情")
    @RequiresPermissions("school:sxjjl:list")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptSizeExchangeService.selectSizeExchangeById(id));
    }
}
