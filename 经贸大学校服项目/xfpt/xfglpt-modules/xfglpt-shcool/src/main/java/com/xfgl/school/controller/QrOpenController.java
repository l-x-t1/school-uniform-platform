package com.xfgl.school.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.CommUtil;
import com.xfgl.common.core.utils.DateUtils;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.domain.vo.LableValue;
import com.xfgl.school.domain.vo.XfptPdxzSureSupplierVo;
import com.xfgl.school.domain.vo.XfptReportModeVo;
import com.xfgl.school.mp.MpUtil;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/qr/open")
@RestController
@CrossOrigin
public class QrOpenController extends BaseController {
    @Autowired
    private IXfptPurchaseProcessService xfptPurchaseProcessService;

    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private ITeamMeetingSonService teamMeetingSonServiceImpl;
    @Autowired
    private IXfptJzBuyOpinionsService xfptJzBuyOpinionsService;
    @Autowired
    private IXfptJzBuyOpinionsSonService jzBuyOpinionsSonService;
    @Autowired
    private IXfptQuestionnaireResultService iXfptQuestionnaireResultService;
    @Autowired
    private IXfptBuyGradeClassService buyGradeClassService;
    @Autowired
    private IXfptQuestionnaireSurveyService questionnaireSurveyService;
    @Autowired
    private IXfptQuestionnaireSurveyService xfptQuestionnaireSurveyService;
    @Autowired
    private IXfptQuestionnaireSurveyService iXfptQuestionnaireSurveyService;
    @Autowired
    private IXfptQuestionnaireSignatureService iXfptQuestionnaireSignatureService;

    @Autowired
    private IXfptQuestionnaireResultService xfptQuestionnaireResultService;
    @Autowired
    private IXfptOrgReportDiscloseService xfptOrgReportDiscloseService;
    @Autowired
    private IXfptOrgReportDiscloseSonService orgReportDiscloseSonService;


    /**
     * 流程接口
     * @param xfptPurchaseProcess
     * @return
     */
    @GetMapping("/cglclist")
    public TableDataInfo cglclist(XfptPurchaseProcess xfptPurchaseProcess) {
        startPage();
        if (xfptPurchaseProcess.getDeptId() == null) {
            throw new RuntimeException("部门ID不能为空");
        }
        List<XfptPurchaseProcess> list = xfptPurchaseProcessService.selectXfptPurchaseProcessList(xfptPurchaseProcess);
        for (XfptPurchaseProcess purchaseProcess : list) {
            List<TeamMeeting> list1 = iTeamMeetingService.lambdaQuery().eq(TeamMeeting::getTeamId, purchaseProcess.getId()).list();
            if (CollUtil.isNotEmpty(list1)) {
                purchaseProcess.setTeamId(list1.get(0).getId());
            }
        }
        return getDataTable(list);
    }

