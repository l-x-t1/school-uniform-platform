package com.xfgl.school.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptParentsFeedback;
import com.xfgl.school.service.IXfptParentsFeedbackService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 家长反馈Controller
 *
 * @author yinet
 * @date 2025-05-13
 */
@Tag(name = "家长反馈管理")
@RestController
@RequestMapping("/parentsFeedback")
public class XfptParentsFeedbackController extends BaseController {
    @Autowired
    private IXfptParentsFeedbackService xfptParentsFeedbackService;
    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;
    HashMap<String, String> map = new HashMap<String, String>(){
        {
            put("130102", "长安区");
            put("130104", "桥西区");
            put("130105", "新华区");
            put("130107", "井陉矿区");
            put("130108", "裕华区");
            put("130109", "藁城区");
            put("130110", "鹿泉区");
            put("130111", "栾城区");
            put("130121", "井陉县");
            put("130123", "正定县");
            put("130125", "行唐县");
            put("130126", "灵寿县");
            put("130127", "高邑县");
            put("130128", "深泽县");
            put("130129", "赞皇县");
            put("130130", "无极县");
            put("130131", "平山县");
            put("130132", "元氏县");
            put("130133", "赵县");
            put("130183", "晋州市");
            put("130184", "新乐市");
        }
    };

    /**
     * 查询家长反馈列表
     */

    @Operation(summary = "查询家长反馈列表")
//@RequiresPermissions("school:parentsFeedback:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptParentsFeedback xfptParentsFeedback) {

        //查询角色，提取角色的key
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        List<XfptParentsFeedback> list = null;
        startPage();
        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            String string = map.get(xfptParentsFeedback.getRegion());
            xfptParentsFeedback.setRegion(string);
            list = xfptParentsFeedbackService.selectXfptParentsFeedbackList2(xfptParentsFeedback);
        }else if (collect.contains(RoleType.JYJ.getCode())){
            String deptName = SecurityUtils.getLoginUser().getSysUser().getDept().getDeptName();
            xfptParentsFeedback.setRegion(deptName);
            list = xfptParentsFeedbackService.selectXfptParentsFeedbackList(xfptParentsFeedback);
        }
        else  {
            Long deptId = SecurityUtils.getLoginUser().getSysUser().getDept().getDeptId();
            xfptParentsFeedback.setDeptId(deptId+"");
            String string = map.get(xfptParentsFeedback.getRegion());
            xfptParentsFeedback.setRegion(string);
            list = xfptParentsFeedbackService.selectXfptParentsFeedbackList(xfptParentsFeedback);
        }
        return getDataTable(list);
    }
    /**
     * 查询家长反馈列表
     */

    @Operation(summary = "查询家长反馈列表")
//@RequiresPermissions("school:parentsFeedback:list")
    @GetMapping("/list2")
    public TableDataInfo list2(XfptParentsFeedback xfptParentsFeedback) {


        startPage();

        List<XfptParentsFeedback> xfptParentsFeedbacks = xfptParentsFeedbackService.lambdaQuery()
                .eq(XfptParentsFeedback::getUserId, xfptParentsFeedback.getUserId())
                .eq(StrUtil.isNotEmpty(xfptParentsFeedback.getReplyState()),XfptParentsFeedback::getReplyState, xfptParentsFeedback.getReplyState())
                .list();
        return getDataTable(xfptParentsFeedbacks);
    }
    /**
     * 导出家长反馈列表
     */
    @Operation(summary = "导出家长反馈列表")
//    @RequiresPermissions("school:parentsFeedback:export")
    @Log(title = "家长反馈", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptParentsFeedback xfptParentsFeedback) {
        List<XfptParentsFeedback> list = xfptParentsFeedbackService.selectXfptParentsFeedbackList(xfptParentsFeedback);
        ExcelUtil<XfptParentsFeedback> util = new ExcelUtil<XfptParentsFeedback>(XfptParentsFeedback.class);
        util.exportExcel(response, list, "家长反馈数据");
    }

    /**
     * 获取家长反馈详细信息
     */
    @Operation(summary = "获取家长反馈详细信息")
//    @RequiresPermissions("school:parentsFeedback:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptParentsFeedbackService.getById(id));
    }

    /**
     * 新增家长反馈
     */
    @Operation(summary = "新增家长反馈")
//    @RequiresPermissions("school:parentsFeedback:add")
    @Log(title = "家长反馈", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(XfptParentsFeedback xfptParentsFeedback) {
        //查询学校的区域
        List<XfptSchoolMessage> list = xfptSchoolMessageService.lambdaQuery().eq(XfptSchoolMessage::getDeptId, xfptParentsFeedback.getDeptId()).list();
        if(CollUtil.isNotEmpty(list)){
            xfptParentsFeedback.setRegion(list.get(0).getRegionCounty());
        }

        return toAjax(xfptParentsFeedbackService.save(xfptParentsFeedback));
    }

    /**
     * 修改家长反馈
     */
    @Operation(summary = "修改家长反馈")
//    @RequiresPermissions("school:parentsFeedback:edit")
    @Log(title = "家长反馈", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptParentsFeedback xfptParentsFeedback) {
        Long id = xfptParentsFeedback.getId();
        if (id == null) {
            return error("获取标识失败！");
        }
        xfptParentsFeedback.setReplyTime(new Date());
        return toAjax(xfptParentsFeedbackService.updateById(xfptParentsFeedback));
    }


    /**
     * 删除家长反馈
     */
    @Operation(summary = "删除家长反馈")
//    @RequiresPermissions("school:parentsFeedback:remove")
    @Log(title = "家长反馈", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptParentsFeedbackService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("/getCount")
    public AjaxResult getCount(XfptParentsFeedback xfptParentsFeedback){
        if(StrUtil.isEmpty(xfptParentsFeedback.getUserId())){
            return AjaxResult.success("系统错误");
        }
        //查询总数
        Long count = xfptParentsFeedbackService.lambdaQuery()
                .eq(XfptParentsFeedback::getUserId, xfptParentsFeedback.getUserId())
                .count();
        //查询回复数量
        Long replyCount = xfptParentsFeedbackService.lambdaQuery()
                .eq(XfptParentsFeedback::getUserId, xfptParentsFeedback.getUserId())
                .eq(XfptParentsFeedback::getReplyState, "1")
                .count();
        //查询未回复数量
        Long noReplyCount = xfptParentsFeedbackService.lambdaQuery()
                .eq(XfptParentsFeedback::getUserId, xfptParentsFeedback.getUserId())
                .eq(XfptParentsFeedback::getReplyState, "0")
                .count();
        return AjaxResult.success(new HashMap<String,Object>(){{
            put("count",count);
            put("replyCount",replyCount);
            put("noReplyCount",noReplyCount);
        }});
    }

}
