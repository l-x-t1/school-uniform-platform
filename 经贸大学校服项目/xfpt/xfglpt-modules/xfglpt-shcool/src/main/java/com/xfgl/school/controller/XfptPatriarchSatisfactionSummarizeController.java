package com.xfgl.school.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.utils.CommUtil;
import com.xfgl.common.core.utils.DateUtils;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.service.*;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 选用组织家长满意度总结报告报备与公开Controller
 *
 * @author yinet
 * @date 2025-04-27
 */
@Tag(name = "选用组织家长满意度总结报告报备与公开管理")
@RestController
@RequestMapping("/satisfactionSummarize")
public class XfptPatriarchSatisfactionSummarizeController extends BaseController {
    @Autowired
    private IXfptPatriarchSatisfactionSummarizeService xfptPatriarchSatisfactionSummarizeService;

    @Autowired
    private IXfptBuyGradeClassService buyGradeClassService;

    @Autowired
    private IXfptQuestionnaireConfigService xfptQuestionnaireConfigService;
    @Autowired
    private IXfptQuestionnaireSurveyService questionnaireSurveyService;
    @Autowired
    private ITeamMeetingService teamMeetingService;
    @Autowired
    private IXfptQuestionnaireResultService iXfptQuestionnaireResultService;

    /**
     * 查询选用组织家长满意度总结报告报备与公开列表
     */

    @Operation(summary = "查询选用组织家长满意度总结报告报备与公开列表")
//@RequiresPermissions("school:summarize:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize) {
        startPage();
        xfptPatriarchSatisfactionSummarize.setDeptId(null);
        List<XfptPatriarchSatisfactionSummarize> list = xfptPatriarchSatisfactionSummarizeService.selectXfptPatriarchSatisfactionSummarizeList(xfptPatriarchSatisfactionSummarize);