    /**
     * 班子会议列表
     * @param teamMeeting
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(TeamMeeting teamMeeting) {
        if (teamMeeting.getDeptId() == null) {
            teamMeeting.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        }
        startPage();
        List<TeamMeeting> list = iTeamMeetingService.selectTeamMeetingList(teamMeeting);
        for (TeamMeeting meetingCurr : list) {
            meetingCurr.setTeamMeetingSonList(new ArrayList<>());
        }
        return getDataTable(list);
    }

    @Operation(summary = "获取班子会议详细信息")
    //@RequiresPermissions("team:meeting:query")
    @GetMapping(value = "/bzhy/{id}")
    public AjaxResult getBzhyInfo(@PathVariable("id") Long id) {
        TeamMeeting dataEntity = new TeamMeeting();
//        if (id == null) {
//            return AjaxResult.error("获取标识失败！");
//        }
        List<TeamMeeting> teamMeetings = iTeamMeetingService.selectTeamMeetingList(TeamMeeting.builder().id(id).build());
        if (!CollectionUtils.isEmpty(teamMeetings) && teamMeetings.size() == 1) {
            dataEntity = teamMeetings.get(0);
            List<String> timeAxisList = new ArrayList<>();
            String timeTb = dataEntity.getTimeTb();
            if (StringUtils.isNotBlank(timeTb)) {
                timeAxisList = Arrays.stream(timeTb.split(",")).collect(Collectors.toList());
                dataEntity.setTimeAxisList(timeAxisList);
            }
            //查询子表数据
            List<TeamMeetingSon> sonDataList = teamMeetingSonServiceImpl.selectTeamMeetingSonList(TeamMeetingSon.builder().teamMeetingId(id).build());
            dataEntity.setTeamMeetingSonList(sonDataList);
            return success(dataEntity);
        }
        return AjaxResult.success(dataEntity);
    }

    @Operation(summary = "查询家长购买意见征集列表")
    //@RequiresPermissions("jzbuy:opinions:list")
    @GetMapping("/gmyjzjlist")
    public TableDataInfo gmyjzjlist(XfptJzBuyOpinions xfptJzBuyOpinions) {
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

    @Operation(summary = "获取家长购买意见征集详细信息")
    //@RequiresPermissions("jzbuy:opinions:query")
    @GetMapping(value = "/gmyjzj/{id}")
    public AjaxResult getGmyjzjInfo(@PathVariable("id") Long id) {
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
     * 查询表头信息
     *
     * @param questionId 问卷iD
     * @return
     */
    @GetMapping("/headingList")
    public AjaxResult headingList(Long questionId) {
        List<XfptQuestionnaireSurvey> list = xfptQuestionnaireSurveyService.lambdaQuery().eq(XfptQuestionnaireSurvey::getQuestionConfigId, questionId).list();
        if(CollUtil.isEmpty(list)){
            return AjaxResult.error("没有数据");
        }
        List<LableValue> lableValues=new ArrayList<>();
        List<String> result=new ArrayList<>();
        LableValue lableValue=new LableValue();
        lableValue.setLabel("姓名");
        lableValue.setValue("name");
        lableValues.add(lableValue);
        LableValue lableValue2=new LableValue();
        lableValue2.setLabel("IP");
        lableValue2.setValue("ip");
        lableValues.add(lableValue2);
        List<String> collect = list.stream().map(item -> item.getSurveyContent()).collect(Collectors.toList());
        Map<Long, List<XfptQuestionnaireSurvey>> collect1 = list.stream().collect(Collectors.groupingBy(XfptQuestionnaireSurvey::getId));
        //int flag=1;
        //for (String s : collect) {
        //    LableValue lableValue3=new LableValue();
        //    lableValue3.setLabel(s);
        //    lableValue3.setValue("value"+flag);
        //    lableValues.add(lableValue3);
        //    flag++;
        //}
        for (Long l : collect1.keySet()) {
            LableValue lableValue3=new LableValue();
            lableValue3.setLabel(collect1.get(l).get(0).getSurveyContent());
            lableValue3.setValue("value-"+l);
            lableValues.add(lableValue3);
        }

        LableValue lableValue4=new LableValue();
        lableValue4.setLabel("提交时间");
        lableValue4.setValue("createTime");
        lableValues.add(lableValue4);
        LableValue lableValue5=new LableValue();
        lableValue5.setLabel("签名");
        lableValue5.setValue("signature");
        lableValues.add(lableValue5);

        return AjaxResult.success(lableValues);
    }

