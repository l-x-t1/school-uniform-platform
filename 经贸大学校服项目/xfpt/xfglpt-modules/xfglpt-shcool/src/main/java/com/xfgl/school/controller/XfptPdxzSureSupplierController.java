package com.xfgl.school.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.CommUtil;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.domain.vo.XfptPdxzSureSupplierVo;
import com.xfgl.school.service.*;
import com.xfgl.system.api.domain.SysDept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
 * 评定小组确定供应商Controller
 *
 * @author yinet
 * @date 2025-05-23
 */
@Tag(name = "评定小组确定供应商管理")
@RestController
@RequestMapping("/pdxzSupplier")
@Slf4j
public class XfptPdxzSureSupplierController extends BaseController {
    @Autowired
    private IXfptPdxzSureSupplierService xfptPdxzSureSupplierService;
    @Autowired
    private IXfptQuestionnaireConfigService xfptQuestionnaireConfigService;
    @Autowired
    private IXfptQuestionnaireSurveyService questionnaireSurveyService;
    @Autowired
    private IXfptReportModeService iXfptReportModeService;
    @Autowired
    private IXfptSupplierListService iXfptSupplierListService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptQuestionnaireResultService iXfptQuestionnaireResultService;

    /**
     * 查询评定小组确定供应商列表
     */

    @Operation(summary = "查询评定小组确定供应商列表")
    //@RequiresPermissions("school:pdxzSupplier:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptPdxzSureSupplier xfptPdxzSureSupplier) {
        startPage();
        List<XfptPdxzSureSupplier> list = xfptPdxzSureSupplierService.selectXfptPdxzSureSupplierList(xfptPdxzSureSupplier);
        if(CollUtil.isNotEmpty(list)){
            for (XfptPdxzSureSupplier pdxzSureSupplier : list) {
                //查询投票人数
                List<XfptQuestionnaireResult> list1 = iXfptQuestionnaireResultService.lambdaQuery().eq(XfptQuestionnaireResult::getQuestionnaireSurveyId
                        , pdxzSureSupplier.getQuestId()).list();
                if(CollUtil.isNotEmpty(list1) && pdxzSureSupplier.getCount()!=null){
                    //去重
                    List<XfptQuestionnaireResult> collect = list1.stream().filter(CommUtil.distinctPredicate(XfptQuestionnaireResult::getName)).collect(Collectors.toList());
                    pdxzSureSupplier.setVotedCount(collect.size());
                    //计算投票率，四舍五入
                    //计算投票率，四舍五入
                    if(pdxzSureSupplier.getCount()==null){
                        pdxzSureSupplier.setCount(0);
                    }
                    if(pdxzSureSupplier.getVotedCount()==null){
                        pdxzSureSupplier.setVotedCount(0);
                    }
                    pdxzSureSupplier.setVotingRate(new BigDecimal(pdxzSureSupplier.getVotedCount()).divide(new BigDecimal(pdxzSureSupplier.getCount()),
                            2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }else {
                    pdxzSureSupplier.setVotingRate(0);
                    pdxzSureSupplier.setVotedCount(0);
                }
            }
        }

        return getDataTable(list);
    }

    /**
     * 导出评定小组确定供应商列表
     */
    @Operation(summary = "导出评定小组确定供应商列表")
   // @RequiresPermissions("school:pdxzSupplier:export")
    @Log(title = "评定小组确定供应商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptPdxzSureSupplier xfptPdxzSureSupplier) {
        List<XfptPdxzSureSupplier> list = xfptPdxzSureSupplierService.selectXfptPdxzSureSupplierList(xfptPdxzSureSupplier);
        ExcelUtil<XfptPdxzSureSupplier> util = new ExcelUtil<XfptPdxzSureSupplier>(XfptPdxzSureSupplier.class);
        util.exportExcel(response, list, "评定小组确定供应商数据");
    }

    /**
     * 获取评定小组确定供应商详细信息
     */
    @Operation(summary = "获取评定小组确定供应商详细信息")
    //@RequiresPermissions("school:pdxzSupplier:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptPdxzSureSupplier xfptPdxzSureSupplier = xfptPdxzSureSupplierService.getById(id);
        XfptQuestionnaireConfig xfptQuestionnaireConfig =new XfptQuestionnaireConfig();
        if(xfptPdxzSureSupplier!=null){
            //查询确定厂商列表
            Long teamId = xfptPdxzSureSupplier.getTeamId();
            //查询问卷配置
            xfptQuestionnaireConfig = xfptQuestionnaireConfigService.getById(xfptPdxzSureSupplier.getQuestId());
            //查询问卷选项配置
            List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyDataList = questionnaireSurveyService
                    .lambdaQuery().eq(XfptQuestionnaireSurvey::getQuestionConfigId, xfptQuestionnaireConfig.getId()).list();
            xfptQuestionnaireConfig.setSonList(xfptQuestionnaireSurveyDataList);

        }
        XfptPdxzSureSupplierVo xfptPdxzSureSupplierVo=new XfptPdxzSureSupplierVo();
        xfptPdxzSureSupplierVo.setXfptPdxzSureSupplier(xfptPdxzSureSupplier);
        xfptPdxzSureSupplierVo.setXfptQuestionnaireConfig(xfptQuestionnaireConfig);
        return success(xfptPdxzSureSupplierVo);
    }

