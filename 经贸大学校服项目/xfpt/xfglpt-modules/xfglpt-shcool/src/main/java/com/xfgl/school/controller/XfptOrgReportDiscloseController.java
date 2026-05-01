package com.xfgl.school.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptOrgReportDisclose;
import com.xfgl.school.domain.XfptOrgReportDiscloseSon;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.school.service.IXfptOrgReportDiscloseService;
import com.xfgl.school.service.IXfptOrgReportDiscloseSonService;
import com.xfgl.system.api.RemoteDictService;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 选用组织名单报备与公开Controller
 *
 * @author wxm
 * @date 2025-04-23
 */
@Tag(name = "选用组织名单报备与公开管理")
@RestController
@RequestMapping("/org/report/disclose")
public class XfptOrgReportDiscloseController extends BaseController {

    @Autowired
    private IXfptOrgReportDiscloseService xfptOrgReportDiscloseService;
    @Autowired
    private IXfptOrgReportDiscloseSonService orgReportDiscloseSonService;
    @Autowired
    private RemoteDictService remoteDictService;
    @Autowired
    private ITeamMeetingService teamMeetingService;


    public Map<String, String> getLabelValueMap(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            dictType = "sc_dble";
        }
        R<Map<String, String>> sdf = remoteDictService.getDictDataMap(dictType, 1, SecurityConstants.INNER);
        return sdf.getData();
    }

    public Map<String, String> getValueLabelMap(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            dictType = "sc_dble";
        }
        R<Map<String, String>> sdf = remoteDictService.getDictDataMap(dictType, 2, SecurityConstants.INNER);
        return sdf.getData();
    }


    /**
     * 查询选用组织名单报备与公开列表
     */
    @Operation(summary = "查询选用组织名单报备与公开列表")
    //@RequiresPermissions("org:disclose:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptOrgReportDisclose xfptOrgReportDisclose) {
        startPage();
        xfptOrgReportDisclose.setDeptId(null);
        List<XfptOrgReportDisclose> list = xfptOrgReportDiscloseService.selectXfptOrgReportDiscloseList(xfptOrgReportDisclose);
        for (XfptOrgReportDisclose orgReportDisclose : list) {
            orgReportDisclose.setSonDataList(new ArrayList<>());
        }
        return getDataTable(list);
    }

    /**
     * 导出选用组织名单报备与公开列表
     */
    @Operation(summary = "导出选用组织名单报备与公开列表")
    //@RequiresPermissions("org:disclose:export")
    @Log(title = "选用组织名单报备与公开", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptOrgReportDisclose xfptOrgReportDisclose) {
        Long id = xfptOrgReportDisclose.getId();
        if (id == null) {
            throw new RuntimeException("获取标识失败！");
        }
        XfptOrgReportDisclose xfptOrgReportDiscloseBase = xfptOrgReportDiscloseService.selectXfptOrgReportDiscloseById(id);
        if (xfptOrgReportDiscloseBase == null) {
            throw new RuntimeException("没有获取到任何数据！");
        }
        List<XfptOrgReportDiscloseSon> exportList = orgReportDiscloseSonService.selectXfptOrgReportDiscloseSonList(XfptOrgReportDiscloseSon.builder().pId(id).build());
        ExcelUtil<XfptOrgReportDiscloseSon> util = new ExcelUtil<>(XfptOrgReportDiscloseSon.class);
        //获取职务字典
        Map<String, String> zwDictMap = getValueLabelMap("sc_zw");
        //获取代表类别字典
        Map<String, String> dblbDictMap = getValueLabelMap("sc_dble");
        for (XfptOrgReportDiscloseSon xfptOrgReportDiscloseSonCurr : exportList) {
            String zw = xfptOrgReportDiscloseSonCurr.getZw();
            String dblb = xfptOrgReportDiscloseSonCurr.getDblb();
            xfptOrgReportDiscloseSonCurr.setZw(zwDictMap.getOrDefault(zw, ""));
            xfptOrgReportDiscloseSonCurr.setDblb(dblbDictMap.getOrDefault(dblb, ""));
        }
        util.exportExcel(response, exportList, "选用组织名单报备与公开数据");
    }

    /**
     * 获取选用组织名单报备与公开详细信息
     */
    @Operation(summary = "获取选用组织名单报备与公开详细信息")
    //@RequiresPermissions("org:disclose:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        if (id == null) {
            return AjaxResult.error("获取标识失败！");
        }
        try {
            XfptOrgReportDisclose entityBase = xfptOrgReportDiscloseService.selectXfptOrgReportDiscloseById(id);
            if (entityBase == null) {
                return AjaxResult.error("没有获取到数据！");
            }
            List<XfptOrgReportDiscloseSon> xfptOrgReportDiscloseSonDataList = new ArrayList<>();
            List<XfptOrgReportDiscloseSon> xfptOrgReportDiscloseSonDataListTmp = orgReportDiscloseSonService.selectXfptOrgReportDiscloseSonList(XfptOrgReportDiscloseSon.builder().pId(id).build());
            if (!CollectionUtils.isEmpty(xfptOrgReportDiscloseSonDataListTmp)) {
                xfptOrgReportDiscloseSonDataList = xfptOrgReportDiscloseSonDataListTmp;
            }
            entityBase.setSonDataList(xfptOrgReportDiscloseSonDataList);
            return AjaxResult.success(entityBase);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 新增选用组织名单报备与公开
     */
    @Operation(summary = "新增选用组织名单报备与公开")
    //@RequiresPermissions("org:disclose:add")
    @Log(title = "新增选用组织名单报备与公开", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody XfptOrgReportDisclose xfptOrgReportDisclose) {
        try {
            Long teamMeetingId = xfptOrgReportDisclose.getTeamMeetingId();
            if (teamMeetingId == null) {
                return AjaxResult.error("班子会议id不能为空！");
            }
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            if (dept == null) {
                return error("没有获取到用户部门信息！");
            }
            Long deptId = dept.getDeptId();
            String ancestors = dept.getAncestors();
            if (deptId == null) {
                return error("没有获取到部门信息！");
            }
            if (StringUtils.isBlank(ancestors)) {
                return error("没有获取到部门祖籍信息！");
            }
            xfptOrgReportDisclose.setDeptId(deptId);
            xfptOrgReportDisclose.setAncestors(ancestors);
            List<XfptOrgReportDiscloseSon> sonDataList = xfptOrgReportDisclose.getSonDataList();
            if (!CollectionUtils.isEmpty(sonDataList)) {
                //职务字典
                Map<String, String> zwValueLabelMap = getValueLabelMap("sc_zw");
                //value是label
                List<String> zwStdList = zwValueLabelMap.values().stream().collect(Collectors.toList());
                List<XfptOrgReportDiscloseSon> errorList = sonDataList.stream().filter(e -> StringUtils.isBlank(e.getZw())
                        || !zwStdList.contains(zwValueLabelMap.getOrDefault(e.getZw(),"nil"))).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(errorList)) {
                    return AjaxResult.error("职务不能为空且只能填写：" + zwStdList.toString());
                }
                int zzSize = sonDataList.stream().filter(e -> e.getZw().trim().contains("组长")).collect(Collectors.toList()).size();
                if (zzSize > 1) {
                    return AjaxResult.error("只能有一个组长！");
                }
                Map<String, String> dblbValueLabelMap = getValueLabelMap(null);
                List<String> dblbStdList = dblbValueLabelMap.values().stream().collect(Collectors.toList());
                errorList = sonDataList.stream().filter(e -> StringUtils.isBlank(e.getDblb()) || !dblbStdList.contains(dblbValueLabelMap.getOrDefault(e.getDblb(),"nil"))).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(errorList)) {
                    return AjaxResult.error("代表类别不能为空且只能填写：" + dblbStdList.toString());
                }
            }
            boolean saveFlg = xfptOrgReportDiscloseService.save(xfptOrgReportDisclose);
            if (saveFlg) {
                if (!CollectionUtils.isEmpty(sonDataList)) {
                    for (XfptOrgReportDiscloseSon xfptOrgReportDiscloseSonCurr : sonDataList) {
                        xfptOrgReportDiscloseSonCurr.setPId(xfptOrgReportDisclose.getId());
                    }
                    orgReportDiscloseSonService.saveBatch(sonDataList);
                }
            }
            //记录班子会议时间轴
            String timeTb = ProceConstants.ZZMD + "#" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            //如果存在多条数据，则只在第一次添加时添加时间轴
            List<XfptOrgReportDisclose> xfptOrgReportDisclosesExistList = xfptOrgReportDiscloseService.selectXfptOrgReportDiscloseList(XfptOrgReportDisclose.builder().teamMeetingId(teamMeetingId).build());
            if (CollectionUtils.isEmpty(xfptOrgReportDisclosesExistList) || xfptOrgReportDisclosesExistList.size() == 1) {
                teamMeetingService.updateTeamMeetingCurrOrder(teamMeetingId, timeTb);
            }
            return toAjax(1);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改选用组织名单报备与公开
     */
    @Operation(summary = "修改选用组织名单报备与公开")
    //@RequiresPermissions("org:disclose:edit")
    @Log(title = "选用组织名单报备与公开", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody XfptOrgReportDisclose xfptOrgReportDisclose) {
        try {
            Long id = xfptOrgReportDisclose.getId();
            if (id == null) {
                return AjaxResult.error("获取标识失败！");
            }
            List<XfptOrgReportDiscloseSon> sonDataList = xfptOrgReportDisclose.getSonDataList();
            if (!CollectionUtils.isEmpty(sonDataList)) {
                //职务字典
                Map<String, String> zwValueLabelMap = getValueLabelMap("sc_zw");
                List<String> zwStdList = zwValueLabelMap.values().stream().collect(Collectors.toList());
                List<XfptOrgReportDiscloseSon> errorList = sonDataList.stream().filter(e -> StringUtils.isBlank(e.getZw())
                        || !zwStdList.contains(zwValueLabelMap.getOrDefault(e.getZw(),"nil"))).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(errorList)) {
                    return AjaxResult.error("职务不能为空且只能填写：" + zwStdList.toString());
                }
                int zzSize = sonDataList.stream().filter(e -> e.getZw().trim().contains("组长")).collect(Collectors.toList()).size();
                if (zzSize > 1) {
                    return AjaxResult.error("只能有一个组长！");
                }
                Map<String, String> dblbValueLabelMap = getValueLabelMap(null);
                List<String> dblbStdList = dblbValueLabelMap.values().stream().collect(Collectors.toList());
                errorList = sonDataList.stream().filter(e -> StringUtils.isBlank(e.getDblb()) || !dblbStdList.contains(dblbValueLabelMap.getOrDefault(e.getDblb(),"nil"))).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(errorList)) {
                    return AjaxResult.error("代表类别不能为空且只能填写：" + dblbStdList.toString());
                }
            }
            boolean updateFlg = xfptOrgReportDiscloseService.updateById(xfptOrgReportDisclose);
            if (updateFlg) {
                sonDataList.stream().forEach(e -> {
                    e.setPId(id);
                });
                orgReportDiscloseSonService.saveOrUpdateBatch(sonDataList);
            }
            return toAjax(1);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 删除选用组织名单报备与公开
     */
    @Operation(summary = "删除选用组织名单报备与公开")
    //@RequiresPermissions("org:disclose:remove")
    @Log(title = "选用组织名单报备与公开删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids) {
        List<Long> pidList = Arrays.stream(ids).collect(Collectors.toList());
        try {
            xfptOrgReportDiscloseService.removeByIds(Arrays.asList(ids));
            LambdaUpdateWrapper<XfptOrgReportDiscloseSon> sonUpdateWrapper = new LambdaUpdateWrapper<>();
            sonUpdateWrapper.in(XfptOrgReportDiscloseSon::getPId, pidList);
            orgReportDiscloseSonService.remove(sonUpdateWrapper);
            return toAjax(1);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    @Operation(summary = "删除选用组织名单报备与公开从表")
    //@RequiresPermissions("org:disclose:remove")
    @Log(title = "选用组织名单报备与公开删除从表", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteSonData/{id}")
    public AjaxResult remove2(@PathVariable("id") Long id) {
        try {
            orgReportDiscloseSonService.removeById(id);
            return toAjax(1);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 下载导入模板
     *
     * @param response
     * @throws IOException
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<XfptOrgReportDiscloseSon> util = new ExcelUtil<>(XfptOrgReportDiscloseSon.class);
        util.importTemplateExcel(response, "选用组织名单模板");
    }


    /**
     * 临时导入数据，不存储
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/importTmpData")
    public AjaxResult importTmpData(MultipartFile file) throws Exception {
        ExcelUtil<XfptOrgReportDiscloseSon> util = new ExcelUtil<>(XfptOrgReportDiscloseSon.class);
        List<XfptOrgReportDiscloseSon> dataList = util.importExcel(file.getInputStream());
        //职务字典
        Map<String, String> zwLabelValueMap = getLabelValueMap("sc_zw");
        List<String> zwStdList = zwLabelValueMap.keySet().stream().collect(Collectors.toList());
        //检查职务是否在允许的数据列中
        List<XfptOrgReportDiscloseSon> errorList = dataList.stream().filter(e -> StringUtils.isBlank(e.getZw()) || !zwStdList.contains(e.getZw())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(errorList)) {
            return AjaxResult.error("职务不能为空且只能填写：" + zwStdList.toString());
        }
        Map<String, String> dblbLabelValueMap = getLabelValueMap(null);
        List<String> dblbStdList = dblbLabelValueMap.keySet().stream().collect(Collectors.toList());
        errorList = dataList.stream().filter(e -> StringUtils.isBlank(e.getDblb()) || !dblbStdList.contains(e.getDblb())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(errorList)) {
            return AjaxResult.error("代表类别不能为空且只能填写：" + dblbStdList.toString());
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return AjaxResult.error("没有数据！");
        }
        for (XfptOrgReportDiscloseSon xfptOrgReportDiscloseSonCurr : dataList) {
            xfptOrgReportDiscloseSonCurr.setDblb(dblbLabelValueMap.get(xfptOrgReportDiscloseSonCurr.getDblb()));
            xfptOrgReportDiscloseSonCurr.setZw(zwLabelValueMap.get(xfptOrgReportDiscloseSonCurr.getZw()));
        }
        return AjaxResult.success(dataList);
    }
}