    /**
     * 投票名单
     * @param questId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/signatureList")
    public TableDataInfo signatureList(Long questId, Long pageNum, Long pageSize) {
        List<XfptQuestionnaireSurvey> list1 = iXfptQuestionnaireSurveyService.lambdaQuery().eq(XfptQuestionnaireSurvey::getQuestionConfigId, questId).list();
        if (CollUtil.isEmpty(list1)) {
            return getDataTable(new ArrayList<>());
        }
        List<Long> collect = list1.stream().map(XfptQuestionnaireSurvey::getId).collect(Collectors.toList());
        List<HashMap<String, Object>> result = new ArrayList<>();
        List<XfptQuestionnaireResult> list = xfptQuestionnaireResultService.lambdaQuery()
                .eq(XfptQuestionnaireResult::getQuestionnaireSurveyId, questId).orderByDesc(XfptQuestionnaireResult::getCreateTime).list();
        Map<String, List<XfptQuestionnaireResult>> collect1 = list.stream().collect(Collectors.groupingBy(XfptQuestionnaireResult::getImage));

        for (String s : collect1.keySet()) {
            int flag = 1;
            List<XfptQuestionnaireResult> xfptQuestionnaireResults = collect1.get(s)
                    .stream().sorted(Comparator.comparing(XfptQuestionnaireResult::getSurveyId)).collect(Collectors.toList());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", xfptQuestionnaireResults.get(0).getName());
            hashMap.put("ip", xfptQuestionnaireResults.get(0).getIpAddress());

            for (XfptQuestionnaireResult xfptQuestionnaireResult : xfptQuestionnaireResults) {

                if (collect.contains(Long.valueOf(xfptQuestionnaireResult.getSurveyId()))) {
                    hashMap.put("value-" + xfptQuestionnaireResult.getSurveyId(), xfptQuestionnaireResult.getQuestionnaireResult());
                } else {
                    hashMap.put("value-" + xfptQuestionnaireResult.getSurveyId(), "");
                }


                flag++;
            }
            hashMap.put("createTime", DateUtil.format(xfptQuestionnaireResults.get(0).getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            hashMap.put("signature", xfptQuestionnaireResults.get(0).getImage());
            result.add(hashMap);
        }
        result = result.stream().skip((pageNum - 1) * pageSize) // 跳过前面无用的记录
                .limit(pageSize) // 获取指定记录数
                .collect(Collectors.toList());
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(result);
        Long count = iXfptQuestionnaireSignatureService.lambdaQuery().eq(XfptQuestionnaireSignature::getQuestionnaireSurveyId, questId).count();
        rspData.setTotal(count);

        return rspData;
    }

    /**
     * 查询选用组织名单报备与公开列表
     */
    @Operation(summary = "查询选用组织名单报备与公开列表")
    //@RequiresPermissions("org:disclose:list")
    @GetMapping("/xyzz/list")
    public TableDataInfo xyzzList(XfptOrgReportDisclose xfptOrgReportDisclose) {
        startPage();
        xfptOrgReportDisclose.setDeptId(null);
        List<XfptOrgReportDisclose> list = xfptOrgReportDiscloseService.selectXfptOrgReportDiscloseList(xfptOrgReportDisclose);
        for (XfptOrgReportDisclose orgReportDisclose : list) {
            orgReportDisclose.setSonDataList(new ArrayList<>());
        }
        return getDataTable(list);
    }

