package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;


import cn.hutool.core.date.DateUtil;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.SupplierUserRel;
import com.xfgl.school.mapper.SysUserRoleMapper;
import com.xfgl.school.mapper.SupplierUserRelMapper;
import com.xfgl.school.domain.XfptHmd;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.school.domain.XfptSuppliceContract;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.ISupplierUserRelService;
import com.xfgl.school.service.IXfptHmdService;
import com.xfgl.school.service.IXfptSchoolGradeClassService;
import com.xfgl.school.service.IXfptSuppliceContractService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 供应商信息Controller
 *
 * @author yinet
 * @date 2025-04-22
 */
@Tag(name = "供应商信息管理")
@RestController
@RequestMapping("/supplierMessage")
public class SupplierMessageController extends BaseController {
    @Autowired
    private ISupplierMessageService supplierMessageService;

    @Autowired
    private IXfptSuppliceContractService xfptSuppliceContractService;
    @Autowired
    private IXfptSchoolGradeClassService xfptSchoolGradeClassService;
    @Autowired
    private IXfptHmdService iXfptHmdService;
    @Autowired
    private ISupplierUserRelService supplierUserRelService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SupplierUserRelMapper supplierUserRelMapper;

    /**
     * 查询供应商信息列表
     */

    @Operation(summary = "查询供应商信息列表")
//    @RequiresPermissions("system:message:list")
    @GetMapping("/list")
    public TableDataInfo list(SupplierMessage supplierMessage) {

        startPage();
        List<SupplierMessage> list = supplierMessageService.selectSupplierMessageList(supplierMessage);
        for (SupplierMessage message : list) {
            List<XfptSuppliceContract> list1 = xfptSuppliceContractService.lambdaQuery()
                    .eq(XfptSuppliceContract::getSupplierId, message.getId())
                    .list();
            message.setServeSchoolNumber(String.valueOf(list1.size()));
        }
        fillBindingStatus(list);
        return getDataTable(list);
    }

    /**
     * 导出供应商信息列表
     */
    @Operation(summary = "导出供应商信息列表")
//    @RequiresPermissions("system:message:export")
    @Log(title = "供应商信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SupplierMessage supplierMessage) {
        List<SupplierMessage> list = supplierMessageService.selectSupplierMessageList(supplierMessage);
        for (SupplierMessage message : list) {
            List<XfptSuppliceContract> list1 = xfptSuppliceContractService.lambdaQuery()
                    .eq(XfptSuppliceContract::getSupplierId, message.getId())
                    .list();
            message.setServeSchoolNumber(String.valueOf(list1.size()));
        }
        ExcelUtil<SupplierMessage> util = new ExcelUtil<SupplierMessage>(SupplierMessage.class);
        util.exportExcel(response, list, "供应商信息数据");
    }

    /**
     * 获取供应商信息详细信息
     */
    @Operation(summary = "获取供应商信息详细信息")
//    @RequiresPermissions("system:message:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(supplierMessageService.getById(id));
    }

    /**
     * 新增供应商信息
     */
    @Operation(summary = "新增供应商信息")
//    @RequiresPermissions("system:message:add")
    @Log(title = "供应商信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SupplierMessage supplierMessage) {
        //判断是否是黑名单
        Long count = iXfptHmdService.lambdaQuery().eq(XfptHmd::getName, supplierMessage.getSupplierName()).count();
        if(count>0){
            return AjaxResult.error("此供应商已加入黑名单，请勿添加！");
        }

        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        List<SupplierMessage> supplierMessageList = supplierMessageService.lambdaQuery().eq(SupplierMessage::getSupplierName, supplierMessage.getSupplierName()).list();
        if (!supplierMessageList.isEmpty()) {
            return error(supplierMessage.getSupplierName() + "供应商已存在，不可重复添加！");
        }
        supplierMessage.setDeptName(dept.getDeptName());
        supplierMessage.setAncestors(dept.getAncestors());
        //校验手机号是否正确
        boolean matches = supplierMessage.getSupplierPhone().matches("^1[3-9]\\d{9}$");
        if (!matches ){
            return error("手机号格式不正确！");
        }
        return toAjax(supplierMessageService.save(supplierMessage));
    }

    /**
     * 修改供应商信息
     */
    @Operation(summary = "修改供应商信息")
//    @RequiresPermissions("system:message:edit")
    @Log(title = "供应商信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SupplierMessage supplierMessage) {
        return toAjax(supplierMessageService.updateById(supplierMessage));
    }


    /**
     * 删除供应商信息
     */
    @Operation(summary = "删除供应商信息")
