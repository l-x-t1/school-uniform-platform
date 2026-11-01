package com.xfgl.school.controller;

import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptWithdrawApply;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.school.service.IXfptWithdrawApplyService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 渚涘簲鍟嗘彁鐜扮敵璇稢ontroller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name = "渚涘簲鍟嗘彁鐜扮敵璇风鐞?")
@RestController
@RequestMapping({"/withdrawApply", "/school/withdrawApply", "/xfglpt-school/withdrawApply"})
public class XfptWithdrawApplyController extends BaseController {

    private static final Long INVALID_SCHOOL_ID = -1L;
    private static final String VIEW_MODE_EDUCATION = "education";
    private static final String VIEW_MODE_SCHOOL = "school";

    @Autowired
    private IXfptWithdrawApplyService xfptWithdrawApplyService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    @Autowired
    private IXfptSchoolMessageService schoolMessageService;

    /**
     * 鏌ヨ渚涘簲鍟嗘彁鐜扮敵璇峰垪琛?
     */
    @Operation(summary = "鏌ヨ渚涘簲鍟嗘彁鐜扮敵璇峰垪琛?")
    @RequiresPermissions("school:withdrawApply:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptWithdrawApply xfptWithdrawApply) {
        xfptWithdrawApply = prepareWithdrawApplyScope(xfptWithdrawApply);
        startPage();
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        return getDataTable(list);
    }

    /**
     * 瀵煎嚭渚涘簲鍟嗘彁鐜扮敵璇峰垪琛?
     */
    @Operation(summary = "瀵煎嚭渚涘簲鍟嗘彁鐜扮敵璇峰垪琛?")
    @RequiresPermissions("school:withdrawApply:export")
    @Log(title = "渚涘簲鍟嗘彁鐜扮敵璇?", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptWithdrawApply xfptWithdrawApply) {
        xfptWithdrawApply = prepareWithdrawApplyScope(xfptWithdrawApply);
        List<XfptWithdrawApply> list = xfptWithdrawApplyService.selectXfptWithdrawApplyList(xfptWithdrawApply);
        ExcelUtil<XfptWithdrawApply> util = new ExcelUtil<XfptWithdrawApply>(XfptWithdrawApply.class);
        util.exportExcel(response, list, "渚涘簲鍟嗘彁鐜扮敵璇锋暟鎹?");
    }

    /**
     * 鑾峰彇渚涘簲鍟嗘彁鐜扮敵璇疯缁嗕俊鎭?
     */
    @Operation(summary = "鑾峰彇渚涘簲鍟嗘彁鐜扮敵璇疯缁嗕俊鎭?")
    @RequiresPermissions("school:withdrawApply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id, XfptWithdrawApply xfptWithdrawApply) {
        xfptWithdrawApply = prepareWithdrawApplyScope(xfptWithdrawApply);
        xfptWithdrawApply.setId(id);
        XfptWithdrawApply withdrawApply = xfptWithdrawApplyService.selectXfptWithdrawApplyDetail(xfptWithdrawApply);
        if (withdrawApply == null) {
            XfptWithdrawApply existing = xfptWithdrawApplyService.getById(id);
            if (existing == null) {
                return error("鎻愮幇鐢宠璁板綍涓嶅瓨鍦?");
            }
            return error("褰撳墠鎿嶄綔娌℃湁鏉冮檺");
        }
        return success(withdrawApply);
    }

    /**
     * 鏂板渚涘簲鍟嗘彁鐜扮敵璇?
     */
    @Operation(summary = "鏂板渚涘簲鍟嗘彁鐜扮敵璇?")
    @RequiresPermissions("school:withdrawApply:add")
    @Log(title = "渚涘簲鍟嗘彁鐜扮敵璇?", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
        xfptWithdrawApply = applySupplierScope(xfptWithdrawApply);
        fillCurrentSupplierSnapshot(xfptWithdrawApply);
        AjaxResult validateResult = validateWithdrawTimeOrder(xfptWithdrawApply);
        if (validateResult != null) {
            return validateResult;
        }
        return toAjax(xfptWithdrawApplyService.save(xfptWithdrawApply));
    }