    /**
     * 获取选用组织名单报备与公开详细信息
     */
    @Operation(summary = "获取选用组织名单报备与公开详细信息")
    //@RequiresPermissions("org:disclose:query")
    @GetMapping(value = "/xyzz/{id}")
    public AjaxResult getXyzzInfo(@PathVariable("id") Long id) {
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

    @Autowired
    private IXfptReportModeService xfptReportModeService;
    @Autowired
    private IXfptReportModeOperService iXfptReportModeOperService;
    /**
     * 查询选用组织确定选购模式会议列表
     */
    @Operation(summary = "查询选用组织确定选购模式会议列表")
    // @RequiresPermissions("shcool:mode:list")
    @GetMapping("/qdxgms/list")
    public TableDataInfo qdxgmsList(XfptReportMode xfptReportMode) {
        startPage();
        xfptReportMode.setDeptId(null);
        List<XfptReportMode> list = xfptReportModeService.selectXfptReportModeList(xfptReportMode);
        return getDataTable(list);
    }
    /**
     * 获取选用组织确定选购模式会议详细信息
     */
    @Operation(summary = "获取选用组织确定选购模式会议详细信息")
    // @RequiresPermissions("shcool:mode:query")
    @GetMapping(value = "/qdxgms/{id}")
    public AjaxResult getQdxgmsInfo(@PathVariable("id") Long id) {

        XfptReportMode xfptReportMode = xfptReportModeService.getById(id);
        if(xfptReportMode!=null){
            XfptReportModeVo xfptReportModeVo=new XfptReportModeVo();
            xfptReportModeVo.setXfptReportMode(xfptReportMode);
            //查询操作记录
            List<XfptReportModeOper> list = iXfptReportModeOperService.lambdaQuery().eq(XfptReportModeOper::getReportModeId, id).list();
            xfptReportModeVo.setOperList(list);
            return success(xfptReportModeVo);
        }else {
            return error("未查询到数据");
        }
    }
    @Autowired
    private IXfptOrganizationModeService xfptOrganizationModeService;
    @Operation(summary = "发布公告")
    //@RequiresPermissions("school:organization:list")
    @GetMapping("/notice/list")
    public TableDataInfo noticeList(XfptOrganizationMode xfptOrganizationMode) {
        startPage();
        List<XfptOrganizationMode> list = xfptOrganizationModeService.selectXfptOrganizationModeList(xfptOrganizationMode);
        return getDataTable(list);
    }

    /**
     * 采购公告详细信息
     */
    @Operation(summary = "采购公告详细信息")
    //@RequiresPermissions("school:organization:query")
    @GetMapping(value = "/notice/{id}")
    public AjaxResult getNoticeInfo(@PathVariable("id") Long id) {
//        //查询供应商列表
//        List<XfptSupplierList> supplierList = iXfptSupplierListService
//                .list(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, id+"-"+PurchaseType.ZZXJ.getCode()));
        XfptOrganizationMode xfptOrganizationMode = xfptOrganizationModeService.getById(id);
//        xfptOrganizationMode.setSupplierList(supplierList);
        return success(xfptOrganizationMode);
    }

    @Autowired
    private IXfptRwggzsService xfptRwggzsService;
    @Autowired
    private IXfptSupplierListService iXfptSupplierListService;

    /**
     * 备选公告列表
     */
    @Operation(summary = "查询企业入围公告列表")
    //@RequiresPermissions("school:rwggzs:list")
    @GetMapping("/rwgg/list")
    public TableDataInfo rwggList(XfptRwggzs xfptRwggzs) {
        startPage();
        List<XfptRwggzs> list = xfptRwggzsService.selectXfptRwggzsList(xfptRwggzs);
        return getDataTable(list);
    }

    /**
     * 获取企业入围公告详细信息
     */
    @Operation(summary = "获取企业入围公告详细信息")
    //@RequiresPermissions("school:rwggzs:query")
    @GetMapping(value = "/rwgg/{id}")
    public AjaxResult getRwggInfo(@PathVariable("id") Long id) {
        //查询供应商列表
        List<XfptSupplierList> supplierList = iXfptSupplierListService
                .list(new LambdaQueryWrapper<XfptSupplierList>().eq(XfptSupplierList::getModeId, id+""));
        XfptRwggzs xfptRwggzs = xfptRwggzsService.getById(id);
        xfptRwggzs.setSupplierList(supplierList);
        return success(xfptRwggzs);
    }


    @Autowired
    private IXfptPdxzSureSupplierService xfptPdxzSureSupplierService;
    @Autowired
    private IXfptQuestionnaireConfigService xfptQuestionnaireConfigService;
    /**
     * 查询评定小组确定供应商列表
     */
    @Operation(summary = "查询评定小组确定供应商列表")
    //@RequiresPermissions("school:pdxzSupplier:list")
    @GetMapping("/pdxzcs/list")
    public TableDataInfo pdxzcsList(XfptPdxzSureSupplier xfptPdxzSureSupplier) {
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
     * 获取评定小组确定供应商详细信息
     */
    @Operation(summary = "获取评定小组确定供应商详细信息")
    //@RequiresPermissions("school:pdxzSupplier:query")
    @GetMapping(value = "/pdxzcs/{id}")
    public AjaxResult getPdxzcsInfo(@PathVariable("id") Long id) {
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
    @Autowired
    private IXfptAnnoResultsService xfptAnnoResultsService;
    @Autowired
    private IXfptReportModeService iXfptReportModeService;
    /**
     * 查询选用组织选购结果公示报备与公开列表
     */
    @Operation(summary = "查询选用组织选购结果公示报备与公开列表")
    //@RequiresPermissions("school:results:list")
    @GetMapping("/xgjg/list")
    public TableDataInfo xgjgList(XfptAnnoResults xfptAnnoResults) {
        startPage();
        xfptAnnoResults.setDeptId(null);
        List<XfptAnnoResults> list = xfptAnnoResultsService.selectXfptAnnoResultsList(xfptAnnoResults);
        if(CollUtil.isEmpty(list)){
            return getDataTable(list);
        }
        //查询成成交模式
        List<XfptReportMode> list1 = iXfptReportModeService.lambdaQuery().eq(XfptReportMode::getTeamId, list.get(0).getTeamId()).list();
        if(CollUtil.isEmpty(list1)){

            throw new ServiceException("未查询到选购模式");
        }

        //遍历枚举ReportModeStatus找到对应值
        list.stream().forEach(item->{
            String purchaseMode = list1.get(0).getPurchaseMode();
            if(StrUtil.isEmpty(purchaseMode)){
                item.setReportMode("");
            }else {
                item.setReportMode(getMenu(purchaseMode));
            }

        });
        return getDataTable(list);
    }
    /**
     * 获取选用组织选购结果公示报备与公开详细信息
     */
    @Operation(summary = "获取选用组织选购结果公示报备与公开详细信息")
    //@RequiresPermissions("school:results:query")
    @GetMapping(value = "/xgjg/{id}")
    public AjaxResult getXgjgInfo(@PathVariable("id") Long id) {
        XfptAnnoResults byId = xfptAnnoResultsService.getById(id);
        List<TeamMeetingSon> sonDataList = teamMeetingSonServiceImpl.selectTeamMeetingSonList(TeamMeetingSon.builder().teamMeetingId(byId.getTeamId()).build());
        byId.setSonDataList(sonDataList);
        return success(byId);
    }
    /**
     * 获取枚举info
     * @param purchaseMode
     * @return
     */
    private static String getMenu(String purchaseMode) {
        PurchaseType[] values = PurchaseType.values();
        for (int i = 0; i < values.length; i++) {
            String code = values[i].getCode();
            if(purchaseMode.equals(code)){
                return values[i].getInfo();
            }
        }
        return "";
    }
    @Autowired
    private IXfptSuppliceContractService xfptSuppliceContractService;

    @Autowired
    private ISupplierMessageService iSupplierMessageService;
    /**
     * 查询学校与供应商采购合同关联列表
     */
    @Operation(summary = "查询学校与供应商采购合同关联列表")
//@RequiresPermissions("system:contract:list")
    @GetMapping("/cght/list")
    public TableDataInfo cghtList(XfptSuppliceContract xfptSuppliceContract) {
        startPage();
        xfptSuppliceContract.setDeptId(null);
        List<XfptSuppliceContract> list = xfptSuppliceContractService.selectXfptSuppliceContractList(xfptSuppliceContract);
        for (XfptSuppliceContract xfptSuppliceContract1 : list) {
            SupplierMessage supplierMessage = iSupplierMessageService.getById(xfptSuppliceContract1.getSupplierId());
            xfptSuppliceContract1.setLegalRepresentative(supplierMessage.getLegalRepresentative());
        }
        return getDataTable(list);
    }
    @Autowired
    private ITeamMeetingSonService iTeamMeetingSonService;
    @Autowired
    private IXfptSuppliceContractSonService xfptSuppliceContractSonService;
    @Autowired
    private IXfptSuppliceContractService iXfptSuppliceContractService;
    @Value("${fileUrl}")
    public String fileUrl;
    @Autowired
    private MpUtil mpUtil;
    /**
     * 查询学校与供应商采购合同从列表
     */
    @Operation(summary = "查询学校与供应商采购合同从列表")
//    @RequiresPermissions("system:son:list")
    @GetMapping("/cghtson/list")
    public TableDataInfo cghtSonList(XfptSuppliceContractSon xfptSuppliceContractSon) {
        startPage();
        List<XfptSuppliceContractSon> list = xfptSuppliceContractSonService.selectXfptSuppliceContractSonList(xfptSuppliceContractSon);

        XfptSuppliceContract xfptSuppliceContract = iXfptSuppliceContractService.getById(xfptSuppliceContractSon.getSuppliceContractId());
        for (XfptSuppliceContractSon suppliceContractSon : list) {
            suppliceContractSon.setFzrdh(mpUtil.decrypt(suppliceContractSon.getFzrdh(), mpUtil.getToken()));
            //查询子表数据
            List<TeamMeetingSon> sonDataList = iTeamMeetingSonService.selectTeamMeetingSonList(TeamMeetingSon.builder().teamMeetingId(Long.parseLong(xfptSuppliceContract.getTeamMeetingId())).build());
            suppliceContractSon.setTeamMeetingSonList(sonDataList);
            if(StrUtil.isNotEmpty(suppliceContractSon.getIntegrity())){
                String mac = mpUtil.mac(suppliceContractSon.getUrl());
                if(!suppliceContractSon.getIntegrity().equals(mac)){
                    throw new ServiceException("完整性校验失败，数据被篡改");
                }
            }
        }
        return getDataTable(list);
    }
    @Autowired
    private IXfptSuppliceInspectionReportService xfptSuppliceInspectionReportService;
    /**
     * 查询学校与供应商检验报告关联列表
     */
    @Operation(summary = "查询学校与供应商检验报告关联列表")
//@RequiresPermissions("system:report:list")
    @GetMapping("/zjbg/list")
    public TableDataInfo zjbgList(XfptSuppliceInspectionReport xfptSuppliceInspectionReport) {
        startPage();
        xfptSuppliceInspectionReport.setDeptId(null);
        List<XfptSuppliceInspectionReport> list = xfptSuppliceInspectionReportService.selectXfptSuppliceInspectionReportList(xfptSuppliceInspectionReport);
        for (XfptSuppliceInspectionReport suppliceInspectionReport : list) {
            SupplierMessage supplierMessage = iSupplierMessageService.getById(suppliceInspectionReport.getSupplierId());
            suppliceInspectionReport.setLegalRepresentative(supplierMessage.getLegalRepresentative());
        }
        return getDataTable(list);
    }
    @Autowired
    private IXfptSuppliceInspectionReportSonService xfptSuppliceInspectionReportSonService;
    /**
     * 查询学校与供应商检验报告从列表
     */
    @Operation(summary = "查询学校与供应商检验报告从列表")
//@RequiresPermissions("system:son:list")
    @GetMapping("/zjbgson/list")
    public TableDataInfo zjbgSonlist(XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon) {
        startPage();
        List<XfptSuppliceInspectionReportSon> list = xfptSuppliceInspectionReportSonService.selectXfptSuppliceInspectionReportSonList(xfptSuppliceInspectionReportSon);
        return getDataTable(list);
    }

    @Autowired
    private IXfptPatriarchSatisfactionSummarizeService xfptPatriarchSatisfactionSummarizeService;
    /**
     * 查询选用组织家长满意度总结报告报备与公开列表
     */
    @Operation(summary = "查询选用组织家长满意度总结报告报备与公开列表")
//@RequiresPermissions("school:summarize:list")
    @GetMapping("/jzmyd/list")
    public TableDataInfo jzmydList(XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize) {
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
     * 获取选用组织家长满意度总结报告报备与公开详细信息
     */
    @Operation(summary = "获取选用组织家长满意度总结报告报备与公开详细信息")
//    @RequiresPermissions("school:summarize:query")
    @GetMapping(value = "/jzmyd/{id}")
    public AjaxResult getJzmydInfo(@PathVariable("id") Long id) {
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

    @Autowired
    private IXfptProcSummaryService xfptProcSummaryService;
    /**
     * 获取采购汇总信息详细信息
     */
    @Operation(summary = "获取采购汇总信息详细信息")
    //@RequiresPermissions("school:summary:query")
    @GetMapping(value = "/cgxxbb/{id}")
    public AjaxResult getCgxxbbInfo(@PathVariable("id") Long id) {


        return success(xfptProcSummaryService.lambdaQuery().eq(XfptProcSummary::getTeamId,id).one());
    }
    @Autowired
    private IXfptCglcgzzjService xfptCglcgzzjService;
    /**
     * 查询学校校服采购工作总结列表
     */
    @Operation(summary = "查询学校校服采购工作总结列表")
    //@RequiresPermissions("school:cglcgzzj:list")
    @GetMapping("/cggzzj/list")
    public TableDataInfo cggzzjList(XfptCglcgzzj xfptCglcgzzj) {
        startPage();
        List<XfptCglcgzzj> list = xfptCglcgzzjService.selectXfptCglcgzzjList(xfptCglcgzzj);
        return getDataTable(list);
    }
    /**
     * 获取学校校服采购工作总结详细信息
     */
    @Operation(summary = "获取学校校服采购工作总结详细信息")
    //@RequiresPermissions("school:cglcgzzj:query")
    @GetMapping(value = "/cggzzj/{id}")
    public AjaxResult getCggzzjInfo(@PathVariable("id") Long id) {
        return success(xfptCglcgzzjService.getById(id));
    }
}
