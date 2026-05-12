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
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptComment;
import com.xfgl.school.domain.XfptCommentReply;
import com.xfgl.school.service.IXfptCommentService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;

/**
 * 商品评价Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="商品评价管理")
@RestController
@RequestMapping("/comment")
public class XfptCommentController extends BaseController {
    @Autowired
    private IXfptCommentService xfptCommentService;

/**
 * 查询商品评价列表
 */

@Operation(summary = "查询商品评价列表")
@RequiresPermissions("school:comment:list")
@GetMapping("/list")
    public TableDataInfo list(XfptComment xfptComment) {
        startPage();
        List<XfptComment> list = xfptCommentService.selectXfptCommentList(xfptComment);
        return getDataTable(list);
    }

    /**
     * 查询评价管理列表
     */
    @Operation(summary = "查询评价管理列表")
    @RequiresPermissions("school:evaluation:list")
    @GetMapping("/evaluation/list")
    public TableDataInfo evaluationList(XfptComment xfptComment) {
        applySchoolScope(xfptComment);
        startPage();
        List<XfptComment> list = xfptCommentService.selectEvaluationCommentList(xfptComment);
        return getDataTable(list);
    }

    /**
     * 获取评价管理详细信息
     */
    @Operation(summary = "获取评价管理详细信息")
    @RequiresPermissions("school:evaluation:query")
    @GetMapping(value = "/evaluation/{id}")
    public AjaxResult evaluationInfo(@PathVariable("id") Long id) {
        XfptComment xfptComment = xfptCommentService.selectEvaluationCommentById(id, resolveSchoolScopeId());
        return StringUtils.isNull(xfptComment) ? error("评价不存在或无权查看") : success(xfptComment);
    }

    /**
     * 回复评价
     */
    @Operation(summary = "回复评价")
    @RequiresPermissions("school:evaluation:reply")
    @Log(title = "评价管理" , businessType = BusinessType.INSERT)
    @PostMapping(value = "/evaluation/{id}/reply")
    public AjaxResult replyEvaluation(@PathVariable("id") Long id, @RequestBody XfptCommentReply xfptCommentReply) {
        if (StringUtils.isNull(xfptCommentReply) || StringUtils.isEmpty(xfptCommentReply.getContent())) {
            return error("回复内容不能为空");
        }
        String content = xfptCommentReply.getContent().trim();
        if (StringUtils.isEmpty(content)) {
            return error("回复内容不能为空");
        }
        if (content.length() > 500) {
            return error("回复内容不能超过500字");
        }
        xfptCommentReply.setContent(content);
        return toAjax(xfptCommentService.replyEvaluationComment(id, xfptCommentReply, resolveSchoolScopeId()));
    }

    /**
     * 隐藏或恢复评价
     */
    @Operation(summary = "隐藏或恢复评价")
    @RequiresPermissions("school:evaluation:hide")
    @Log(title = "评价管理" , businessType = BusinessType.UPDATE)
    @PutMapping(value = "/evaluation/{id}/status")
    public AjaxResult updateEvaluationStatus(@PathVariable("id") Long id, @RequestBody XfptComment xfptComment) {
        if (StringUtils.isNull(xfptComment) || StringUtils.isEmpty(xfptComment.getStatus())) {
            return error("评价状态不能为空");
        }
        String status = xfptComment.getStatus().trim();
        if (!"0".equals(status) && !"1".equals(status)) {
            return error("评价状态只能为0正常或1隐藏");
        }
        return toAjax(xfptCommentService.updateEvaluationCommentStatus(id, status, resolveSchoolScopeId()));
    }

    /**
     * 导出商品评价列表
     */
    @Operation(summary = "导出商品评价列表")
    @RequiresPermissions("school:comment:export")
    @Log(title = "商品评价" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptComment xfptComment) {
        List<XfptComment> list = xfptCommentService.selectXfptCommentList(xfptComment);
        ExcelUtil<XfptComment> util = new ExcelUtil<XfptComment>(XfptComment. class);
        util.exportExcel(response, list, "商品评价数据");
    }

    /**
     * 获取商品评价详细信息
     */
    @Operation(summary = "获取商品评价详细信息")
    @RequiresPermissions("school:comment:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptCommentService.selectXfptCommentById(id));
    }

    /**
     * 新增商品评价
     */
    @Operation(summary = "新增商品评价")
    @RequiresPermissions("school:comment:add")
    @Log(title = "商品评价" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptComment xfptComment) {
            return toAjax(xfptCommentService.insertXfptComment(xfptComment));
    }

    /**
     * 修改商品评价
     */
    @Operation(summary = "修改商品评价")
    @RequiresPermissions("school:comment:edit")
    @Log(title = "商品评价" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptComment xfptComment) {
            return toAjax(xfptCommentService.updateXfptComment(xfptComment));
    }


    /**
     * 删除商品评价
     */
    @Operation(summary = "删除商品评价")
    @RequiresPermissions("school:comment:remove")
    @Log(title = "商品评价" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptCommentService.deleteXfptCommentByIds(ids));
    }

    private void applySchoolScope(XfptComment xfptComment) {
        Long scopeSchoolId = resolveSchoolScopeId();
        if (scopeSchoolId != null) {
            xfptComment.getParams().put("scopeSchoolId", scopeSchoolId);
        }
    }

    private Long resolveSchoolScopeId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        if (sysUser == null) {
            return null;
        }
        if (SecurityUtils.isAdmin(sysUser.getUserId())) {
            return null;
        }
        if (hasRole(loginUser, RoleType.XX.getCode())) {
            return sysUser.getDeptId() == null ? -1L : sysUser.getDeptId();
        }
        return null;
    }

    private boolean hasRole(LoginUser loginUser, String roleKey) {
        return loginUser != null && loginUser.getRoles() != null && loginUser.getRoles().contains(roleKey);
    }
}