    /**
     * 淇敼渚涘簲鍟嗘彁鐜扮敵璇?
     */
    @Operation(summary = "淇敼渚涘簲鍟嗘彁鐜扮敵璇?")
    @RequiresPermissions("school:withdrawApply:edit")
    @Log(title = "渚涘簲鍟嗘彁鐜扮敵璇?", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
        AjaxResult validateResult = validateWithdrawTimeOrder(xfptWithdrawApply);
        if (validateResult != null) {
            return validateResult;
        }
        return toAjax(xfptWithdrawApplyService.updateById(xfptWithdrawApply));
    }

    /**
     * 瀛︽牎绔鏍镐緵搴斿晢鎻愮幇鐢宠
     */
    @Operation(summary = "瀛︽牎绔鏍镐緵搴斿晢鎻愮幇鐢宠")
    @RequiresPermissions("school:withdrawApply:edit")
    @Log(title = "渚涘簲鍟嗘彁鐜扮敵璇峰鏍?", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody XfptWithdrawApply xfptWithdrawApply) {
        if (xfptWithdrawApply == null || xfptWithdrawApply.getId() == null) {
            return error("鎻愮幇鐢宠涓婚敭涓嶈兘涓虹┖");
        }

        SysUser currentUser = getCurrentUser();
        if (supplierAccessSupport.isSupplierUser(currentUser)) {
            return error("褰撳墠瑙掕壊涓嶆敮鎸佸鏍告搷浣?");
        }

        XfptWithdrawApply scopedQuery = prepareWithdrawApplyScope(xfptWithdrawApply);
        scopedQuery.setId(xfptWithdrawApply.getId());
        XfptWithdrawApply existing = xfptWithdrawApplyService.selectXfptWithdrawApplyDetail(scopedQuery);
        if (existing == null) {
            XfptWithdrawApply rawRecord = xfptWithdrawApplyService.getById(xfptWithdrawApply.getId());
            if (rawRecord == null) {
                return error("鎻愮幇鐢宠璁板綍涓嶅瓨鍦?");
            }
            return error("褰撳墠鎿嶄綔娌℃湁鏉冮檺");
        }
        if (!canAuditInCurrentView(scopedQuery)) {
            return error("褰撳墠瑙嗗浘涓嶆敮鎸佸鏍告搷浣?");
        }
        if (!"0".equals(existing.getStatus())) {
            return error("浠呭緟瀹℃牳璁板綍鍏佽瀹℃牳");
        }

        String targetStatus = xfptWithdrawApply.getStatus();
        if (!"1".equals(targetStatus) && !"2".equals(targetStatus)) {
            return error("瀛︽牎绔彧鍏佽瀹℃牳閫氳繃鎴栧鏍告嫆缁?");
        }
        if (StringUtils.isBlank(xfptWithdrawApply.getAuditRemark())) {
            return error("瀹℃牳澶囨敞涓嶈兘涓虹┖");
        }

        existing.setStatus(targetStatus);
        existing.setAuditRemark(xfptWithdrawApply.getAuditRemark());
        existing.setAuditTime(new Date());
        existing.setAuditUserId(currentUser == null ? null : currentUser.getUserId());
        existing.setAuditUserName(resolveAuditUserName(currentUser));

        AjaxResult validateResult = validateWithdrawTimeOrder(existing);
        if (validateResult != null) {
            return validateResult;
        }
        return toAjax(xfptWithdrawApplyService.updateById(existing));
    }