//    @RequiresPermissions("system:message:remove")
    @Log(title = "供应商信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(supplierMessageService.removeByIds(Arrays.asList(ids)));
    }

    //    @RequiresPermissions("system:message:list")
    @GetMapping("/schoolList")
    public TableDataInfo schoolList(SupplierMessage supplierMessage) {
        startPage();
        List<XfptSuppliceContract> list = xfptSuppliceContractService.lambdaQuery()
                .eq(XfptSuppliceContract::getSupplierId, supplierMessage.getId())
                .select(XfptSuppliceContract::getDeptName, XfptSuppliceContract::getDeptId)  // 选择需要的字段
                .groupBy(XfptSuppliceContract::getDeptName, XfptSuppliceContract::getDeptId)
                .list();
        for (XfptSuppliceContract xfptSuppliceContract : list) {
            int studentNumber = xfptSchoolGradeClassService.lambdaQuery()
                    .eq(XfptSchoolGradeClass::getDeptId, xfptSuppliceContract.getDeptId())
                    .select(XfptSchoolGradeClass::getNumber)
                    .list()
                    .stream()  // 流式处理
                    .mapToInt(gradeClass -> {
                        try {
                            return Integer.parseInt(gradeClass==null?"0":gradeClass.getNumber());  // 将 String 转换为 int
                        } catch (NumberFormatException e) {
                            return 0;  // 如果转换失败，返回 0，或者你可以选择返回其他默认值
                        }
                    })
                    .sum();  // 对所有值进行求和
            xfptSuppliceContract.setStudentNumber(String.valueOf(studentNumber));
        }
        return getDataTable(list);
    }

    /**
     * 获取供应商绑定的用户列表
     */
    @Operation(summary = "获取供应商绑定的用户列表")
    @GetMapping("/{supplierId}/users")
    public AjaxResult getUsersBySupplierId(@PathVariable("supplierId") Long supplierId) {
        List<SysUser> users = supplierUserRelService.getUserListBySupplierId(supplierId);
        return success(users);
    }

    /**
     * 绑定用户到供应商
     */
    @Operation(summary = "绑定用户到供应商")
    @Log(title = "供应商用户关联", businessType = BusinessType.INSERT)
    @PostMapping("/bindUser")
    public AjaxResult bindUser(@RequestBody SupplierUserRel rel) {
        // 校验用户是否为供应商角色
        if (!sysUserRoleMapper.isSupplierUser(rel.getUserId())) {
            return error("该用户不是供应商角色，无法绑定");
        }
        // 校验用户是否已绑定其他厂商
        if (supplierUserRelMapper.existsByUserId(rel.getUserId())) {
            return error("该用户已绑定其他厂商");
        }
        // 校验是否已绑定当前厂商
        List<Long> existingUserIds = supplierUserRelService.findUserIdsBySupplierId(rel.getSupplierId());
        if (existingUserIds.contains(rel.getUserId())) {
            return error("该用户已绑定此厂商，请勿重复绑定");
        }
        supplierUserRelService.createRel(rel.getSupplierId(), rel.getUserId());
        return success();
    }

    /**
     * 解除用户与供应商的绑定
     */
    @Operation(summary = "解除用户与供应商的绑定")
    @Log(title = "供应商用户关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/unbindUser")
    public AjaxResult unbindUser(@RequestBody SupplierUserRel rel) {
        supplierUserRelService.unbindUser(rel.getSupplierId(), rel.getUserId());
        return success();
    }



    /**
     * 获取可绑定的供应商用户列表（排除已绑定用户）
     */
    @Operation(summary = "获取可绑定的供应商用户列表")
    @GetMapping("/availableUsers")
    public AjaxResult availableUsers(@RequestParam(required = false) String keyword) {
        List<SysUser> users = supplierUserRelMapper.findAvailableSupplierUsers(keyword);
        return success(users);
    }

    private void fillBindingStatus(List<SupplierMessage> suppliers) {
        if (suppliers == null || suppliers.isEmpty()) {
            return;
        }
        List<Long> supplierIds = suppliers.stream()
                .map(SupplierMessage::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (supplierIds.isEmpty()) {
            return;
        }
        List<SupplierUserRel> relList = supplierUserRelMapper.findUserRelsBySupplierIds(supplierIds);
        Map<Long, List<SupplierUserRel>> relMap = relList == null
                ? Collections.emptyMap()
                : relList.stream()
                .filter(rel -> rel.getSupplierId() != null)
                .collect(Collectors.groupingBy(SupplierUserRel::getSupplierId));

        for (SupplierMessage supplier : suppliers) {
            List<SupplierUserRel> userRels = relMap.getOrDefault(supplier.getId(), Collections.emptyList());
            supplier.setHasUserBinding(!userRels.isEmpty());
            supplier.setBindUserCount(userRels.size());
            supplier.setBindUserNames(userRels.stream()
                    .map(rel -> StringUtils.isNotBlank(rel.getNickName()) ? rel.getNickName() : rel.getUserName())
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.joining("、")));
        }
    }
}
