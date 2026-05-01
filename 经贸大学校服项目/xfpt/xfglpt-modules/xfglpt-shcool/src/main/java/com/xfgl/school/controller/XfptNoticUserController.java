package com.xfgl.school.controller;

import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xfgl.school.domain.XfptNoticUser;
import com.xfgl.school.service.IXfptNoticUserService;
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
 * 收藏Controller
 *
 * @author yinet
 * @date 2025-09-15
 */
@Tag(name = "收藏管理")
@RestController
@RequestMapping("/user")
public class XfptNoticUserController extends BaseController {
    @Autowired
    private IXfptNoticUserService xfptNoticUserService;

    /**
     * 查询收藏列表
     */

    @Operation(summary = "查询收藏列表")
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptNoticUser xfptNoticUser) {
        startPage();
        List<XfptNoticUser> list = xfptNoticUserService.selectXfptNoticUserList(xfptNoticUser);
        return getDataTable(list);
    }

    /**
     * 导出收藏列表
     */
    @Operation(summary = "导出收藏列表")
    @RequiresPermissions("system:user:export")
    @Log(title = "收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptNoticUser xfptNoticUser) {
        List<XfptNoticUser> list = xfptNoticUserService.selectXfptNoticUserList(xfptNoticUser);
        ExcelUtil<XfptNoticUser> util = new ExcelUtil<XfptNoticUser>(XfptNoticUser.class);
        util.exportExcel(response, list, "收藏数据");
    }

    /**
     * 获取收藏详细信息
     */
    @Operation(summary = "获取收藏详细信息")
    @RequiresPermissions("system:user:query")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        return success(xfptNoticUserService.getById(userId));
    }

    /**
     * 新增收藏
     */
    @Operation(summary = "新增收藏")
    @RequiresPermissions("system:user:add")
    @Log(title = "收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptNoticUser xfptNoticUser) {
        return toAjax(xfptNoticUserService.save(xfptNoticUser));
    }

    /**
     * 修改收藏
     */
    @Operation(summary = "修改收藏")
    @RequiresPermissions("system:user:edit")
    @Log(title = "收藏", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptNoticUser xfptNoticUser) {
        return toAjax(xfptNoticUserService.updateById(xfptNoticUser));
    }


    /**
     * 删除收藏
     */
    @Operation(summary = "删除收藏")
    @RequiresPermissions("system:user:remove")
    @Log(title = "收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(xfptNoticUserService.removeByIds(Arrays.asList(userIds)));
    }

    @GetMapping("/addCollect")
    public AjaxResult addCollect(XfptNoticUser xfptNoticUser) {
        if(xfptNoticUser.getNoticId()==null || xfptNoticUser.getUserId()==null){
            return AjaxResult.error("参数错误");
        }
        xfptNoticUser.setDelFlag("0");
        //判断是否收藏过
        Long count = xfptNoticUserService.lambdaQuery().eq(XfptNoticUser::getUserId, xfptNoticUser.getUserId())
                .eq(XfptNoticUser::getNoticId, xfptNoticUser.getNoticId()).count();
        if(count > 0){
            return AjaxResult.warn("您已收藏,请勿重复收藏");
        }
        boolean save = xfptNoticUserService.save(xfptNoticUser);
        return save ? AjaxResult.success("收藏成功") : AjaxResult.error("收藏失败");

    }
    @GetMapping("/collectList")
    public TableDataInfo collectList(Long userId){
        startPage();
       return getDataTable(xfptNoticUserService.collectList(userId));
    }

    @GetMapping("/removeCollect")
    public AjaxResult removeCollect(XfptNoticUser xfptNoticUser) {
        if(xfptNoticUser.getNoticId()==null || xfptNoticUser.getUserId()==null){
            return AjaxResult.error("参数错误");
        }
        //判断是否收藏过
        LambdaQueryWrapper<XfptNoticUser> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XfptNoticUser::getUserId, xfptNoticUser.getUserId()).eq(XfptNoticUser::getNoticId, xfptNoticUser.getNoticId());
        boolean remove = xfptNoticUserService.remove(lambdaQueryWrapper);
        return toAjax(remove);

    }
}
