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
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptCommentReply;
import com.xfgl.school.service.IXfptCommentReplyService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 商品评论回复Controller
 *
 * @author yinet
 * @date 2026-05-04
 */
@Tag(name="商品评论回复管理")
@RestController
@RequestMapping("/commentReply")
public class XfptCommentReplyController extends BaseController {
    @Autowired
    private IXfptCommentReplyService xfptCommentReplyService;

/**
 * 查询商品评论回复列表
 */

@Operation(summary = "查询商品评论回复列表")
@RequiresPermissions("school:commentReply:list")
@GetMapping("/list")
    public TableDataInfo list(XfptCommentReply xfptCommentReply) {
        startPage();
        List<XfptCommentReply> list = xfptCommentReplyService.selectXfptCommentReplyList(xfptCommentReply);
        return getDataTable(list);
    }

    /**
     * 导出商品评论回复列表
     */
    @Operation(summary = "导出商品评论回复列表")
    @RequiresPermissions("school:commentReply:export")
    @Log(title = "商品评论回复" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptCommentReply xfptCommentReply) {
        List<XfptCommentReply> list = xfptCommentReplyService.selectXfptCommentReplyList(xfptCommentReply);
        ExcelUtil<XfptCommentReply> util = new ExcelUtil<XfptCommentReply>(XfptCommentReply. class);
        util.exportExcel(response, list, "商品评论回复数据");
    }

    /**
     * 获取商品评论回复详细信息
     */
    @Operation(summary = "获取商品评论回复详细信息")
    @RequiresPermissions("school:commentReply:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
            return success(xfptCommentReplyService.getById(id));
    }

    /**
     * 新增商品评论回复
     */
    @Operation(summary = "新增商品评论回复")
    @RequiresPermissions("school:commentReply:add")
    @Log(title = "商品评论回复" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptCommentReply xfptCommentReply) {
            return toAjax(xfptCommentReplyService.save(xfptCommentReply));
    }

    /**
     * 修改商品评论回复
     */
    @Operation(summary = "修改商品评论回复")
    @RequiresPermissions("school:commentReply:edit")
    @Log(title = "商品评论回复" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptCommentReply xfptCommentReply) {
            return toAjax(xfptCommentReplyService.updateById(xfptCommentReply));
    }


    /**
     * 删除商品评论回复
     */
    @Operation(summary = "删除商品评论回复")
    @RequiresPermissions("school:commentReply:remove")
    @Log(title = "商品评论回复" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
            return toAjax(xfptCommentReplyService.removeByIds(Arrays.asList(ids)));
    }
}
