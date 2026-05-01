package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.Ipv4Util;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.utils.ip.IpUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptReportMode;
import com.xfgl.school.domain.XfptReportModeOper;
import com.xfgl.school.domain.vo.XfptReportModeVo;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.school.service.IXfptReportModeOperService;
import com.xfgl.school.service.IXfptReportModeService;
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

import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 选用组织确定选购模式会议Controller
 *
 * @author yinet
 * @date 2025-04-23
 */
@Tag(name = "选用组织确定选购模式会议管理")
@RestController
@RequestMapping("/repmode")
@Slf4j
public class XfptReportModeController extends BaseController {
    @Autowired
    private IXfptReportModeService xfptReportModeService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptReportModeOperService iXfptReportModeOperService;

    /**
     * 查询选用组织确定选购模式会议列表
     */

    @Operation(summary = "查询选用组织确定选购模式会议列表")
   // @RequiresPermissions("shcool:mode:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptReportMode xfptReportMode) {
        startPage();
        xfptReportMode.setDeptId(null);
        List<XfptReportMode> list = xfptReportModeService.selectXfptReportModeList(xfptReportMode);
        return getDataTable(list);
    }

    /**
     * 导出选用组织确定选购模式会议列表
     */
    @Operation(summary = "导出选用组织确定选购模式会议列表")
    //@RequiresPermissions("shcool:mode:export")
    @Log(title = "选用组织确定选购模式会议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptReportMode xfptReportMode) {
        List<XfptReportMode> list = xfptReportModeService.selectXfptReportModeList(xfptReportMode);
        ExcelUtil<XfptReportMode> util = new ExcelUtil<XfptReportMode>(XfptReportMode.class);
        util.exportExcel(response, list, "选用组织确定选购模式会议数据");
    }

    /**
     * 获取选用组织确定选购模式会议详细信息
     */
    @Operation(summary = "获取选用组织确定选购模式会议详细信息")
   // @RequiresPermissions("shcool:mode:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {

        XfptReportMode xfptReportMode = xfptReportModeService.getById(id);
        if(xfptReportMode!=null){
            XfptReportModeVo xfptReportModeVo=new XfptReportModeVo();
            xfptReportModeVo.setXfptReportMode(xfptReportMode);
            //查询操作记录
            List<XfptReportModeOper> list = iXfptReportModeOperService.lambdaQuery().eq(XfptReportModeOper::getReportModeId, id).list();
            xfptReportModeVo.setOperList(list);
            return success(xfptReportModeVo);
        }else {
            return error("未查询到数据");
        }
    }

    /**
     * 新增选用组织确定选购模式会议
     */
    @Operation(summary = "新增选用组织确定选购模式会议")
   // @RequiresPermissions("shcool:mode:add")
    @Log(title = "选用组织确定选购模式会议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptReportMode xfptReportMode) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptReportMode.setAncestors(dept.getAncestors());
        xfptReportMode.setDeptId(dept.getDeptId());
        xfptReportMode.setUpdateTime(new Date());
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        //保存时间轴
        iTeamMeetingService.updateTeamMeetingCurrOrder(xfptReportMode.getTeamId(), ProceConstants.XGMS +"#"+format);
        return toAjax(xfptReportModeService.save(xfptReportMode));
    }

    /**
     * 修改选用组织确定选购模式会议
     */
    @Operation(summary = "修改选用组织确定选购模式会议")
   // @RequiresPermissions("shcool:mode:edit")
    @Log(title = "选用组织确定选购模式会议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptReportMode xfptReportMode) {
        //保存修改记录
        XfptReportModeOper xfptReportModeOper=new XfptReportModeOper();
        xfptReportModeOper.setReportModeId(xfptReportMode.getId()+"");
        xfptReportModeOper.setIp(IpUtils.getIpAddr());
        xfptReportModeOper.setCzr(SecurityUtils.getLoginUser().getSysUser().getNickName());
        boolean save = iXfptReportModeOperService.save(xfptReportModeOper);
        if(!save){
            log.error("保存操作记录失败：{}",xfptReportMode.toString());
        }
        return toAjax(xfptReportModeService.updateById(xfptReportMode));
    }


    /**
     * 删除选用组织确定选购模式会议
     */
    @Operation(summary = "删除选用组织确定选购模式会议")
   // @RequiresPermissions("shcool:mode:remove")
    @Log(title = "选用组织确定选购模式会议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptReportModeService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("/getModeByTeamId")
    public AjaxResult getModeByTeamId(Long teamId) {
        XfptReportMode xfptReportMode = xfptReportModeService.lambdaQuery().eq(XfptReportMode::getTeamId, teamId).one();
        if(xfptReportMode!=null){
            return success(xfptReportMode.getPurchaseMode());
        }else {
            return AjaxResult.success(null,null);
        }
    }
}
