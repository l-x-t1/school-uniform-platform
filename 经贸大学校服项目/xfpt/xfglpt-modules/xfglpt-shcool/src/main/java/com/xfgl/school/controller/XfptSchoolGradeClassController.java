package com.xfgl.school.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.Classes;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.school.service.IXfptSchoolGradeClassService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 学校年级班级Controller
 *
 * @author yinet
 * @date 2025-04-24
 */
@Tag(name = "学校年级班级管理")
@RestController
@RequestMapping("/gradeClass")
//@CrossOrigin
public class XfptSchoolGradeClassController extends BaseController {
    @Autowired
    private IXfptSchoolGradeClassService xfptSchoolGradeClassService;


    /**
     * 获取本学校所有年级列表
     * @return
     */
    @GetMapping("/getCurrSchoolGradeList")
    public AjaxResult getCurrSchoolGradeList() {

        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        if (deptId == null) {
            return error("获取部门信息失败！");
        }
        XfptSchoolGradeClass xfptSchoolGradeClass = new XfptSchoolGradeClass();
        xfptSchoolGradeClass.setParentId(0L);
        xfptSchoolGradeClass.setDeptId(deptId.toString());
        //查询启用状态的年级
        xfptSchoolGradeClass.setStatus("0");
        List<XfptSchoolGradeClass> dataList = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
        return success(dataList);
    }

    /**
     * 获取本学校所有年级列表
     * @return
     */
    @GetMapping("/getCurrSchoolGradeList2")
    public AjaxResult getCurrSchoolGradeList2(Long deptId) {


        if (deptId == null) {
            return error("获取部门信息失败！");
        }
        XfptSchoolGradeClass xfptSchoolGradeClass = new XfptSchoolGradeClass();
        xfptSchoolGradeClass.setParentId(0L);
        xfptSchoolGradeClass.setDeptId(deptId.toString());
        //查询启用状态的年级
        xfptSchoolGradeClass.setStatus("0");
        List<XfptSchoolGradeClass> dataList = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
        return success(dataList);
    }


    /**
     * 查询学校年级班级列表
     */

    @Operation(summary = "查询学校年级班级列表")
//@RequiresPermissions("school:gradeClass:list")
    @GetMapping("/list")
    public AjaxResult list(XfptSchoolGradeClass xfptSchoolGradeClass) {
        xfptSchoolGradeClass.setParentId(0L);
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (!admin) {
            String deptId = xfptSchoolGradeClass.getDeptId();
            if (StringUtils.isBlank(deptId)) {
                Long deptId_curr = SecurityUtils.getLoginUser().getSysUser().getDeptId();
                if (deptId_curr != null) {
                    xfptSchoolGradeClass.setDeptId(deptId_curr.toString());
                }
            }
        }
        List<XfptSchoolGradeClass> list = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
        for (XfptSchoolGradeClass xfptSchoolGradeClass1 : list) {
            xfptSchoolGradeClass.setParentId(xfptSchoolGradeClass1.getId());
            List<XfptSchoolGradeClass> list1 = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
            List<Classes> classes = new ArrayList<>();
            for (XfptSchoolGradeClass gradeClass : list1) {
                Classes classes1 = new Classes();
                classes1.setId(gradeClass.getId());
                classes1.setDeptId(gradeClass.getDeptId());
                classes1.setDeptName(gradeClass.getDeptName());
                classes1.setGradeClass(gradeClass.getGradeClass());
                classes1.setStatus(gradeClass.getStatus());
                classes1.setNumber(gradeClass.getNumber());
                classes1.setParentId(gradeClass.getParentId());
                classes.add(classes1);
            }
            xfptSchoolGradeClass1.setClasses(classes);
        }
        return success(list);
    }

    /**
     * 导出学校年级班级列表
     */
    @Operation(summary = "导出学校年级班级列表")
//    @RequiresPermissions("school:gradeClass:export")
    @Log(title = "学校年级班级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSchoolGradeClass xfptSchoolGradeClass) {
        List<XfptSchoolGradeClass> list = xfptSchoolGradeClassService.selectXfptSchoolGradeClassList(xfptSchoolGradeClass);
        ExcelUtil<XfptSchoolGradeClass> util = new ExcelUtil<XfptSchoolGradeClass>(XfptSchoolGradeClass.class);
        util.exportExcel(response, list, "学校年级班级数据");
    }