    /**
     * 新增评定小组确定供应商
     */
    @Operation(summary = "新增评定小组确定供应商")
    //@RequiresPermissions("school:pdxzSupplier:add")
    @Log(title = "评定小组确定供应商", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional
    public AjaxResult add(@RequestBody XfptPdxzSureSupplierVo xfptPdxzSureSupplierVo) {

        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        try {
            XfptPdxzSureSupplier xfptPdxzSureSupplier = xfptPdxzSureSupplierVo.getXfptPdxzSureSupplier();
            xfptPdxzSureSupplier.setDeptId(dept.getDeptId());
            xfptPdxzSureSupplier.setAncestors(dept.getAncestors());
            XfptQuestionnaireConfig xfptQuestionnaireConfig = xfptPdxzSureSupplierVo.getXfptQuestionnaireConfig();
            xfptQuestionnaireConfig.setFbStatus(1l);
            xfptQuestionnaireConfig.setCategory("3");
            xfptQuestionnaireConfig.setUseStatus(1l);
            xfptQuestionnaireConfig.setType(ProceConstants.XYZZTP_TYPE);
            xfptQuestionnaireConfig.setDeptId(dept.getDeptId());
            xfptQuestionnaireConfig.setAncestors(dept.getAncestors());
            xfptQuestionnaireConfig.setTitle(xfptPdxzSureSupplier.getTitle());
            xfptQuestionnaireConfig.setNames(xfptPdxzSureSupplier.getNames());
            boolean save = xfptQuestionnaireConfigService.save(xfptQuestionnaireConfig);
            if (save) {
                List<XfptQuestionnaireSurvey> sonList =xfptQuestionnaireConfig.getSonList();
                if (!CollectionUtils.isEmpty(sonList)) {
                    //保存配置
                    sonList.stream().forEach(son -> son.setQuestionConfigId(xfptQuestionnaireConfig.getId()));
                    questionnaireSurveyService.saveBatch(sonList);
                }
            }
            //保存时间轴
            String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptPdxzSureSupplierVo.getXfptPdxzSureSupplier().getTeamId(), ProceConstants.PDXZ + "#" + format);
            if(i==0){
                log.error("保存时间轴失败：{}",xfptPdxzSureSupplierVo.toString());
                throw new ServiceException("系统错误");
            }
            xfptPdxzSureSupplier.setQuestId(String.valueOf(xfptQuestionnaireConfig.getId()));
            return toAjax(xfptPdxzSureSupplierService.save(xfptPdxzSureSupplier));
        } catch (Exception e) {
            return error(e.getMessage());
        }


    }

    /**
     * 修改评定小组确定供应商
     */
    @Operation(summary = "修改评定小组确定供应商")
    //@RequiresPermissions("school:pdxzSupplier:edit")
    @Log(title = "评定小组确定供应商", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional
    public AjaxResult edit(@RequestBody XfptPdxzSureSupplierVo xfptPdxzSureSupplierVo) {
        boolean b = xfptPdxzSureSupplierService.updateById(xfptPdxzSureSupplierVo.getXfptPdxzSureSupplier());
        //修改问卷配置
        XfptQuestionnaireConfig xfptQuestionnaireConfig = xfptPdxzSureSupplierVo.getXfptQuestionnaireConfig();
        xfptQuestionnaireConfig.setNames(xfptPdxzSureSupplierVo.getXfptPdxzSureSupplier().getNames());
        xfptQuestionnaireConfig.setTitle(xfptPdxzSureSupplierVo.getXfptPdxzSureSupplier().getTitle());
        boolean b1 = xfptQuestionnaireConfigService.updateById(xfptQuestionnaireConfig);
        if(!b1){
            log.error("修改问卷配置错误：{}",xfptPdxzSureSupplierVo.getXfptQuestionnaireConfig().getId());
            throw new ServiceException("系统错误");
        }
        //修改问卷选项
        List<XfptQuestionnaireSurvey> sonList = xfptPdxzSureSupplierVo.getXfptQuestionnaireConfig().getSonList();
        boolean b2 = questionnaireSurveyService.updateBatchById(sonList);
        if(!b2){
            log.error("修改问卷选项错误：{}",sonList);
            throw new ServiceException("系统错误");
        }

        return toAjax(b);
    }


    /**
     * 删除评定小组确定供应商
     */
    @Operation(summary = "删除评定小组确定供应商")
   // @RequiresPermissions("school:pdxzSupplier:remove")
    @Log(title = "评定小组确定供应商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        //删除问卷数据
        List<XfptPdxzSureSupplier> list = xfptPdxzSureSupplierService.lambdaQuery().in(XfptPdxzSureSupplier::getId, ids).list();
        for (XfptPdxzSureSupplier xfptPdxzSureSupplier : list) {
            boolean b = xfptQuestionnaireConfigService.removeById(Long.valueOf(xfptPdxzSureSupplier.getQuestId()));
            if(!b){
                log.error("删除问卷错误：{}",xfptPdxzSureSupplier);
                throw new ServiceException("系统错误");
            }
            //删除子项
            LambdaQueryWrapper<XfptQuestionnaireSurvey> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(XfptQuestionnaireSurvey::getQuestionConfigId,xfptPdxzSureSupplier.getQuestId());
            boolean b2 = questionnaireSurveyService.remove(lambdaQueryWrapper);
            if(!b2){
                log.error("删除问卷选项错误：{}",xfptPdxzSureSupplier);
                throw new ServiceException("系统错误");
            }
        }

        boolean b1 = xfptPdxzSureSupplierService.removeByIds(Arrays.asList(ids));
        if(!b1){
            log.error("删除评定小组确定供应商：{}",ids);
            throw new ServiceException("系统错误");
        }

        return toAjax(b1);
    }

    /**
     * 查询供应商列表
     * @param teamId
     * @return
     */
    @GetMapping("/getSupplie")
    public AjaxResult getSupplie(Long teamId) {
        List<XfptSupplierList> list = iXfptSupplierListService.lambdaQuery().eq(XfptSupplierList::getRemark, teamId).list();
        return AjaxResult.success(list);
    }
}