    /**
     * 鍒犻櫎渚涘簲鍟嗘彁鐜扮敵璇?
     */
    @Operation(summary = "鍒犻櫎渚涘簲鍟嗘彁鐜扮敵璇?")
    @RequiresPermissions("school:withdrawApply:remove")
    @Log(title = "渚涘簲鍟嗘彁鐜扮敵璇?", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptWithdrawApplyService.removeByIds(Arrays.asList(ids)));
    }

    private XfptWithdrawApply prepareWithdrawApplyScope(XfptWithdrawApply xfptWithdrawApply) {
        if (xfptWithdrawApply == null) {
            xfptWithdrawApply = new XfptWithdrawApply();
        }

        Long supplierId = resolveCurrentSupplierId();
        if (supplierId != null) {
            xfptWithdrawApply.setSupplierId(supplierId);
            xfptWithdrawApply.setViewMode(null);
            xfptWithdrawApply.setScopeSchoolId(null);
            xfptWithdrawApply.setScopeSchoolIds(null);
            return xfptWithdrawApply;
        }

        SysUser currentUser = getCurrentUser();
        String viewMode = normalizeViewMode(xfptWithdrawApply.getViewMode(), currentUser);
        xfptWithdrawApply.setViewMode(viewMode);
        xfptWithdrawApply.setScopeSchoolIds(null);

        if (isSchoolUser(currentUser)) {
            Long currentSchoolId = currentUser == null ? null : currentUser.getDeptId();
            xfptWithdrawApply.setViewMode(VIEW_MODE_SCHOOL);
            xfptWithdrawApply.setScopeSchoolId(currentSchoolId == null ? INVALID_SCHOOL_ID : currentSchoolId);
            return xfptWithdrawApply;
        }

        if (VIEW_MODE_SCHOOL.equals(viewMode)) {
            Long selectedSchoolId = xfptWithdrawApply.getScopeSchoolId();
            if (!canAccessSelectedSchool(currentUser, selectedSchoolId)) {
                xfptWithdrawApply.setScopeSchoolId(INVALID_SCHOOL_ID);
            }
            return xfptWithdrawApply;
        }

        xfptWithdrawApply.setScopeSchoolId(null);
        if (isAdminUser(currentUser)) {
            return xfptWithdrawApply;
        }

        List<Long> accessibleSchoolIds = resolveAccessibleSchoolIds(currentUser);
        if (accessibleSchoolIds.isEmpty()) {
            accessibleSchoolIds = Collections.singletonList(INVALID_SCHOOL_ID);
        }
        xfptWithdrawApply.setScopeSchoolIds(accessibleSchoolIds);
        return xfptWithdrawApply;
    }

    private XfptWithdrawApply applySupplierScope(XfptWithdrawApply xfptWithdrawApply) {
        if (xfptWithdrawApply == null) {
            xfptWithdrawApply = new XfptWithdrawApply();
        }
        Long supplierId = resolveCurrentSupplierId();
        if (supplierId != null) {
            xfptWithdrawApply.setSupplierId(supplierId);
        }
        return xfptWithdrawApply;
    }

    private void fillCurrentSupplierSnapshot(XfptWithdrawApply xfptWithdrawApply) {
        SysUser currentUser = getCurrentUser();
        if (xfptWithdrawApply == null || !supplierAccessSupport.isSupplierUser(currentUser)
                || StringUtils.isNotEmpty(xfptWithdrawApply.getSupplierName())) {
            return;
        }
        String supplierName = StringUtils.isNotEmpty(currentUser.getUserName()) ? currentUser.getUserName() : currentUser.getNickName();
        xfptWithdrawApply.setSupplierName(supplierName);
    }

    private boolean canAccessSelectedSchool(SysUser currentUser, Long schoolId) {
        if (schoolId == null) {
            return false;
        }
        if (isAdminUser(currentUser)) {
            return schoolExists(schoolId);
        }
        if (isSchoolUser(currentUser)) {
            return currentUser != null && schoolId.equals(currentUser.getDeptId());
        }
        return resolveAccessibleSchoolIds(currentUser).contains(schoolId);
    }

    private List<Long> resolveAccessibleSchoolIds(SysUser currentUser) {
        if (currentUser == null || currentUser.getDeptId() == null) {
            return Collections.emptyList();
        }
        SysDept deptQuery = new SysDept();
        deptQuery.setParentId(currentUser.getDeptId());
        deptQuery.setIsSchool("1");
        deptQuery.setStatus("0");
        List<SysDept> schoolList = schoolMessageService.selectDeptList(deptQuery);
        if (schoolList == null || schoolList.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> schoolIdSet = new LinkedHashSet<>();
        for (SysDept school : schoolList) {
            if (school != null && school.getDeptId() != null) {
                schoolIdSet.add(school.getDeptId());
            }
        }
        return new ArrayList<>(schoolIdSet);
    }

    private boolean schoolExists(Long schoolId) {
        if (schoolId == null) {
            return false;
        }
        SysDept deptQuery = new SysDept();
        deptQuery.setDeptId(schoolId);
        deptQuery.setIsSchool("1");
        deptQuery.setStatus("0");
        List<SysDept> schoolList = schoolMessageService.selectDeptList(deptQuery);
        return schoolList != null && !schoolList.isEmpty();
    }

    private boolean canAuditInCurrentView(XfptWithdrawApply xfptWithdrawApply) {
        return VIEW_MODE_SCHOOL.equalsIgnoreCase(normalizeViewMode(
                xfptWithdrawApply == null ? null : xfptWithdrawApply.getViewMode(),
                getCurrentUser()));
    }

    private boolean canAccessSupplierRecord(Long supplierId) {
        Long currentSupplierId = resolveCurrentSupplierId();
        return currentSupplierId == null || currentSupplierId.equals(supplierId);
    }

    private Long resolveCurrentSupplierId() {
        SysUser sysUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(sysUser)) {
            return null;
        }
        return supplierAccessSupport.resolveSupplierId(sysUser);
    }

    private String normalizeViewMode(String viewMode, SysUser currentUser) {
        if (isSchoolUser(currentUser)) {
            return VIEW_MODE_SCHOOL;
        }
        if (StringUtils.equalsIgnoreCase(viewMode, VIEW_MODE_SCHOOL)) {
            return VIEW_MODE_SCHOOL;
        }
        return VIEW_MODE_EDUCATION;
    }

    private boolean isSchoolUser(SysUser currentUser) {
        return hasRoleKey(currentUser, RoleType.XX.getCode(), RoleType.CSXX.getCode());
    }

    private boolean isAdminUser(SysUser currentUser) {
        if (currentUser == null) {
            return false;
        }
        if (currentUser.isAdmin()) {
            return true;
        }
        return hasRoleKey(currentUser, "super_admin");
    }

    private boolean hasRoleKey(SysUser currentUser, String... roleKeys) {
        if (currentUser == null || roleKeys == null || roleKeys.length == 0) {
            return false;
        }
        List<SysRole> roles = currentUser.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        for (SysRole role : roles) {
            if (role == null || StringUtils.isBlank(role.getRoleKey())) {
                continue;
            }
            for (String roleKey : roleKeys) {
                if (StringUtils.equalsIgnoreCase(role.getRoleKey(), roleKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    private SysUser getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return loginUser == null ? null : loginUser.getSysUser();
    }

    private String resolveAuditUserName(SysUser currentUser) {
        if (currentUser == null) {
            return "绯荤粺";
        }
        if (StringUtils.isNotEmpty(currentUser.getNickName())) {
            return currentUser.getNickName();
        }
        if (StringUtils.isNotEmpty(currentUser.getUserName())) {
            return currentUser.getUserName();
        }
        return "绯荤粺";
    }

    private AjaxResult validateWithdrawTimeOrder(XfptWithdrawApply withdrawApply) {
        if (withdrawApply == null) {
            return error("鎻愮幇鐢宠鏁版嵁涓嶈兘涓虹┖");
        }
        if (withdrawApply.getApplyTime() != null && withdrawApply.getAuditTime() != null
                && !withdrawApply.getApplyTime().before(withdrawApply.getAuditTime())) {
            return error("鐢宠鏃堕棿蹇呴』灏忎簬瀹℃牳鏃堕棿");
        }
        if (withdrawApply.getAuditTime() != null && withdrawApply.getPayTime() != null
                && !withdrawApply.getAuditTime().before(withdrawApply.getPayTime())) {
            return error("瀹℃牳鏃堕棿蹇呴』灏忎簬鎵撴鏃堕棿");
        }
        if (withdrawApply.getApplyTime() != null && withdrawApply.getPayTime() != null
                && !withdrawApply.getApplyTime().before(withdrawApply.getPayTime())) {
            return error("鐢宠鏃堕棿蹇呴』灏忎簬鎵撴鏃堕棿");
        }
        return null;
    }
}
