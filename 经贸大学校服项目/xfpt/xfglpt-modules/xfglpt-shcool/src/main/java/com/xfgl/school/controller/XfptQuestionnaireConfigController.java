package com.xfgl.school.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptQuestionnaireResult;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.service.*;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptQuestionnaireConfig;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 问卷调查主配置Controller
 *
 * @author wxm
 * @date 2025-04-27
 */
@Tag(name = "问卷调查主配置管理")
@RestController
@RequestMapping("/question/config")
public class XfptQuestionnaireConfigController extends BaseController {
    @Autowired
    private IXfptQuestionnaireConfigService xfptQuestionnaireConfigService;
    @Autowired
    private IXfptQuestionnaireSurveyService questionnaireSurveyService;

    @Autowired
    private IXfptQuestionnaireResultService xfptQuestionnaireResultService;

    @Autowired
    private IXfptSchoolGradeClassService xfptSchoolGradeClassService;


    /**
     * 获取当前部门的问卷模板类型数据
     *
     * @return
     */
    @GetMapping("/getQuestionnaireTemplateConfig")
    public AjaxResult getQuestionnaireConfig() {
        List<XfptQuestionnaireConfig> templateList = xfptQuestionnaireConfigService.selectTemplateQuestionnaireConfigList();
        return success(templateList);
    }


    /**
     * 查询问卷调查主配置列表
     */
    @Operation(summary = "查询问卷调查主配置列表")
    //@RequiresPermissions("question:config:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        SysDept dept = currentDept();
        xfptQuestionnaireConfig.setViewerDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        xfptQuestionnaireConfig.setViewerAncestors(dept == null ? null : dept.getAncestors());
        xfptQuestionnaireConfig.setViewerIsSchool(dept == null ? null : dept.getIsSchool());
        xfptQuestionnaireConfig.setDeptId(null);
        xfptQuestionnaireConfig.setAncestors(null);
        startPage();
        List<XfptQuestionnaireConfig> list = xfptQuestionnaireConfigService.selectXfptQuestionnaireConfigList(xfptQuestionnaireConfig);
        for (XfptQuestionnaireConfig config : list) {
            if (!config.getCategory().equals("1")) {
                if (config.getBeginTime() != null && config.getEndTime() != null) {
                    //开始时间
                    LocalDateTime startTime = new Timestamp(config.getBeginTime().getTime()).toLocalDateTime();
                    //结束时间
                    LocalDateTime endTime = new Timestamp(config.getEndTime().getTime()).toLocalDateTime();
                    LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();
                    if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
                        // "在时间段内";
                        config.setWjStatus("1");
                    } else if (date.isBefore(startTime)) {
                        //"在时间段前";
                        config.setWjStatus("0");
                    } else {
                        //"在时间段后";
                        config.setWjStatus("2");
                    }
                }

            }

            SysDept sysDept = xfptSchoolGradeClassService.selectDeptId(config.getDeptId().toString());
            config.setCreateBy(sysDept.getDeptName());
        }
        return getDataTable(list);
    }

    @Autowired
    private IXfptStudentInfoService iXfptStudentInfoService;
    /**
     * 手机端查询问卷调查主配置列表
     */
    //@RequiresPermissions("question:config:list")
    @GetMapping("/list2")
    public TableDataInfo list2(XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        List<XfptStudentInfo> list1 = iXfptStudentInfoService.lambdaQuery().eq(XfptStudentInfo::getSimId, xfptQuestionnaireConfig.getId())
                .list();
        List<String> list2=new ArrayList<>();
        if(CollUtil.isNotEmpty(list1)){
            list2 = list1.stream().map(XfptStudentInfo::getShcoolId).collect(Collectors.toList());
            xfptQuestionnaireConfig.setDeptIds(list2);
        }

        startPage();
        List<XfptQuestionnaireConfig> list = xfptQuestionnaireConfigService.selectXfptQuestionnaireConfigList2(xfptQuestionnaireConfig);
        for (XfptQuestionnaireConfig config : list) {
            if (!config.getCategory().equals("1")) {
                if (config.getBeginTime() != null && config.getEndTime() != null) {
                    //开始时间
                    LocalDateTime startTime = new Timestamp(config.getBeginTime().getTime()).toLocalDateTime();
                    //结束时间
                    LocalDateTime endTime = new Timestamp(config.getEndTime().getTime()).toLocalDateTime();
                    LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();
                    if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
                        // "在时间段内";
                        config.setWjStatus("1");
                    } else if (date.isBefore(startTime)) {
                        //"在时间段前";
                        config.setWjStatus("0");
                    } else {
                        //"在时间段后";
                        config.setWjStatus("2");
                    }
                }

            }

            SysDept sysDept = xfptSchoolGradeClassService.selectDeptId(config.getDeptId().toString());
            config.setCreateBy(sysDept.getDeptName());
        }
        return getDataTable(list);
    }

    /**
     * 导出问卷调查主配置列表
     */