    /**
     * 获取学校年级班级详细信息
     */
    @Operation(summary = "获取学校年级班级详细信息")
//    @RequiresPermissions("school:gradeClass:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptSchoolGradeClassService.getById(id));
    }

    /**
     * 新增学校年级班级
     */
    @Operation(summary = "新增学校年级班级")
//    @RequiresPermissions("school:gradeClass:add")
    @Log(title = "学校年级班级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSchoolGradeClass xfptSchoolGradeClass) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        String ancestors = dept.getAncestors();
        List<Classes> classesList = xfptSchoolGradeClass.getClasses();
        xfptSchoolGradeClass.setAncestors(ancestors);
//        LambdaQueryWrapper<XfptSchoolGradeClass> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(XfptSchoolGradeClass::get)
        xfptSchoolGradeClassService.saveOrUpdate(xfptSchoolGradeClass);
        for (Classes classes : classesList) {
            XfptSchoolGradeClass gradeClass = new XfptSchoolGradeClass();
            gradeClass.setId(classes.getId());
            gradeClass.setDeptId(xfptSchoolGradeClass.getDeptId());
            gradeClass.setDeptName(xfptSchoolGradeClass.getDeptName());
            gradeClass.setGradeClass(classes.getGradeClass());
            gradeClass.setNumber(classes.getNumber());
            gradeClass.setParentId(xfptSchoolGradeClass.getId());
            gradeClass.setAncestors(ancestors);
            gradeClass.setStatus(classes.getStatus());
            xfptSchoolGradeClassService.saveOrUpdate(gradeClass);
        }
        return AjaxResult.success();
    }

    /**
     * 修改学校年级班级
     */
    @Operation(summary = "修改学校年级班级")
//    @RequiresPermissions("school:gradeClass:edit")
    @Log(title = "学校年级班级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSchoolGradeClass xfptSchoolGradeClass) {
        xfptSchoolGradeClassService.updateById(xfptSchoolGradeClass);
        List<Classes> classesList = xfptSchoolGradeClass.getClasses();
        for (Classes classes : classesList) {
            XfptSchoolGradeClass gradeClass = new XfptSchoolGradeClass();
            gradeClass.setDeptId(xfptSchoolGradeClass.getDeptId());
            gradeClass.setDeptName(xfptSchoolGradeClass.getDeptName());
            gradeClass.setGradeClass(classes.getGradeClass());
            gradeClass.setNumber(classes.getNumber());
            gradeClass.setStatus(classes.getStatus());
            gradeClass.setParentId(xfptSchoolGradeClass.getId());
            xfptSchoolGradeClassService.updateById(gradeClass);
        }
        return AjaxResult.success();
    }


    /**
     * 删除学校年级班级
     */
    @Operation(summary = "删除学校年级班级")
//    @RequiresPermissions("school:gradeClass:remove")
    @Log(title = "学校年级班级", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Transactional
    public AjaxResult remove(@PathVariable Long[] ids) {
        boolean b = xfptSchoolGradeClassService.removeByIds(Arrays.asList(ids));
        QueryWrapper<XfptSchoolGradeClass> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("parent_id",ids);
        boolean remove = xfptSchoolGradeClassService.remove(queryWrapper);
        if(!remove){
            throw new RuntimeException("删除失败");
        }
        return toAjax(b);
    }

    /**
     * 查询年级
     * @param xfptSchoolGradeClass
     * @return
     */
    @GetMapping("/selectGradeClassList")
    public AjaxResult selectGradeClassList(XfptSchoolGradeClass xfptSchoolGradeClass) {
        List<XfptSchoolGradeClass> list1 = xfptSchoolGradeClassService.lambdaQuery()
                .eq(XfptSchoolGradeClass::getDeptId, xfptSchoolGradeClass.getDeptId())
                .eq(XfptSchoolGradeClass::getStatus, "0")
                .eq(XfptSchoolGradeClass::getGradeYear, xfptSchoolGradeClass.getParentId()).list();
        if(CollUtil.isNotEmpty(list1)){
            List<XfptSchoolGradeClass> list2 = xfptSchoolGradeClassService.lambdaQuery().eq(XfptSchoolGradeClass::getParentId, list1.get(0).getId()).list();
            return success(list2);
        }
        return success("未查询到班级信息");
    }


}
