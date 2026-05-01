package com.xfgl.school.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.CommUtil;
import com.xfgl.common.core.utils.DateUtils;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.service.*;
import com.xfgl.system.api.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 家长购买意见征集Controller
 *
 * @author wxm
 * @date 2025-04-25
 */
@Tag(name = "家长购买意见征集管理")
@RestController
@RequestMapping("/jzbuy/opinions")
public class XfptJzBuyOpinionsController extends BaseController {
    @Autowired
    private IXfptJzBuyOpinionsService xfptJzBuyOpinionsService;
    @Autowired
    private IXfptJzBuyOpinionsSonService jzBuyOpinionsSonService;
    @Autowired
    private IXfptJzbuyOpinionsConfigService jzbuyOpinionsConfigService;
    @Autowired
    private IXfptBuyGradeClassService buyGradeClassService;
    @Autowired
    private ITeamMeetingService teamMeetingService;
    @Autowired
    private IXfptQuestionnaireConfigService questionnaireConfigService;
    @Autowired
    private IXfptQuestionnaireSurveyService questionnaireSurveyService;
    @Autowired
    private ITeamMeetingSonService teamMeetingSonService;
    @Autowired
    private IXfptQuestionnaireResultService iXfptQuestionnaireResultService;


    /**
     * 获取班子会议填写的选购需求
     *
     * @param teamMeetingId
     * @return
     */
    @GetMapping("/getTeamXGCQ/{teamMeetingId}")
    public AjaxResult getTeamXGCQ(@PathVariable("teamMeetingId") Long teamMeetingId) {
        //获取班子会议填写的选购需求，进行回显
        List<TeamMeetingSon> teamMeetingSons = teamMeetingSonService.selectTeamMeetingSonList(TeamMeetingSon.builder().teamMeetingId(teamMeetingId).build());
        List<XfptJzBuyOpinionsSon> finaDataList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(teamMeetingSons)){
            for (TeamMeetingSon teamMeetingSon : teamMeetingSons) {
                finaDataList.add(XfptJzBuyOpinionsSon.builder()
                                .cgnj(teamMeetingSon.getCgnj())
                                .xfpl(teamMeetingSon.getXfpl())
                                .xsngmts(teamMeetingSon.getGmts())
                                .jgqj(teamMeetingSon.getJgqj())
                                .bz(teamMeetingSon.getBz())
                                .cgnjId(teamMeetingSon.getCgnjId())
                        .build());
            }
        }
        return success(finaDataList);
    }

    /**
     * 查询家长购买意见征集列表
     */
    @Operation(summary = "查询家长购买意见征集列表")
    //@RequiresPermissions("jzbuy:opinions:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptJzBuyOpinions xfptJzBuyOpinions) {
        startPage();
        xfptJzBuyOpinions.setDeptId(null);
        List<XfptJzBuyOpinions> list = xfptJzBuyOpinionsService.selectXfptJzBuyOpinionsList(xfptJzBuyOpinions);
        list.stream().forEach(e -> {
            e.setSonDataList(new ArrayList<>());
            e.setGradeClassList(new ArrayList<>());
        });
        List<String> statusList = Stream.of(ProceConstants.YJZJ_PASS, ProceConstants.YJZJ_NO_PASS).collect(Collectors.toList());
        List<XfptJzBuyOpinions> no_start_list = list.stream().filter(e -> StringUtils.isNotBlank(e.getYjzjqk())
                && !statusList.contains(e.getYjzjqk())).collect(Collectors.toList());
        List<XfptJzBuyOpinions> needUpdateList = new ArrayList<>();
        Map<Long,String> primaryId_yjzjqk_map = new HashMap<>();
        if (!CollectionUtils.isEmpty(no_start_list)) {
            for (XfptJzBuyOpinions jzBuyOpinionsCurr : no_start_list) {
                String tpqzsj = jzBuyOpinionsCurr.getTpqzsj();
                if (StringUtils.isNotBlank(tpqzsj)) {
                    String[] tpqzsjArr = tpqzsj.split("#");
                    boolean starting_flg = DateUtils.dateBetween(tpqzsjArr[0], tpqzsjArr[1], TimeUnit.SECONDS);
                    XfptJzBuyOpinions updateBuild = XfptJzBuyOpinions.builder().id(jzBuyOpinionsCurr.getId()).build();
                    if (starting_flg) {
                        //说明当前系统时间在活动进行中
                        updateBuild.setYjzjqk(ProceConstants.YJZJ_STARTING);
                        needUpdateList.add(updateBuild);
                        primaryId_yjzjqk_map.put(jzBuyOpinionsCurr.getId(), ProceConstants.YJZJ_STARTING);
                    } else if (DateUtils.dateBefore(tpqzsjArr[0], TimeUnit.SECONDS)) {
                        //如果当前日期在活动开始之前，则为初始状态
                        String yjzjqk_curr = jzBuyOpinionsCurr.getYjzjqk();
                        if (!yjzjqk_curr.equals(ProceConstants.YJZJ_NO_STARTED)) {
                            //更新为初始状态
                            updateBuild.setYjzjqk(ProceConstants.YJZJ_NO_STARTED);
                            needUpdateList.add(updateBuild);
                            primaryId_yjzjqk_map.put(jzBuyOpinionsCurr.getId(), ProceConstants.YJZJ_NO_STARTED);
                        }
                    } else if (DateUtils.dateAfter(tpqzsjArr[1], TimeUnit.SECONDS)) {
                        //这里判断如果活动结束，默认设置为活动未通过
                        updateBuild.setYjzjqk(ProceConstants.YJZJ_PASS);
                        needUpdateList.add(updateBuild);
                        primaryId_yjzjqk_map.put(jzBuyOpinionsCurr.getId(), ProceConstants.YJZJ_NO_PASS);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(needUpdateList)) {
            xfptJzBuyOpinionsService.saveOrUpdateBatch(needUpdateList);
            for (XfptJzBuyOpinions jzBuyOpinionsCurr : list) {
                String value = primaryId_yjzjqk_map.getOrDefault(jzBuyOpinionsCurr.getId(), "nil");
                if (!value.equals("nil")) {
                    jzBuyOpinionsCurr.setYjzjqk(value);
                }
            }
        }
        if(CollUtil.isNotEmpty(list)){
            for (XfptJzBuyOpinions jzBuyOpinions : list) {
                //查询投票人数
                List<XfptQuestionnaireResult> list1 = iXfptQuestionnaireResultService.lambdaQuery().eq(XfptQuestionnaireResult::getQuestionnaireSurveyId
                        , jzBuyOpinions.getQuestionBusinessId()).list();
                if(CollUtil.isNotEmpty(list1)){
                    //去重
                    List<XfptQuestionnaireResult> collect = list1.stream().filter(CommUtil.distinctPredicate(XfptQuestionnaireResult::getName)).collect(Collectors.toList());
                    jzBuyOpinions.setVotedCount(collect.size());
                    //计算投票率，四舍五入
                    if(jzBuyOpinions.getCount()==null){
                        jzBuyOpinions.setCount(0);
                    }
                    if(jzBuyOpinions.getVotedCount()==null){
                        jzBuyOpinions.setVotedCount(0);
                    }
                    jzBuyOpinions.setVotingRate(new BigDecimal(jzBuyOpinions.getVotedCount()).divide(new BigDecimal(jzBuyOpinions.getCount()),
                            2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue());

                    if(jzBuyOpinions.getVotingRate()>80){
                        jzBuyOpinions.setYjzjqk(ProceConstants.YJZJ_PASS);
                    }
                }else {
                    jzBuyOpinions.setVotingRate(0);
                    jzBuyOpinions.setVotedCount(0);
                }

            }

        }

        return getDataTable(list);
    }

    /**
     * 导出家长购买意见征集列表
     */
    @Operation(summary = "导出家长购买意见征集列表")
    //@RequiresPermissions("jzbuy:opinions:export")
    @Log(title = "家长购买意见征集", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptJzBuyOpinions xfptJzBuyOpinions) {
        List<XfptJzBuyOpinions> list = xfptJzBuyOpinionsService.selectXfptJzBuyOpinionsList(xfptJzBuyOpinions);
        ExcelUtil<XfptJzBuyOpinions> util = new ExcelUtil<XfptJzBuyOpinions>(XfptJzBuyOpinions.class);
        util.exportExcel(response, list, "家长购买意见征集数据");
    }

    /**
     * 获取家长购买意见征集详细信息
     */
    @Operation(summary = "获取家长购买意见征集详细信息")
    //@RequiresPermissions("jzbuy:opinions:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        if (id == null) {
            return error("获取标识失败！");
        }
        XfptJzBuyOpinions baseData = xfptJzBuyOpinionsService.getById(id);
        if (baseData == null) {
            return error("获取数据失败！");
        }
        //意见征集情况
        String yjzjqk = baseData.getYjzjqk();
        if (StringUtils.isNotBlank(yjzjqk) && yjzjqk.equals(ProceConstants.YJZJ_NO_STARTED)) {
            String tpqzsj = baseData.getTpqzsj();
            if (StringUtils.isNotBlank(tpqzsj)) {
                String[] yjzjqkArr = tpqzsj.split("#");
                boolean activedFlg = DateUtils.dateBetween(yjzjqkArr[0], yjzjqkArr[1], TimeUnit.SECONDS);
                if (activedFlg) {
                    //更新状态为投票中
                    XfptJzBuyOpinions build = XfptJzBuyOpinions.builder().id(id).yjzjqk(ProceConstants.YJZJ_STARTING).build();
                    xfptJzBuyOpinionsService.updateById(build);
                }
            }
        }
        List<XfptJzBuyOpinionsSon> sonDataList = new ArrayList<>();
        List<XfptBuyGradeClass> gradeClassList = new ArrayList<>();
        //查询子表数据
        List<XfptJzBuyOpinionsSon> xfptJzBuyOpinionsSonDataList = jzBuyOpinionsSonService.selectXfptJzBuyOpinionsSonList(XfptJzBuyOpinionsSon.builder().pId(id).build());
        if (!CollectionUtils.isEmpty(xfptJzBuyOpinionsSonDataList)) {
            sonDataList = xfptJzBuyOpinionsSonDataList;
        }
        //查询年级购买子表数据
        List<XfptBuyGradeClass> buyGradeClasses = buyGradeClassService.selectXfptBuyGradeClassList(XfptBuyGradeClass.builder().category(ProceConstants.JZ_BUY_NJ_CATEGORY).pid(id).build());
        if (!CollectionUtils.isEmpty(buyGradeClasses)) {
            gradeClassList = buyGradeClasses;
        }
        baseData.setGradeClassList(gradeClassList);
        baseData.setSonDataList(sonDataList);
        //查询问卷
        List<XfptQuestionnaireSurvey> list = questionnaireSurveyService.lambdaQuery()
                .eq(XfptQuestionnaireSurvey::getQuestionConfigId, baseData.getQuestionBusinessId()).list();
        baseData.setSonList(list);
        return success(baseData);
    }

    /**
     * 新增家长购买意见征集
     */
    @Operation(summary = "新增家长购买意见征集")
    //@RequiresPermissions("jzbuy:opinions:add")
    @Log(title = "家长购买意见征集", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody XfptJzBuyOpinions xfptJzBuyOpinions) {
        try {
            Long teamMeetingId = xfptJzBuyOpinions.getTeamMeetingId();
            if (teamMeetingId == null) {
                return error("班子会议id不能为空！");
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
            xfptJzBuyOpinions.setDeptId(deptId);
            xfptJzBuyOpinions.setAncestors(ancestors);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //如果是线上类型，则需要问卷模板
            String zjlx = xfptJzBuyOpinions.getZjlx();
            if (StringUtils.isBlank(zjlx)) {
                return error("意见征集类型不能为空！");
            }
            if (zjlx.equals("0")) {
                String tpqzsj = xfptJzBuyOpinions.getTpqzsj();
                if (StringUtils.isBlank(tpqzsj)) {
                    return error("投票起止时间不能为空！");
                }
                String[] tpqzsjArr = tpqzsj.split("#");
                boolean activing = DateUtils.dateBetween(tpqzsjArr[0], tpqzsjArr[1], TimeUnit.SECONDS);
                xfptJzBuyOpinions.setYjzjqk(ProceConstants.YJZJ_NO_STARTED);
                if (activing) {
                    //设置活动状态
                    xfptJzBuyOpinions.setYjzjqk(ProceConstants.YJZJ_STARTING);
                }
                //处理线上问卷相关数据
//                //获取模板id
//                Long questionConfigId = xfptJzBuyOpinions.getQuestionConfigId();
//                if (questionConfigId == null) {
//                    return error("问卷不能为空！");
//                }
//                //查询问卷模板配置项
//                List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyDataList = questionnaireSurveyService.selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey
//                        .builder().questionConfigId(questionConfigId).build());
                List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyDataList = xfptJzBuyOpinions.getSonList();
                if (CollectionUtils.isEmpty(xfptQuestionnaireSurveyDataList)) {
                    return error("没有获取到问卷的配置项，请联系管理员！");
                }
                //自动生成问卷调查主数据: 默认发布状态
                XfptQuestionnaireConfig questionBuild = XfptQuestionnaireConfig
                        .builder().fbStatus(1L).title(xfptJzBuyOpinions.getTitle()).category("2")
                        .type(ProceConstants.GMYJ_QUESTION_TYPE)
                        .deptId(deptId)
                        .lcCategory("1")
                        .ancestors(ancestors)
                        //由于tpqzsjArr 这里的时间是精确到的分钟，需要拼接上秒
                        .beginTime(simpleDateFormat.parse(tpqzsjArr[0]))
                        .endTime(simpleDateFormat.parse(tpqzsjArr[1]))
                        .votingNotice(xfptJzBuyOpinions.getConfigContent())
                        .names(xfptJzBuyOpinions.getNames())
                        .build();
                questionnaireConfigService.save(questionBuild);
                xfptQuestionnaireSurveyDataList.stream().forEach(e -> {
                    e.setQuestionConfigId(questionBuild.getId());
                    e.setId(null);
                });
                questionnaireSurveyService.saveBatch(xfptQuestionnaireSurveyDataList);
                //设置班子会议根据问卷模板创建的问卷数据
                xfptJzBuyOpinions.setQuestionBusinessId(questionBuild.getId());
            }

            boolean save = xfptJzBuyOpinionsService.save(xfptJzBuyOpinions);
            List<XfptJzBuyOpinionsSon> sonDataList = xfptJzBuyOpinions.getSonDataList();
            List<XfptBuyGradeClass> gradeClassList = xfptJzBuyOpinions.getGradeClassList();
            if (save) {
                //子表 家长购买意见征集活动
                if (!CollectionUtils.isEmpty(sonDataList)) {
                    sonDataList.stream().forEach(e -> e.setPId(xfptJzBuyOpinions.getId()));
                    jzBuyOpinionsSonService.saveBatch(sonDataList);
                }
                //子表采购年级
                if (!CollectionUtils.isEmpty(gradeClassList)) {
                    gradeClassList.stream().forEach(e -> {
                        e.setPid(xfptJzBuyOpinions.getId());
                        e.setCategory(ProceConstants.JZ_BUY_NJ_CATEGORY);
                    });
                    buyGradeClassService.saveBatch(gradeClassList);
                }
                //增加采购模版
                XfptJzbuyOpinionsConfig xfptJzbuyOpinionsConfig = new  XfptJzbuyOpinionsConfig();
                xfptJzbuyOpinionsConfig.setContent(xfptJzBuyOpinions.getConfigContent());
                xfptJzbuyOpinionsConfig.setType("1");
                xfptJzbuyOpinionsConfig.setDeptId(deptId);
                jzbuyOpinionsConfigService.save(xfptJzbuyOpinionsConfig);
            }
            List<XfptJzBuyOpinions> xfptJzBuyOpinionsExistList = xfptJzBuyOpinionsService.selectXfptJzBuyOpinionsList(XfptJzBuyOpinions.builder().teamMeetingId(teamMeetingId).build());
            if (CollectionUtils.isEmpty(xfptJzBuyOpinionsExistList) || xfptJzBuyOpinionsExistList.size() == 1) {
                //记录班子会议时间轴
                String timeTb = ProceConstants.GMYJ + "#" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                teamMeetingService.updateTeamMeetingCurrOrder(teamMeetingId, timeTb);
            }
            return toAjax(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改家长购买意见征集
     */
    @Operation(summary = "修改家长购买意见征集")
    //@RequiresPermissions("jzbuy:opinions:edit")
    @Log(title = "家长购买意见征集修改", businessType = BusinessType.UPDATE)
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody XfptJzBuyOpinions xfptJzBuyOpinions) {
        Long id = xfptJzBuyOpinions.getId();
        if (id == null) {
            return error("获取标识失败！");
        }
        try {
            xfptJzBuyOpinions.setUpdateBy("默认");
            if (StringUtils.isNotBlank(SecurityUtils.getUsername())) {
                xfptJzBuyOpinions.setUpdateBy(SecurityUtils.getUsername());
            }
            //存在问卷调查模板时
            String zjlx = xfptJzBuyOpinions.getZjlx();
            if (StringUtils.isBlank(zjlx)) {
                return error("意见征集类型不能为空！");
            }
//            if (xfptJzBuyOpinions.getQuestionConfigId() != null && zjlx.equals("0")) {
//                String tpqzsj = xfptJzBuyOpinions.getTpqzsj();
//                if (StringUtils.isBlank(tpqzsj)) {
//                    return error("投票起止时间不能为空！");
//                }
//                XfptJzBuyOpinions baseData = xfptJzBuyOpinionsService.getById(id);
//                String tpqzsjBase = baseData.getTpqzsj();
//                //时间发生了修改
//                boolean tpqzsjNotEqFlg = !tpqzsj.equals(tpqzsjBase);
//                String[] tpqzsjArr = tpqzsj.split("#");
//                boolean activing = DateUtils.dateBetween(tpqzsjArr[0], tpqzsjArr[1], TimeUnit.SECONDS);
//                if (activing && baseData.getYjzjqk().equals(ProceConstants.YJZJ_NO_STARTED)) {
//                    //设置活动状态
//                    xfptJzBuyOpinions.setYjzjqk(ProceConstants.YJZJ_STARTING);
//                }
//
//                //限制如果是线上投票征集,查询修改后的问卷调查模板数据
//                XfptQuestionnaireConfig questionConfigTmp = questionnaireConfigService.getById(xfptJzBuyOpinions.getQuestionConfigId());
//                if (questionConfigTmp == null) {
//                    throw new Exception("没有获取到模板数据！");
//                }
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                if (baseData.getQuestionConfigId() != null
//                        && !baseData.getQuestionConfigId().equals(xfptJzBuyOpinions.getQuestionConfigId())) {
//                    //如果用户选错了或者选择新的问卷模板,则把原来生成的问卷调查数据删除
//                    questionnaireConfigService.removeById(baseData.getQuestionConfigId());
//                    //删除问卷调查的子表配置项
//                    LambdaQueryWrapper<XfptQuestionnaireSurvey> questionnaireSurveyQueryWrapper = new LambdaQueryWrapper<>();
//                    questionnaireSurveyQueryWrapper.eq(XfptQuestionnaireSurvey::getQuestionConfigId, baseData.getQuestionConfigId());
//                    questionnaireSurveyService.remove(questionnaireSurveyQueryWrapper);
//                    //查询问卷模板配置项
//                    List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyDataList = questionnaireSurveyService.selectXfptQuestionnaireSurveyList(XfptQuestionnaireSurvey
//                            .builder().questionConfigId(xfptJzBuyOpinions.getQuestionConfigId()).build());
//                    if (CollectionUtils.isEmpty(xfptQuestionnaireSurveyDataList)) {
//                        return error("没有获取到问卷模板的配置项，请联系管理员！");
//                    }
//                    //创建新的问卷调查数据,自动生成问卷调查主数据: 默认发布状态
//                    XfptQuestionnaireConfig questionBuild = XfptQuestionnaireConfig
//                            .builder().fbStatus(1L).title(xfptJzBuyOpinions.getTitle()).category("2")
//                            .type(ProceConstants.GMYJ_QUESTION_TYPE)
//                            .deptId(xfptJzBuyOpinions.getDeptId())
//                            .ancestors(xfptJzBuyOpinions.getAncestors())
//                            .beginTime(simpleDateFormat.parse(tpqzsjArr[0]))
//                            .endTime(simpleDateFormat.parse(tpqzsjArr[1]))
//                            .names(xfptJzBuyOpinions.getNames())
//                            .build();
//                    questionnaireConfigService.save(questionBuild);
//                    //查询问卷调查新模板子数据
//                    xfptQuestionnaireSurveyDataList.stream().forEach(e -> {
//                        e.setId(null);
//                        e.setQuestionConfigId(questionBuild.getId());
//                    });
//                    questionnaireSurveyService.saveBatch(xfptQuestionnaireSurveyDataList);
//                    xfptJzBuyOpinions.setQuestionBusinessId(questionBuild.getId());
//                } else if (tpqzsjNotEqFlg) {
//                    //仅更新时间
//                    LambdaUpdateWrapper<XfptQuestionnaireConfig> questionConfigUpdateWrapper = new LambdaUpdateWrapper<>();
//                    questionConfigUpdateWrapper.eq(XfptQuestionnaireConfig::getId, baseData.getQuestionBusinessId());
//                    questionConfigUpdateWrapper.set(XfptQuestionnaireConfig::getBeginTime, simpleDateFormat.parse(tpqzsjArr[0]));
//                    questionConfigUpdateWrapper.set(XfptQuestionnaireConfig::getEndTime, simpleDateFormat.parse(tpqzsjArr[1]));
//                    questionnaireConfigService.update(null, questionConfigUpdateWrapper);
//                }
//            }

            if (zjlx.equals("0")) {
                SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
                Long deptId = dept.getDeptId();
                String ancestors = dept.getAncestors();
                String tpqzsj = xfptJzBuyOpinions.getTpqzsj();
                String[] tpqzsjArr = tpqzsj.split("#");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //删除问卷调查
                // boolean b = questionnaireConfigService.removeById(xfptJzBuyOpinions.getQuestionBusinessId());
                List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveyDataList = xfptJzBuyOpinions.getSonList();
                if (CollectionUtils.isEmpty(xfptQuestionnaireSurveyDataList)) {
                    return error("没有获取到问卷的配置项，请联系管理员！");
                }

                //自动生成问卷调查主数据: 默认发布状态
                XfptQuestionnaireConfig questionBuild = XfptQuestionnaireConfig
                        .builder().fbStatus(1L).title(xfptJzBuyOpinions.getTitle()).category("2")
                        .type(ProceConstants.GMYJ_QUESTION_TYPE)
                        .deptId(deptId)
                        .lcCategory("1")
                        .ancestors(ancestors)
                        //由于tpqzsjArr 这里的时间是精确到的分钟，需要拼接上秒
                        .beginTime(simpleDateFormat.parse(tpqzsjArr[0]))
                        .endTime(simpleDateFormat.parse(tpqzsjArr[1]))
                        .votingNotice(xfptJzBuyOpinions.getConfigContent())
                        .names(xfptJzBuyOpinions.getNames())
                        .id(xfptJzBuyOpinions.getQuestionBusinessId())
                        .build();
                questionnaireConfigService.saveOrUpdate(questionBuild);
                xfptQuestionnaireSurveyDataList.stream().forEach(e -> {
                    e.setQuestionConfigId(xfptJzBuyOpinions.getQuestionBusinessId());
                });
                LambdaQueryWrapper<XfptQuestionnaireSurvey> lambdaQueryWrapper=new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(XfptQuestionnaireSurvey::getQuestionConfigId,xfptJzBuyOpinions.getQuestionBusinessId());
                boolean remove = questionnaireSurveyService.remove(lambdaQueryWrapper);
                if(remove){
                    for (XfptQuestionnaireSurvey xfptQuestionnaireSurvey : xfptQuestionnaireSurveyDataList) {
                        xfptQuestionnaireSurvey.setId(null);
                    }
                    questionnaireSurveyService.saveOrUpdateBatch(xfptQuestionnaireSurveyDataList);
                }else {
                    throw new ServiceException("系统错误");
                }

                //设置班子会议根据问卷模板创建的问卷数据
                //xfptJzBuyOpinions.setQuestionBusinessId(questionBuild.getId());
                boolean saveFlg = xfptJzBuyOpinionsService.updateById(xfptJzBuyOpinions);
                List<XfptJzBuyOpinionsSon> sonDataList = xfptJzBuyOpinions.getSonDataList();
                List<XfptBuyGradeClass> gradeClassList = xfptJzBuyOpinions.getGradeClassList();
                if (saveFlg) {
                    if (!CollectionUtils.isEmpty(sonDataList)) {
                        sonDataList.stream().forEach(e -> e.setPId(id));
                        jzBuyOpinionsSonService.saveOrUpdateBatch(sonDataList);
                    }
                    //采购年级
                    if (!CollectionUtils.isEmpty(gradeClassList)) {
                        //查询数据库中数据已经存在的
                        buyGradeClassService.physicDeleteDataWithGradeClassIdList(null, id, ProceConstants.JZ_BUY_NJ_CATEGORY);
                        gradeClassList.stream().forEach(e -> {
                            e.setPid(id);
                            e.setId(null);
                            e.setCategory(ProceConstants.JZ_BUY_NJ_CATEGORY);
                        });
                        buyGradeClassService.saveBatch(gradeClassList);
                    }
                    //增加采购模版
                    XfptJzbuyOpinionsConfig xfptJzbuyOpinionsConfig = new  XfptJzbuyOpinionsConfig();
                    xfptJzbuyOpinionsConfig.setContent(xfptJzBuyOpinions.getConfigContent());
                    xfptJzbuyOpinionsConfig.setType("1");
                    xfptJzbuyOpinionsConfig.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
                    jzbuyOpinionsConfigService.save(xfptJzbuyOpinionsConfig);
                }
            }else {
                boolean saveFlg = xfptJzBuyOpinionsService.updateById(xfptJzBuyOpinions);
            }

            return toAjax(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除家长购买意见征集
     */
    @Operation(summary = "删除家长购买意见征集")
    //@RequiresPermissions("jzbuy:opinions:remove")
    @Log(title = "家长购买意见征集删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        try {
            if (ids.length < 0) {
                return error("删除数据不能为空！");
            }
            List<Long> idList = Arrays.asList(ids);
            LambdaQueryWrapper<XfptJzBuyOpinions> entityQueryWrapper = new LambdaQueryWrapper<>();
            entityQueryWrapper.in(XfptJzBuyOpinions::getId, idList);
            List<XfptJzBuyOpinions> baseDataList = xfptJzBuyOpinionsService.list(entityQueryWrapper);
            List<Long> requestConfigIdList = baseDataList.stream()
                    .map(XfptJzBuyOpinions::getQuestionBusinessId).filter(Objects::nonNull).collect(Collectors.toList());
            boolean delFlg = xfptJzBuyOpinionsService.removeByIds(Arrays.asList(ids));
//            if (delFlg) {
//                List<Long> pidList = Arrays.stream(ids).collect(Collectors.toList());
//                //删除子表数据
//                LambdaUpdateWrapper<XfptJzBuyOpinionsSon> JzBuyOpinionsSonWrapper = new LambdaUpdateWrapper<>();
//                JzBuyOpinionsSonWrapper.in(XfptJzBuyOpinionsSon::getPId, pidList);
//                jzBuyOpinionsSonService.remove(JzBuyOpinionsSonWrapper);
//                //删除购买年级子表
//                LambdaUpdateWrapper<XfptBuyGradeClass> BuyGradeClassWrapper = new LambdaUpdateWrapper<>();
//                BuyGradeClassWrapper.eq(XfptBuyGradeClass::getCategory, ProceConstants.JZ_BUY_NJ_CATEGORY);
//                BuyGradeClassWrapper.in(XfptBuyGradeClass::getPid, pidList);
//                buyGradeClassService.remove(BuyGradeClassWrapper);
//                if (!CollectionUtils.isEmpty(requestConfigIdList)) {
//                    //删除问卷关联项的数据
//                    LambdaQueryWrapper<XfptQuestionnaireSurvey> questionnaireSurveyQueryWrapper = new LambdaQueryWrapper<>();
//                    questionnaireSurveyQueryWrapper.in(XfptQuestionnaireSurvey::getQuestionConfigId, requestConfigIdList);
//                    questionnaireSurveyService.remove(questionnaireSurveyQueryWrapper);
//                    //删除流程问卷
//                    LambdaQueryWrapper<XfptQuestionnaireConfig> questionnaireConfigQueryWrapper = new LambdaQueryWrapper<>();
//                    questionnaireConfigQueryWrapper.in(XfptQuestionnaireConfig::getId, requestConfigIdList);
//                    questionnaireConfigService.remove(questionnaireConfigQueryWrapper);
//                }
//            }
            return toAjax(1);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @Operation(summary = "删除家长购买意见征集从表")
    //@RequiresPermissions("jzbuy:opinions:remove")
    @Log(title = "家长购买意见征集删除从表", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteSonData/{id}")
    public AjaxResult deleteSonData(@PathVariable("id") Long id) {
        try {
            jzBuyOpinionsSonService.removeById(id);
            return toAjax(1);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @Operation(summary = "删除家长购买意见征集年级购买从表")
    //@RequiresPermissions("jzbuy:opinions:remove")
    @Log(title = "家长购买意见征集删除年级购买从表", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteNJGMData/{ids}")
    public AjaxResult deleteNJGMData(@PathVariable Long[] ids) {
        try {
            LambdaUpdateWrapper<XfptBuyGradeClass> BuyGradeClassWrapper = new LambdaUpdateWrapper<>();
            BuyGradeClassWrapper.eq(XfptBuyGradeClass::getCategory, ProceConstants.JZ_BUY_NJ_CATEGORY);
            BuyGradeClassWrapper.in(XfptBuyGradeClass::getId, Arrays.asList(ids));
            buyGradeClassService.remove(BuyGradeClassWrapper);
            return toAjax(1);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }


    //-------------------------------------------------------------配置模板数据存储 /config--------------------------

    /**
     * 查询家长购买意见征集模板配置列表
     */
    //@Operation(summary = "查询家长购买意见征集模板配置列表")
    @GetMapping(value = {"/config/list","/config/list/{noPage}"})
    public TableDataInfo configList(XfptJzbuyOpinionsConfig jzbuyOpinionsConfig,@PathVariable(value = "noPage",required = false) Integer noPage) {
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (!admin) {
            Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
            jzbuyOpinionsConfig.setDeptId(deptId);
        }
        if (noPage == null) {
            startPage();
        }
        List<XfptJzbuyOpinionsConfig> list = jzbuyOpinionsConfigService.selectXfptJzbuyOpinionsConfigList(jzbuyOpinionsConfig);
        return getDataTable(list);
    }

    /**
     * 新增家长购买意见征集模板配置
     */
    @Operation(summary = "家长购买意见征集模板配置")
    @Log(title = "家长购买意见征集模板配置", businessType = BusinessType.INSERT)
    @PostMapping("/config")
    public AjaxResult configAdd(@RequestBody XfptJzbuyOpinionsConfig jzbuyOpinionsConfig) {
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (admin) {
            return error("管理员禁止操作！");
        }
        try {
            SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
            if (dept == null || dept.getDeptId() == null) {
                return error("获取部门信息失败！");
            }
            if (StringUtils.isBlank(jzbuyOpinionsConfig.getCategory())) {
                return error("模板标题不能为空！");
            }
            //查询模板名称是否存在重复的
            LambdaQueryWrapper<XfptJzbuyOpinionsConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(XfptJzbuyOpinionsConfig::getCategory, jzbuyOpinionsConfig.getCategory());
            queryWrapper.eq(XfptJzbuyOpinionsConfig::getDeptId, dept.getDeptId());
            List<XfptJzbuyOpinionsConfig> baseList = jzbuyOpinionsConfigService.list(queryWrapper);
            if (!CollectionUtils.isEmpty(baseList)) {
                return error("模板标题不能重复！");
            }
            jzbuyOpinionsConfig.setDeptId(dept.getDeptId());
            jzbuyOpinionsConfig.setAncestors(dept.getAncestors());
            jzbuyOpinionsConfig.setType("0");
            jzbuyOpinionsConfigService.save(jzbuyOpinionsConfig);
            return toAjax(1);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 修改家长购买意见征集模板配置
     */
    @Operation(summary = "修改家长购买意见征集模板配置")
    @Log(title = "家长购买意见征集模板配置修改", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult configEdit(@RequestBody XfptJzbuyOpinionsConfig jzbuyOpinionsConfig) {
        Long id = jzbuyOpinionsConfig.getId();
        if (id == null) {
            return error("获取标识失败！");
        }
        try {
            jzbuyOpinionsConfig.setUpdateBy("默认");
            if (StringUtils.isNotBlank(SecurityUtils.getUsername())) {
                jzbuyOpinionsConfig.setUpdateBy(SecurityUtils.getUsername());
            }
            jzbuyOpinionsConfigService.updateById(jzbuyOpinionsConfig);
            return toAjax(1);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 获取家长购买意见征集模板配置详细信息
     */
    @Operation(summary = "获取家长购买意见征集模板配置详细信息")
    @GetMapping(value = "/config/{id}")
    public AjaxResult configGetInfo(@PathVariable("id") Long id) {
        XfptJzbuyOpinionsConfig baseData = jzbuyOpinionsConfigService.getById(id);
        if (baseData == null) {
            return error("获取数据失败！");
        }
        return success(baseData);
    }


    /**
     * 删除家长购买意见征集模板配置
     */
    @Operation(summary = "删除家长购买意见征集模板配置")
    @Log(title = "家长购买意见征集模板配置删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/config/{ids}")
    public AjaxResult configRemove(@PathVariable Long[] ids) {
        try {
            jzbuyOpinionsConfigService.removeByIds(Arrays.asList(ids));
            return toAjax(1);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    /**
     * 查询最后一次的模版
     * @return
     */
    @GetMapping("/lastConfig")
    public AjaxResult lastConfig(){
        XfptJzbuyOpinionsConfig xfptJzbuyOpinionsConfig = jzbuyOpinionsConfigService.lambdaQuery().eq(XfptJzbuyOpinionsConfig::getType, "1")
                .orderByDesc(XfptJzbuyOpinionsConfig::getCreateTime)
                .last("limit 1").one();
        //如果查不出来则查询教育局配置的模版
        if(xfptJzbuyOpinionsConfig==null){
            xfptJzbuyOpinionsConfig= jzbuyOpinionsConfigService.lambdaQuery().eq(XfptJzbuyOpinionsConfig::getType, "0")
                    .orderByDesc(XfptJzbuyOpinionsConfig::getCreateTime)
                    .last("limit 1").one();
        }
        return AjaxResult.success(xfptJzbuyOpinionsConfig);
    }

}