        //获取所有线上的活动数据
        List<XfptPatriarchSatisfactionSummarize> no_start_list = list.stream().filter(e -> e.getSummarizeReportType().contains("线上")
                && ((StringUtils.isNotEmpty(e.getState()) && !e.getState().equals("2")) || StringUtils.isNotEmpty(e.getState()))).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(no_start_list)) {
            for (XfptPatriarchSatisfactionSummarize satisfactionSummarize : no_start_list) {
                //开始时间
                LocalDateTime startTime = new Timestamp(satisfactionSummarize.getStartTime().getTime()).toLocalDateTime();
                //结束时间
                LocalDateTime endTime = new Timestamp(satisfactionSummarize.getEndTime().getTime()).toLocalDateTime();
                LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();
                if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
                    // "在时间段内";
                    satisfactionSummarize.setState("1");
                } else if (date.isBefore(startTime)) {
                    //"在时间段前";
                    satisfactionSummarize.setState("0");
                } else {
                    //"在时间段后";
                    satisfactionSummarize.setState("2");
                }
                xfptPatriarchSatisfactionSummarizeService.updateById(satisfactionSummarize);
            }

        }
        if(CollUtil.isNotEmpty(list)){
            for (XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize1 : list) {
                //查询投票人数
                List<XfptQuestionnaireResult> list1 = iXfptQuestionnaireResultService.lambdaQuery().eq(XfptQuestionnaireResult::getQuestionnaireSurveyId
                        , xfptPatriarchSatisfactionSummarize1.getQuestionnaireSurveyId()).list();
                if(CollUtil.isNotEmpty(list1)){
                    //去重
                    List<XfptQuestionnaireResult> collect = list1.stream().filter(CommUtil.distinctPredicate(XfptQuestionnaireResult::getName)).collect(Collectors.toList());
                    xfptPatriarchSatisfactionSummarize1.setVotedCount(collect.size());
                    //计算投票率，四舍五入
                    xfptPatriarchSatisfactionSummarize1.setVotingRate(new BigDecimal(xfptPatriarchSatisfactionSummarize1.getVotedCount()).divide(new BigDecimal(xfptPatriarchSatisfactionSummarize1.getCount()),
                            2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }else {

                    xfptPatriarchSatisfactionSummarize1.setVotingRate(0);
                    xfptPatriarchSatisfactionSummarize1.setVotedCount(0);
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出选用组织家长满意度总结报告报备与公开列表
     */
    @Operation(summary = "导出选用组织家长满意度总结报告报备与公开列表")
//    @RequiresPermissions("school:summarize:export")
    @Log(title = "选用组织家长满意度总结报告报备与公开", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize) {
        List<XfptPatriarchSatisfactionSummarize> list = xfptPatriarchSatisfactionSummarizeService.selectXfptPatriarchSatisfactionSummarizeList(xfptPatriarchSatisfactionSummarize);
        ExcelUtil<XfptPatriarchSatisfactionSummarize> util = new ExcelUtil<XfptPatriarchSatisfactionSummarize>(XfptPatriarchSatisfactionSummarize.class);
        util.exportExcel(response, list, "选用组织家长满意度总结报告报备与公开数据");
    }

    /**
     * 获取选用组织家长满意度总结报告报备与公开详细信息
     */
    @Operation(summary = "获取选用组织家长满意度总结报告报备与公开详细信息")
//    @RequiresPermissions("school:summarize:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize = xfptPatriarchSatisfactionSummarizeService.getById(id);
        XfptBuyGradeClass xfptBuyGradeClass = new XfptBuyGradeClass();
        xfptBuyGradeClass.setCategory(ProceConstants.XYZZ_JZMYD_NJ_CATEGORY);
        xfptBuyGradeClass.setPid(id);
        List<XfptBuyGradeClass> xfptBuyGradeClassList = buyGradeClassService.selectXfptBuyGradeClassList(xfptBuyGradeClass);
        xfptPatriarchSatisfactionSummarize.setXfptBuyGradeClassList(xfptBuyGradeClassList);
        XfptQuestionnaireConfig xfptQuestionnaireConfig = xfptQuestionnaireConfigService.getById(xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId());
        xfptPatriarchSatisfactionSummarize.setXfptQuestionnaireConfig(xfptQuestionnaireConfig);

//        XfptQuestionnaireSurvey xfptQuestionnaireSurvey=new XfptQuestionnaireSurvey();
//        xfptQuestionnaireSurvey.setQuestionConfigId(xfptQuestionnaireConfig.getId());
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyList = questionnaireSurveyService.lambdaQuery()
                .eq(XfptQuestionnaireSurvey::getQuestionConfigId, xfptQuestionnaireConfig.getId())
                .list();
        xfptPatriarchSatisfactionSummarize.setXfptQuestionnaireSurveys(xfptQuestionnaireSurveyList);
        //查询问卷
        List<XfptQuestionnaireSurvey> list = questionnaireSurveyService.lambdaQuery()
                .eq(XfptQuestionnaireSurvey::getQuestionConfigId, xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId()).list();
        xfptPatriarchSatisfactionSummarize.setSonList(list);
        return success(xfptPatriarchSatisfactionSummarize);
    }

    /**
     * 新增选用组织家长满意度总结报告报备与公开
     */
    @Operation(summary = "新增选用组织家长满意度总结报告报备与公开")
//    @RequiresPermissions("school:summarize:add")
    @Log(title = "选用组织家长满意度总结报告报备与公开", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize) {
        String state = "";
        if (xfptPatriarchSatisfactionSummarize.getStartTime() != null && xfptPatriarchSatisfactionSummarize.getEndTime() != null) {
            //开始时间
            LocalDateTime startTime = new Timestamp(xfptPatriarchSatisfactionSummarize.getStartTime().getTime()).toLocalDateTime();
            //结束时间
            LocalDateTime endTime = new Timestamp(xfptPatriarchSatisfactionSummarize.getEndTime().getTime()).toLocalDateTime();
            LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();

            if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
                // "在时间段内";
                state = "1";
                xfptPatriarchSatisfactionSummarize.setState("1");
            } else if (date.isBefore(startTime)) {
                //"在时间段前";
                state = "0";
                xfptPatriarchSatisfactionSummarize.setState("0");
            } else {
                //"在时间段后";
                state = "2";
                xfptPatriarchSatisfactionSummarize.setState("2");
            }
        }

        String surveyId = xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId();
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        Long deptId = dept.getDeptId();
        String ancestors = dept.getAncestors();
        List<XfptPatriarchSatisfactionSummarize> lists = xfptPatriarchSatisfactionSummarizeService.lambdaQuery()
                .eq(XfptPatriarchSatisfactionSummarize::getDeptId, xfptPatriarchSatisfactionSummarize.getDeptId())
                .eq(XfptPatriarchSatisfactionSummarize::getTeamMeetingId, xfptPatriarchSatisfactionSummarize.getTeamMeetingId())
                .list();
        if (lists.isEmpty()) {
            teamMeetingService.updateTeamMeetingCurrOrder(Long.valueOf(xfptPatriarchSatisfactionSummarize.getTeamMeetingId()), ProceConstants.MDYBB + "#" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        XfptQuestionnaireConfig xfptQuestionnaireConfig = new XfptQuestionnaireConfig();
        xfptQuestionnaireConfig.setTitle(xfptPatriarchSatisfactionSummarize.getSummarizeReportName());
        xfptQuestionnaireConfig.setBeginTime(xfptPatriarchSatisfactionSummarize.getStartTime());
        xfptQuestionnaireConfig.setEndTime(xfptPatriarchSatisfactionSummarize.getEndTime());
        xfptQuestionnaireConfig.setCategory("2");
        xfptQuestionnaireConfig.setFbStatus(1L);
        xfptQuestionnaireConfig.setType(ProceConstants.XYZZ_JZMYD_NJ_TYPE);
        xfptQuestionnaireConfig.setDeptId(deptId);
        xfptQuestionnaireConfig.setAncestors(ancestors);
        xfptQuestionnaireConfig.setWjStatus(state);
        xfptQuestionnaireConfig.setVotingNotice(xfptPatriarchSatisfactionSummarize.getVotingNotice());
        xfptQuestionnaireConfig.setNames(xfptPatriarchSatisfactionSummarize.getNames());
        xfptQuestionnaireConfigService.save(xfptQuestionnaireConfig);
        List<XfptBuyGradeClass> xfptBuyGradeClassList = xfptPatriarchSatisfactionSummarize.getXfptBuyGradeClassList();
        xfptPatriarchSatisfactionSummarize.setQuestionnaireSurveyId(xfptQuestionnaireConfig.getId().toString());
        xfptPatriarchSatisfactionSummarize.setAncestors(ancestors);
        xfptPatriarchSatisfactionSummarizeService.save(xfptPatriarchSatisfactionSummarize);
//        for (XfptBuyGradeClass xfptBuyGradeClass : xfptBuyGradeClassList) {
//            xfptBuyGradeClass.setCategory(ProceConstants.XYZZ_JZMYD_NJ_CATEGORY);
//            xfptBuyGradeClass.setPid(xfptPatriarchSatisfactionSummarize.getId());
//            buyGradeClassService.insertXfptBuyGradeClass(xfptBuyGradeClass);
//        }


//        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = questionnaireSurveyService.lambdaQuery()
//                .eq(XfptQuestionnaireSurvey::getQuestionConfigId, surveyId)
//                .list();
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = xfptPatriarchSatisfactionSummarize.getSonList();
        xfptQuestionnaireSurveys.stream().forEach(e -> {
            e.setQuestionConfigId(xfptQuestionnaireConfig.getId());
            e.setId(null);
        });
        questionnaireSurveyService.saveBatch(xfptQuestionnaireSurveys);
        return toAjax(1);
    }

    /**
     * 修改选用组织家长满意度总结报告报备与公开
     */
    @Operation(summary = "修改选用组织家长满意度总结报告报备与公开")
//    @RequiresPermissions("school:summarize:edit")
    @Log(title = "选用组织家长满意度总结报告报备与公开", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize) {
        String state = "";
        if (xfptPatriarchSatisfactionSummarize.getStartTime() != null && xfptPatriarchSatisfactionSummarize.getEndTime() != null) {
            //开始时间
            LocalDateTime startTime = new Timestamp(xfptPatriarchSatisfactionSummarize.getStartTime().getTime()).toLocalDateTime();
            //结束时间
            LocalDateTime endTime = new Timestamp(xfptPatriarchSatisfactionSummarize.getEndTime().getTime()).toLocalDateTime();
            LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();

            if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
                // "在时间段内";
                state = "1";
                xfptPatriarchSatisfactionSummarize.setState("1");
            } else if (date.isBefore(startTime)) {
                //"在时间段前";
                state = "0";
                xfptPatriarchSatisfactionSummarize.setState("0");
            } else {
                //"在时间段后";
                state = "2";
                xfptPatriarchSatisfactionSummarize.setState("2");
            }
        }
        //删除问卷调查
        //boolean b = xfptQuestionnaireConfigService.removeById(xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId());
        //List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyDataList = xfptPatriarchSatisfactionSummarize.getSonList();
        //if (CollectionUtils.isEmpty(xfptQuestionnaireSurveyDataList)) {
        //    return error("没有获取到问卷的配置项，请联系管理员！");
        //}
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        Long deptId = dept.getDeptId();
        String ancestors = dept.getAncestors();
//        XfptQuestionnaireConfig xfptQuestionnaireConfig = new XfptQuestionnaireConfig();
//        xfptQuestionnaireConfig.setTitle(xfptPatriarchSatisfactionSummarize.getSummarizeReportName());
//        xfptQuestionnaireConfig.setBeginTime(xfptPatriarchSatisfactionSummarize.getStartTime());
//        xfptQuestionnaireConfig.setEndTime(xfptPatriarchSatisfactionSummarize.getEndTime());
//        xfptQuestionnaireConfig.setCategory("2");
//        xfptQuestionnaireConfig.setFbStatus(1L);
//        xfptQuestionnaireConfig.setType(ProceConstants.XYZZ_JZMYD_NJ_TYPE);
//        xfptQuestionnaireConfig.setDeptId(deptId);
//        xfptQuestionnaireConfig.setAncestors(ancestors);
//        xfptQuestionnaireConfig.setWjStatus(state);
//        xfptQuestionnaireConfig.setVotingNotice(xfptPatriarchSatisfactionSummarize.getVotingNotice());
//        xfptQuestionnaireConfig.setNames(xfptPatriarchSatisfactionSummarize.getNames());
//        xfptQuestionnaireConfigService.save(xfptQuestionnaireConfig);
//        xfptQuestionnaireSurveyDataList.stream().forEach(e -> {
//            e.setQuestionConfigId(Long.parseLong(xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId()));
//            //e.setId(null);
//        });
        //xfptPatriarchSatisfactionSummarize.setQuestionnaireSurveyId(xfptQuestionnaireConfig.getId().toString());
        return toAjax(xfptPatriarchSatisfactionSummarizeService.updateById(xfptPatriarchSatisfactionSummarize));
    }


    /**
     * 删除选用组织家长满意度总结报告报备与公开
     */
    @Operation(summary = "删除选用组织家长满意度总结报告报备与公开")
//    @RequiresPermissions("school:summarize:remove")
    @Log(title = "选用组织家长满意度总结报告报备与公开", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id) {
        XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize = xfptPatriarchSatisfactionSummarizeService.getById(id);
        xfptQuestionnaireConfigService.removeById(Long.valueOf(xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId()));
        LambdaQueryWrapper<XfptQuestionnaireSurvey> queryWrapper = Wrappers.<XfptQuestionnaireSurvey>lambdaQuery()
                .eq(XfptQuestionnaireSurvey::getQuestionConfigId, xfptPatriarchSatisfactionSummarize.getQuestionnaireSurveyId());
        questionnaireSurveyService.remove(queryWrapper);
        return toAjax(xfptPatriarchSatisfactionSummarizeService.removeById(Long.valueOf(id)));
    }
}


