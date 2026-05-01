package com.xfgl.school.controller;

import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.school.service.IXfptSchoolGradeClassService;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.service.IXfptStudentInfoService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 家长绑定学生信息Controller
 *
 * @author yinet
 * @date 2025-05-13
 */
@Tag(name = "家长绑定学生信息管理")
@RestController
@RequestMapping("/schoolStudentInfo")
public class XfptStudentInfoController extends BaseController {
    @Autowired
    private IXfptStudentInfoService xfptStudentInfoService;

    @Autowired
    private IXfptSchoolGradeClassService xfptSchoolGradeClassService;

    /**
     * 查询家长绑定学生信息列表
     */

    @Operation(summary = "查询家长绑定学生信息列表")
    //@RequiresPermissions("school:schoolStudentInfo:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptStudentInfo xfptStudentInfo) {
        startPage();
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        if(SecurityUtils.isAdmin(deptId)){
            xfptStudentInfo.setShcoolId(deptId+"");
        }
        List<XfptStudentInfo> list = xfptStudentInfoService.selectXfptStudentInfoList(xfptStudentInfo);
        for (XfptStudentInfo info : list) {
            //XfptSchoolGradeClass xfptSchoolGradeClass = xfptSchoolGradeClassService.getById(info.getClassId());
            //if (xfptSchoolGradeClass != null) {
            //    XfptSchoolGradeClass gradeClass = xfptSchoolGradeClassService.getById(xfptSchoolGradeClass.getParentId());
            //    if (gradeClass != null) {
            //        xfptSchoolGradeClass.setDeptId(gradeClass.getGradeClass());
            //        xfptSchoolGradeClass.setGradeYear(gradeClass.getGradeYear());
            //    }
            //}
            SysDept sysDept = xfptSchoolGradeClassService.selectDeptId(info.getShcoolId());
            //info.setXfptSchoolGradeClasses(xfptSchoolGradeClass);
            info.setSysDepts(sysDept);
            info.setShcoolId(SecurityUtils.getLoginUser().getSysUser().getNickName());
        }
        return getDataTable(list);
    }
    @GetMapping("/list2")
    public TableDataInfo list2(XfptStudentInfo xfptStudentInfo){
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        xfptStudentInfo.setShcoolId(deptId+"");
        startPage();
        List<XfptStudentInfo> list = xfptStudentInfoService.selectXfptStudentInfoList(xfptStudentInfo);
        return getDataTable(list);
    }


    /**
     * 导出家长绑定学生信息列表
     */
    @Operation(summary = "导出家长绑定学生信息列表")
//    @RequiresPermissions("school:schoolStudentInfo:export")
    @Log(title = "家长绑定学生信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptStudentInfo xfptStudentInfo) {
        List<XfptStudentInfo> list = xfptStudentInfoService.selectXfptStudentInfoList(xfptStudentInfo);
        ExcelUtil<XfptStudentInfo> util = new ExcelUtil<XfptStudentInfo>(XfptStudentInfo.class);
        util.exportExcel(response, list, "家长绑定学生信息数据");
    }

    /**
     * 获取家长绑定学生信息详细信息
     */
    @Operation(summary = "获取家长绑定学生信息详细信息")
//    @RequiresPermissions("school:schoolStudentInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptStudentInfo xfptStudentInfo = xfptStudentInfoService.getById(id);
        //XfptSchoolGradeClass xfptSchoolGradeClass = xfptSchoolGradeClassService.getById(xfptStudentInfo.getClassId());
        //if (xfptSchoolGradeClass != null) {
        //    XfptSchoolGradeClass gradeClass = xfptSchoolGradeClassService.getById(xfptSchoolGradeClass.getParentId());
        //    if (gradeClass != null) {
        //        xfptSchoolGradeClass.setDeptId(gradeClass.getGradeClass());
        //        xfptSchoolGradeClass.setGradeYear(gradeClass.getGradeYear());
        //    }
        //}
        SysDept sysDept = xfptSchoolGradeClassService.selectDeptId(xfptStudentInfo.getShcoolId());
        //xfptStudentInfo.setXfptSchoolGradeClasses(xfptSchoolGradeClass);
        xfptStudentInfo.setSysDepts(sysDept);
        return success(xfptStudentInfo);
    }

    /**
     * 新增家长绑定学生信息
     */
    @Operation(summary = "新增家长绑定学生信息")
//    @RequiresPermissions("school:schoolStudentInfo:add")
    @Log(title = "家长绑定学生信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(XfptStudentInfo xfptStudentInfo) {
        XfptStudentInfo info = xfptStudentInfoService.lambdaQuery().eq(XfptStudentInfo::getIdNumber, xfptStudentInfo.getIdNumber()).one();
        if (info != null) {
            return error("此学生已绑定，不可重复绑定！");
        }
        return toAjax(xfptStudentInfoService.save(xfptStudentInfo));
    }

    /**
     * 修改家长绑定学生信息
     */
    @Operation(summary = "修改家长绑定学生信息")
//    @RequiresPermissions("school:schoolStudentInfo:edit")
    @Log(title = "家长绑定学生信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptStudentInfo xfptStudentInfo) {
        return toAjax(xfptStudentInfoService.updateById(xfptStudentInfo));
    }


    /**
     * 删除家长绑定学生信息
     */
    @Operation(summary = "删除家长绑定学生信息")
//    @RequiresPermissions("school:schoolStudentInfo:remove")
    @Log(title = "家长绑定学生信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptStudentInfoService.removeByIds(Arrays.asList(ids)));
    }
}
