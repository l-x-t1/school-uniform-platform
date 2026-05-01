package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.system.api.domain.SysDept;
import lombok.extern.slf4j.Slf4j;
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
import com.xfgl.school.domain.XfptCglcgzzj;
import com.xfgl.school.service.IXfptCglcgzzjService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 学校校服采购工作总结Controller
 *
 * @author yinet
 * @date 2025-07-23
 */
@Tag(name = "学校校服采购工作总结管理")
@RestController
@RequestMapping("/cglcgzzj")
@Slf4j
public class XfptCglcgzzjController extends BaseController {
    @Autowired
    private IXfptCglcgzzjService xfptCglcgzzjService;

    /**
     * 查询学校校服采购工作总结列表
     */

    @Operation(summary = "查询学校校服采购工作总结列表")
    //@RequiresPermissions("school:cglcgzzj:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptCglcgzzj xfptCglcgzzj) {
        startPage();
        List<XfptCglcgzzj> list = xfptCglcgzzjService.selectXfptCglcgzzjList(xfptCglcgzzj);
        return getDataTable(list);
    }

    /**
     * 导出学校校服采购工作总结列表
     */
    @Operation(summary = "导出学校校服采购工作总结列表")
    //@RequiresPermissions("school:cglcgzzj:export")
    @Log(title = "学校校服采购工作总结", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptCglcgzzj xfptCglcgzzj) {
        List<XfptCglcgzzj> list = xfptCglcgzzjService.selectXfptCglcgzzjList(xfptCglcgzzj);
        ExcelUtil<XfptCglcgzzj> util = new ExcelUtil<XfptCglcgzzj>(XfptCglcgzzj.class);
        util.exportExcel(response, list, "学校校服采购工作总结数据");
    }

    /**
     * 获取学校校服采购工作总结详细信息
     */
    @Operation(summary = "获取学校校服采购工作总结详细信息")
    //@RequiresPermissions("school:cglcgzzj:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptCglcgzzjService.getById(id));
    }

    @Autowired
    private ITeamMeetingService iTeamMeetingService;

    /**
     * 新增学校校服采购工作总结
     */
    @Operation(summary = "新增学校校服采购工作总结")
    //@RequiresPermissions("school:cglcgzzj:add")
    @Log(title = "学校校服采购工作总结", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptCglcgzzj xfptCglcgzzj) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();

        xfptCglcgzzj.setDeptId(dept.getDeptId());
        xfptCglcgzzj.setAncestors(dept.getAncestors());
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptCglcgzzj.getTeamId(), ProceConstants.CGGZZJ + "#" + format);
        if (i == 0) {
            log.error("保存时间轴失败：{}", xfptCglcgzzj.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(xfptCglcgzzjService.save(xfptCglcgzzj));
    }

    /**
     * 修改学校校服采购工作总结
     */
    @Operation(summary = "修改学校校服采购工作总结")
    //@RequiresPermissions("school:cglcgzzj:edit")
    @Log(title = "学校校服采购工作总结", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptCglcgzzj xfptCglcgzzj) {
        return toAjax(xfptCglcgzzjService.updateById(xfptCglcgzzj));
    }


    /**
     * 删除学校校服采购工作总结
     */
    @Operation(summary = "删除学校校服采购工作总结")
    //@RequiresPermissions("school:cglcgzzj:remove")
    @Log(title = "学校校服采购工作总结", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptCglcgzzjService.removeByIds(Arrays.asList(ids)));
    }
}
