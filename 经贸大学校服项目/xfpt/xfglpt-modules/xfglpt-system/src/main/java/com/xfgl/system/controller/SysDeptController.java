package com.xfgl.system.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.annotation.InnerAuth;
import com.xfgl.system.api.domain.SysDeptExport;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.domain.SysConfig;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.domain.vo.DeptImportVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.core.constant.UserConstants;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.service.ISysDeptService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 部门信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/dept")
@Slf4j
public class SysDeptController extends BaseController
{
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list")
    public AjaxResult list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return success(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        deptService.checkDeptDataScope(deptId);
        return success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @RequiresPermissions("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @RequiresPermissions("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDept dept)
    {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(deptId))
        {
            return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            return error("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @RequiresPermissions("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return warn("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return warn("部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.deleteDeptById(deptId));
    }

    @GetMapping("/schoolList")
    public TableDataInfo schoolList() {
        SysDept dept=new SysDept();
        dept.setIsSchool("1");
        startPage();
        List<SysDept> depts = deptService.selectDeptList(dept);
        return getDataTable(depts);
    }

    @InnerAuth
    @PostMapping("/update")
    public AjaxResult update(@RequestBody SysDept dept)
    {
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 获取部门列表：提供内部服务
     * @param parentDeptId
     * @return
     */
    @InnerAuth
    @GetMapping("/deptList/{parentDeptId}")
    public R<List<SysDept>> getDeptDataList(@PathVariable(value = "parentDeptId") Long parentDeptId) {
        SysDept sysDept = new SysDept();
        if (parentDeptId != -1L) {
            sysDept.setParentId(parentDeptId);
        }
        List<SysDept> dataList = deptService.selectDeptList2(sysDept);
        return R.ok(dataList);
    }

    /**
     * 获取部门列表：提供内部服务
     * @param deptId
     * @return
     */
    @InnerAuth
    @GetMapping("/getDeptListByDeptId/{deptId}")
    public R<List<SysDept>> getDeptListByDeptId(@PathVariable(value = "deptId") Long deptId) {
        SysDept sysDept = new SysDept();
        if (deptId != -1L) {
            sysDept.setDeptId(deptId);
        }
        List<SysDept> dataList = deptService.selectDeptList2(sysDept);
        return R.ok(dataList);
    }


    @Log(title = "部门管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<DeptImportVo> util = new ExcelUtil<DeptImportVo>(DeptImportVo.class);
        List<DeptImportVo> userList = util.importExcel(file.getInputStream());
        List<SysDept> result=new ArrayList<>();
        int flag=1;
        for (DeptImportVo deptImportVo : userList) {
            //查询上级部门
            SysDept sysDept = new SysDept();
            sysDept.setDeptName(deptImportVo.getDistrict());
            List<SysDept> sysDepts = deptService.selectDeptList(sysDept);
            if(CollUtil.isEmpty(sysDepts)){
                log.error("未找到上级部门：{}",deptImportVo.getDistrict());
            }
            SysDept sysDept1=new SysDept();
            sysDept1.setDeptName(deptImportVo.getSchoolName());
            sysDept1.setParentId(sysDepts.get(0).getDeptId());
            sysDept1.setAncestors(sysDepts.get(0).getAncestors()+","+sysDepts.get(0).getDeptId());
            sysDept1.setStatus("1");
            sysDept1.setIsSchool("1");
            //deptService.insertDept(sysDept1);
            if(flag==1){
                deptService.insertDept(sysDept1);
            }
        }


        return success(userList);
    }

    @GetMapping("/complete")
    public TableDataInfo complete(SysDept dept) {
//        dept.setParentId(100L);

        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());

        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            dept.setParentId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        } else {
            dept.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        }

        startPage();
        List<SysDept> list = deptService.selectDeptList(dept);
        for (SysDept sysDept : list) {
            //完善信息数量
            int schoolComplete = 0;
            //未完善信息数量
            int schoolNotComplete = 0;
            dept.setParentId(sysDept.getDeptId());
            List<SysDept> depts = deptService.selectDeptList3(dept);
            //学校数量
            sysDept.setSchoolSum(String.valueOf(list.size()));
            for (SysDept s : depts) {
                if (s.getIsComplete().equals("0")) {
                    schoolNotComplete+=1;
                }else if(s.getIsComplete().equals("1")){
                    schoolComplete+=1;
                }
            }
            sysDept.setSchoolComplete(String.valueOf(schoolComplete));
            sysDept.setSchoolNotComplete(String.valueOf(schoolNotComplete));
        }
        return getDataTable(list);
    }


    @PostMapping("/completeExport")
    public void completeExport(HttpServletResponse response, SysDept dept) {
//        dept.setParentId(100L);
        dept.setParentId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        List<SysDept> list = deptService.selectDeptList(dept);

        List<SysDeptExport> sysDeptExports=new ArrayList<>();
        for (SysDept sysDept : list) {
            SysDeptExport sysDeptExport=new SysDeptExport();
            sysDeptExport.setDeptName(sysDept.getDeptName());
            //完善信息数量
            int schoolComplete = 0;
            //未完善信息数量
            int schoolNotComplete = 0;
            dept.setParentId(sysDept.getDeptId());
            List<SysDept> depts = deptService.selectDeptList(dept);
            sysDeptExport.setSchoolSum(String.valueOf(list.size()));
            for (SysDept s : depts) {
                if (s.getIsComplete().equals("0")) {
                    schoolNotComplete+=1;
                }else if(s.getIsComplete().equals("1")){
                    schoolComplete+=1;
                }
            }
            sysDeptExport.setSchoolComplete(String.valueOf(schoolComplete));
            sysDeptExport.setSchoolNotComplete(String.valueOf(schoolNotComplete));
            sysDeptExports.add(sysDeptExport);
        }

        ExcelUtil<SysDeptExport> util = new ExcelUtil<SysDeptExport>(SysDeptExport.class);
        util.exportExcel(response, sysDeptExports, "学校完善信息");
    }

}