//    @Operation(summary = "导出问卷调查主配置列表")
//    //@RequiresPermissions("question:config:export")
//    @Log(title = "问卷调查主配置", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, XfptQuestionnaireConfig xfptQuestionnaireConfig) {
//        List<XfptQuestionnaireConfig> list = xfptQuestionnaireConfigService.selectXfptQuestionnaireConfigList(xfptQuestionnaireConfig);
//        ExcelUtil<XfptQuestionnaireConfig> util = new ExcelUtil<XfptQuestionnaireConfig>(XfptQuestionnaireConfig.class);
//        util.exportExcel(response, list, "问卷调查主配置数据");
//    }

    /**
     * 获取问卷调查主配置详细信息
     */
    @Operation(summary = "获取问卷调查主配置详细信息")
    //@RequiresPermissions("question:config:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        if (id == null) {
            return error("获取标识失败！");
        }
        XfptQuestionnaireConfig baseDataEntity = xfptQuestionnaireConfigService.getById(id);
        if (baseDataEntity == null) {
            return error("获取数据失败！");
        }
        List<XfptQuestionnaireSurvey> sonList = new ArrayList<>();
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = questionnaireSurveyService.selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey.builder().questionConfigId(id).build());
        if (!CollectionUtils.isEmpty(xfptQuestionnaireSurveys)) {
            sonList = xfptQuestionnaireSurveys;
        }
        baseDataEntity.setSonList(sonList);
        return success(baseDataEntity);
    }

    /**
     * 新增问卷调查主配置
     */
    @Operation(summary = "新增问卷调查主配置")
    //@RequiresPermissions("question:config:add")
    @Log(title = "问卷调查主配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        try {
            fillOwnerScope(xfptQuestionnaireConfig);
//            if(xfptQuestionnaireConfig.getCategory().equals("0")){
//                //开始时间
//                LocalDateTime startTime = new Timestamp(xfptQuestionnaireConfig.getBeginTime().getTime()).toLocalDateTime();
//                //结束时间
//                LocalDateTime endTime = new Timestamp(xfptQuestionnaireConfig.getEndTime().getTime()).toLocalDateTime();
//                LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();
//                if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
//                    // "在时间段内";
//                    xfptQuestionnaireConfig.setWjStatus("1");
//                } else if (date.isBefore(startTime)) {
//                    //"在时间段前";
//                    xfptQuestionnaireConfig.setWjStatus("0");
//                } else {
//                    //"在时间段后";
//                    xfptQuestionnaireConfig.setWjStatus("2");
//                }
//            }
            boolean save = xfptQuestionnaireConfigService.save(xfptQuestionnaireConfig);
            if (save) {
                List<XfptQuestionnaireSurvey> sonList = xfptQuestionnaireConfig.getSonList();
                if (!CollectionUtils.isEmpty(sonList)) {
                    //保存配置
                    sonList.stream().forEach(son -> son.setQuestionConfigId(xfptQuestionnaireConfig.getId()));
                    questionnaireSurveyService.saveBatch(sonList);
                }
            }
            return toAjax(1);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 修改问卷调查主配置
     */
    @Operation(summary = "修改问卷调查主配置")
    //@RequiresPermissions("question:config:edit")
    @Log(title = "问卷调查主配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        try {
            XfptQuestionnaireConfig existingConfig = xfptQuestionnaireConfig.getId() == null
                    ? null
                    : xfptQuestionnaireConfigService.getById(xfptQuestionnaireConfig.getId());
            if (existingConfig == null || !canManageQuestionnaire(existingConfig)) {
                return error("无权限操作该问卷");
            }
            keepOwnerScope(xfptQuestionnaireConfig, existingConfig);
            boolean updateFlg = xfptQuestionnaireConfigService.updateById(xfptQuestionnaireConfig);
            if (updateFlg) {
                List<XfptQuestionnaireSurvey> sonList = xfptQuestionnaireConfig.getSonList();
                if (!CollectionUtils.isEmpty(sonList)) {
                    sonList.stream().forEach(e -> e.setQuestionConfigId(xfptQuestionnaireConfig.getId()));
                    questionnaireSurveyService.saveOrUpdateBatch(sonList);
                }
            }
            return toAjax(1);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }


    /**
     * 删除问卷调查主配置
     */
    @Operation(summary = "删除问卷调查主配置")
    //@RequiresPermissions("question:config:remove")
    @Log(title = "问卷调查主配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        try {
            List<Long> questionIdList = Arrays.asList(ids);
            if (!canManageQuestionnaires(questionIdList)) {
                return error("无权限操作该问卷");
            }
            boolean delFlg = xfptQuestionnaireConfigService.removeByIds(questionIdList);
            if (delFlg) {
                LambdaUpdateWrapper<XfptQuestionnaireSurvey> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.in(XfptQuestionnaireSurvey::getQuestionConfigId, questionIdList);
                lambdaUpdateWrapper.set(XfptQuestionnaireSurvey::getDelFlag, "1");
                questionnaireSurveyService.update(null, lambdaUpdateWrapper);
            }
            return toAjax(1);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 逻辑删除子表数据
     *
     * @param ids
     * @return
     */
    @Operation(summary = "删除问卷调查子表数据")
    //@RequiresPermissions("question:config:remove")
    @Log(title = "问卷调查主配置子表删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/removeSonData/{ids}")
    public AjaxResult removeSonData(@PathVariable Long[] ids) {
        try {
            List<XfptQuestionnaireSurvey> surveyList = questionnaireSurveyService.listByIds(Arrays.asList(ids));
            List<Long> questionIdList = surveyList.stream()
                    .map(XfptQuestionnaireSurvey::getQuestionConfigId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(questionIdList) && !canManageQuestionnaires(questionIdList)) {
                return error("无权限操作该问卷");
            }
            questionnaireSurveyService.removeByIds(Arrays.asList(ids));
            return toAjax(1);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @Operation(summary = "导出问卷调查主配置列表")
    //@RequiresPermissions("question:config:export")
    @Log(title = "问卷调查主配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        List<XfptQuestionnaireConfig> list = xfptQuestionnaireConfigService.selectXfptQuestionnaireConfigList(xfptQuestionnaireConfig);
        ExcelUtil<XfptQuestionnaireConfig> util = new ExcelUtil<XfptQuestionnaireConfig>(XfptQuestionnaireConfig.class);
        util.exportExcel(response, list, "问卷调查主配置数据");
    }

    //问卷统计
    @GetMapping("/statistics")
    public AjaxResult statistics(XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        XfptQuestionnaireConfig questionnaireConfig = xfptQuestionnaireConfigService.getById(xfptQuestionnaireConfig.getId());
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = questionnaireSurveyService.selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey.builder().questionConfigId(questionnaireConfig.getId()).build());
        //单选
        int multipleChoice = 0;
        //多选
        int multipleResponse = 0;
        //文本
        int text = 0;
        for (XfptQuestionnaireSurvey questionnaireSurvey : xfptQuestionnaireSurveys) {
            if (questionnaireSurvey.getAnsweringMethod().equals("2") || questionnaireSurvey.getAnsweringMethod().equals("3")) {
                //文本
                XfptQuestionnaireResult xfptQuestionnaireResult = new XfptQuestionnaireResult();
                xfptQuestionnaireResult.setQuestionnaireSurveyId(questionnaireConfig.getId().toString());
                xfptQuestionnaireResult.setSurveyId(questionnaireSurvey.getId().toString());
                List<XfptQuestionnaireResult> xfptQuestionnaireResults = xfptQuestionnaireResultService.selectXfptQuestionnaireResultList(xfptQuestionnaireResult);
                questionnaireSurvey.setXfptQuestionnaireResults(xfptQuestionnaireResults);
                questionnaireSurvey.setAmount(xfptQuestionnaireResults.size());
                text++;
            } else {
                int sum = 0;
                String surveyResults[] = questionnaireSurvey.getSurveyResult().split("、");
                List<XfptQuestionnaireResult> list = new ArrayList<>();
                for (int i = 0; i < surveyResults.length; i++) {
                    XfptQuestionnaireResult xfptQuestionnaireResult = new XfptQuestionnaireResult();
                    xfptQuestionnaireResult.setQuestionnaireSurveyId(questionnaireConfig.getId().toString());
                    xfptQuestionnaireResult.setSurveyId(questionnaireSurvey.getId().toString());
                    xfptQuestionnaireResult.setQuestionnaireResult(surveyResults[i]);
                    List<XfptQuestionnaireResult> xfptQuestionnaireResults = xfptQuestionnaireResultService.selectXfptQuestionnaireResultList(xfptQuestionnaireResult);
                    int size = xfptQuestionnaireResults.size();
                    sum += size;
                    xfptQuestionnaireResult.setAmount(size);
                    list.add(xfptQuestionnaireResult);
                }
                if (questionnaireSurvey.getAnsweringMethod().equals("0")) {
                    //单选
                    multipleChoice++;
                } else if (questionnaireSurvey.getAnsweringMethod().equals("1")) {
                    multipleResponse++;
                }
                int a = sum;
                list.stream().forEach(e -> {
                    double v = ((double) e.getAmount() / a) * 100;
                    String s = String.format("%.2f", v) + "%";
                    if (s.equals("NaN%")) {
                        s = "0%";
                    }
                    e.setPercentage(s);
                });
                questionnaireSurvey.setXfptQuestionnaireResults(list);
            }
        }
        questionnaireConfig.setSonList(xfptQuestionnaireSurveys);
        questionnaireConfig.setMultipleChoice(multipleChoice);
        questionnaireConfig.setMultipleResponse(multipleResponse);
        questionnaireConfig.setText(text);
        return success(questionnaireConfig);
    }


    @PostMapping("/content")
    public AjaxResult content(String id) {
        if (id == null) {
            return error("获取标识失败！");
        }
        XfptQuestionnaireConfig baseDataEntity = xfptQuestionnaireConfigService.getById(id);
        if (baseDataEntity == null) {
            return error("获取数据失败！");
        }
        if (baseDataEntity.getBeginTime() != null && baseDataEntity.getEndTime() != null) {
            //开始时间
            LocalDateTime startTime = new Timestamp(baseDataEntity.getBeginTime().getTime()).toLocalDateTime();
            //结束时间
            LocalDateTime endTime = new Timestamp(baseDataEntity.getEndTime().getTime()).toLocalDateTime();
            LocalDateTime date = new Timestamp(new Date().getTime()).toLocalDateTime();
            if (!date.isBefore(startTime) && !date.isAfter(endTime)) {
                // "在时间段内";
                baseDataEntity.setState("1");
            } else if (date.isBefore(startTime)) {
                //"在时间段前";
                baseDataEntity.setState("0");
            } else {
                //"在时间段后";
                baseDataEntity.setState("2");
            }
        }

        List<XfptQuestionnaireSurvey> sonList = new ArrayList<>();
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = questionnaireSurveyService.selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey.builder().questionConfigId(Long.valueOf(id)).build());
        if (!CollectionUtils.isEmpty(xfptQuestionnaireSurveys)) {
            sonList = xfptQuestionnaireSurveys;
        }
        baseDataEntity.setSonList(sonList);
        return success(baseDataEntity);
    }

    @GetMapping("/selectName")
    public AjaxResult selectName(String name, String questId) {
        XfptQuestionnaireConfig xfptQuestionnaireConfig = xfptQuestionnaireConfigService.getById(questId);
        String names = xfptQuestionnaireConfig.getNames();
        if (StrUtil.isEmpty(names)) {
            return AjaxResult.success(null);
        }
        List<String> list = Arrays.asList(names.split(","));
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if(s.contains("-")){
                String s1 = s.split("-")[0];
                if(s1.equals(name)){
                    result.add(s1);
                }
            }else {
                if(s.equals(name)){
                    result.add(s);
                }
            }

//            if (s.contains(name)) {
//                if(s.contains("-")){
//                    result.add(s.split("-")[0]);
//                }else {
//                    result.add(s);
//                }
//            }
        }
        //result去重
        Set<String> set = new HashSet<>(result);


        return AjaxResult.success(set);
    }


    /**
     * 查询流程模版
     *
     * @param type 1校服意见征集   2家长满意度  4班子会议
     * @return
     */
    @GetMapping("/getConfigByType")
    public AjaxResult getConfigByLcCatagory(String type) {
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        //查询最后一次的模版
        XfptQuestionnaireConfig xfptQuestionnaireConfig = xfptQuestionnaireConfigService.lambdaQuery()
                .eq(XfptQuestionnaireConfig::getType, type)
                .eq(XfptQuestionnaireConfig::getDeptId, deptId)
                .eq(XfptQuestionnaireConfig::getLcCategory, "1")
                .orderByDesc(XfptQuestionnaireConfig::getCreateTime)
                .last("limit 1").one();
        if(xfptQuestionnaireConfig!=null){
            //查询子选项
            List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys =questionnaireSurveyService.lambdaQuery()
                    .eq(XfptQuestionnaireSurvey::getQuestionConfigId,xfptQuestionnaireConfig.getId()).list();
            xfptQuestionnaireConfig.setSonList(xfptQuestionnaireSurveys);
            return AjaxResult.success(xfptQuestionnaireConfig);
        }
        //查询最后一次的模版
         xfptQuestionnaireConfig = xfptQuestionnaireConfigService.lambdaQuery()
                .eq(XfptQuestionnaireConfig::getType, type)
                .eq(XfptQuestionnaireConfig::getLcCategory, "0")
                .orderByDesc(XfptQuestionnaireConfig::getCreateTime)
                .last("limit 1").one();
        //查询子选项
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys =questionnaireSurveyService.lambdaQuery()
                .eq(XfptQuestionnaireSurvey::getQuestionConfigId,xfptQuestionnaireConfig.getId()).list();
        xfptQuestionnaireConfig.setSonList(xfptQuestionnaireSurveys);
        //如果没有查询配置的模版
        return AjaxResult.success(xfptQuestionnaireConfig);
    }

    private SysDept currentDept() {
        return SecurityUtils.getLoginUser().getSysUser().getDept();
    }

    private boolean isSchoolDept(SysDept dept) {
        return dept != null && "1".equals(dept.getIsSchool());
    }

    private void fillOwnerScope(XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        SysDept dept = currentDept();
        xfptQuestionnaireConfig.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        xfptQuestionnaireConfig.setAncestors(dept == null ? null : dept.getAncestors());
        xfptQuestionnaireConfig.setLcCategory(isSchoolDept(dept) ? "1" : "0");
    }

    private void keepOwnerScope(XfptQuestionnaireConfig target, XfptQuestionnaireConfig source) {
        target.setDeptId(source.getDeptId());
        target.setAncestors(source.getAncestors());
        target.setLcCategory(source.getLcCategory());
    }

    private boolean canManageQuestionnaire(Long id) {
        XfptQuestionnaireConfig questionnaireConfig = xfptQuestionnaireConfigService.getById(id);
        if (questionnaireConfig == null) {
            return false;
        }
        return canManageQuestionnaire(questionnaireConfig);
    }

    private boolean canManageQuestionnaire(XfptQuestionnaireConfig questionnaireConfig) {
        Long viewerDeptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        if (Objects.equals(questionnaireConfig.getDeptId(), viewerDeptId)) {
            return true;
        }
        SysDept dept = currentDept();
        if (isSchoolDept(dept)) {
            return false;
        }
        SysDept ownerDept = questionnaireConfig.getDeptId() == null
                ? null
                : xfptSchoolGradeClassService.selectDeptId(String.valueOf(questionnaireConfig.getDeptId()));
        if (!isSchoolDept(ownerDept)) {
            return false;
        }
        return containsDept(questionnaireConfig.getAncestors(), viewerDeptId)
                || containsDept(ownerDept.getAncestors(), viewerDeptId);
    }

    private boolean containsDept(String ancestors, Long deptId) {
        if (StrUtil.isBlank(ancestors) || deptId == null) {
            return false;
        }
        return Arrays.asList(ancestors.split(",")).contains(String.valueOf(deptId));
    }

    private boolean canManageQuestionnaires(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return true;
        }
        for (Long id : ids) {
            if (!canManageQuestionnaire(id)) {
                return false;
            }
        }
        return true;
    }

}
