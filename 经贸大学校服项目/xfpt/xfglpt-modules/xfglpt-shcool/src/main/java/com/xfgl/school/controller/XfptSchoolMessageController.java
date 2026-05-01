package com.xfgl.school.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteDictService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysDictData;
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
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 学校信息Controller
 *
 * @author yinet
 * @date 2025-05-08
 */
@Tag(name = "学校信息管理")
@RestController
@RequestMapping("/schoolMessage")
public class XfptSchoolMessageController extends BaseController {
    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;
    @Autowired
    private RemoteDeptService remoteDeptService;

    /**
     * 查询学校信息列表
     */

    @Operation(summary = "查询学校信息列表")
//@RequiresPermissions("school:schoolMessage:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSchoolMessage xfptSchoolMessage) {
        startPage();
        List<XfptSchoolMessage> list = xfptSchoolMessageService.selectXfptSchoolMessageList(xfptSchoolMessage);
        return getDataTable(list);
    }

    @Operation(summary = "查询学校信息列表")
//@RequiresPermissions("school:schoolMessage:list")
    @GetMapping("/list1")
    public TableDataInfo list1(XfptSchoolMessage xfptSchoolMessage) {
        startPage();
        List<XfptSchoolMessage> list = xfptSchoolMessageService.selectXfptSchoolMessageList(xfptSchoolMessage);
        return getDataTable(list);
    }

    /**
     * 导出学校信息列表
     */
    @Operation(summary = "导出学校信息列表")
//    @RequiresPermissions("school:schoolMessage:export")
    @Log(title = "学校信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSchoolMessage xfptSchoolMessage) {
        List<XfptSchoolMessage> list = xfptSchoolMessageService.selectXfptSchoolMessageList(xfptSchoolMessage);
        ExcelUtil<XfptSchoolMessage> util = new ExcelUtil<XfptSchoolMessage>(XfptSchoolMessage.class);
        util.exportExcel(response, list, "学校信息数据");
    }

    /**
     * 获取学校信息详细信息
     */
    @Operation(summary = "获取学校信息详细信息")
//    @RequiresPermissions("school:schoolMessage:query")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable("deptId") Long deptId) {
        return success(xfptSchoolMessageService.lambdaQuery().eq(XfptSchoolMessage::getDeptId, deptId).one());
    }
    /**
     * 获取学校信息详细信息
     */
    @Operation(summary = "获取学校信息详细信息")
//    @RequiresPermissions("school:schoolMessage:query")
    @GetMapping(value = "/getInfo2/{id}")
    public AjaxResult getInfo2(@PathVariable("id") Long id) {
        return success(xfptSchoolMessageService.lambdaQuery().eq(XfptSchoolMessage::getId, id).one());
    }
    /**
     * 新增学校信息
     */
    @Operation(summary = "新增学校信息")
//    @RequiresPermissions("school:schoolMessage:add")
    @Log(title = "学校信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSchoolMessage xfptSchoolMessage) {
        SysDept dept = new SysDept();
        dept.setDeptId(xfptSchoolMessage.getDeptId());
        dept.setSchoolBadge(xfptSchoolMessage.getSchoolBadge());
        dept.setIsComplete("1");
        remoteDeptService.updateDeptInfo(dept, SecurityConstants.INNER);
        return toAjax(xfptSchoolMessageService.saveOrUpdate(xfptSchoolMessage));
    }

    /**
     * 修改学校信息
     */
    @Operation(summary = "修改学校信息")
//    @RequiresPermissions("school:schoolMessage:edit")
    @Log(title = "学校信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSchoolMessage xfptSchoolMessage) {
        SysDept dept = new SysDept();
        dept.setDeptId(xfptSchoolMessage.getDeptId());
        dept.setSchoolBadge(xfptSchoolMessage.getSchoolBadge());
        dept.setIsComplete("1");
        remoteDeptService.updateDeptInfo(dept, SecurityConstants.INNER);
        return toAjax(xfptSchoolMessageService.updateById(xfptSchoolMessage));
    }


    /**
     * 删除学校信息
     */
    @Operation(summary = "删除学校信息")
//    @RequiresPermissions("school:schoolMessage:remove")
    @Log(title = "学校信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSchoolMessageService.removeByIds(Arrays.asList(ids)));
    }

    @PostMapping("/isPerfect")
    public AjaxResult export(Long deptId) {
        XfptSchoolMessage xfptSchoolMessage = xfptSchoolMessageService.lambdaQuery().eq(XfptSchoolMessage::getDeptId, deptId).one();
        if (xfptSchoolMessage == null) {
            return success(false);
        } else {
            return success(true);
        }

    }

    /**
     * 新增供应商信息
     */
    @Operation(summary = "新增供应商信息")
//    @RequiresPermissions("system:message:add")
    @Log(title = "供应商信息", businessType = BusinessType.INSERT)
    @PostMapping("/schoolSecondLevel")
    public AjaxResult SchoolSecondLevel(XfptSchoolMessage xfptSchoolMessage) {

        List<XfptSchoolMessage> xfptSchoolMessageList = new ArrayList<>();
        List<SysDictData> sysDictDataList = xfptSchoolMessageService.selectDictDate("sc_sc_type");
        for (SysDictData sysDictData : sysDictDataList) {
            XfptSchoolMessage message = new XfptSchoolMessage();
            int number = xfptSchoolMessageService.lambdaQuery().eq(XfptSchoolMessage::getSchoolType, sysDictData.getDictValue())
                    .eq(XfptSchoolMessage::getDeptId, xfptSchoolMessage.getDeptId())
                    .list().size();
            message.setSchoolType(sysDictData.getDictLabel());
            message.setNumber(number);
            xfptSchoolMessageList.add(message);
        }
        XfptSchoolMessage message = new XfptSchoolMessage();
        message.setSchoolType("总数");
        int total = xfptSchoolMessageList.stream()
                .mapToInt(XfptSchoolMessage::getNumber) // 将每个SysDictData的number提取出来
                .sum(); // 计算总和
        message.setNumber(total);
        xfptSchoolMessageList.add(0, message);
        return success(xfptSchoolMessageList);
    }

    @GetMapping("/completeDetail")
    public TableDataInfo completeDetail(SysDept dept) {

        startPage();
        List<SysDept> list = new ArrayList<>();
        if (dept.getSchoolType() != null) {
            list = xfptSchoolMessageService.selectDeptList1(dept);
        } else {
            list = xfptSchoolMessageService.selectDeptList(dept);
        }
        for (SysDept sysDept : list) {
            String number = xfptSchoolMessageService.selectSchoolNumber(sysDept.getDeptId().toString());
            sysDept.setNumber(number);
            XfptSchoolMessage xfptSchoolMessage = xfptSchoolMessageService.lambdaQuery().eq(XfptSchoolMessage::getDeptId, sysDept.getDeptId()).one();
            if (xfptSchoolMessage != null) {
                sysDept.setSchoolType(xfptSchoolMessage.getSchoolType());
                sysDept.setSchoolArea(xfptSchoolMessage.getRegionCounty());
                sysDept.setSchoolId(xfptSchoolMessage.getId());
            }
        }
        return getDataTable(list);
    }
}
