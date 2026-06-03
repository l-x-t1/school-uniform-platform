package com.xfgl.school.controller;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.domain.BaseEntity;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.*;
import com.xfgl.school.domain.vo.UserName;
import com.xfgl.school.domain.vo.XfptProcessCGStatisticsVO;
import com.xfgl.school.domain.vo.XfptSchoolStatisticsVO;
import com.xfgl.school.mapper.XfptProcSummaryMapper;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteDictService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 班子会议Controller
 *
 * @author wxm
 * @date 2025-04-22
 */
@Tag(name = "班子会议管理")
@RestController
@RequestMapping("/team/meeting")
public class TeamMeetingController extends BaseController {
    @Autowired
    private ITeamMeetingService teamMeetingService;
    @Autowired
    private ITeamMeetingSonService teamMeetingSonServiceImpl;
    @Autowired
    private RemoteDeptService remoteDeptService;
    @Autowired
    private IXfptJzBuyOpinionsService jzBuyOpinionsService;
    @Autowired
    private IXfptOrgReportDiscloseService orgReportDiscloseService;
    @Autowired
    private IXfptReportModeService reportModeService;
    @Autowired
    private IXfptAnnoResultsService annoResultsService;
    @Autowired
    private IXfptSuppliceContractService suppliceContractService;
    @Autowired
    private IXfptSuppliceInspectionReportService suppliceInspectionReportService;
    @Autowired
    private IXfptRwggzsService rwggzsService;
    @Autowired
    private IXfptPdxzSureSupplierService pdxzSureSupplierService;
    @Autowired
    private IXfptPatriarchSatisfactionSummarizeService patriarchSatisfactionSummarizeService;
    @Autowired
    private IXfptSchoolMessageService schoolMessageService;
    @Autowired
    private RemoteDictService remoteDictService;
    @Autowired
    private XfptProcSummaryMapper xfptProcSummaryMapper;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private IXfptCglcgzzjService iXfptCglcgzzjService;
    @Autowired
    private IXfptReportModeService iXfptReportModeService;

    /**
     * 获取字典数据 key：label value: value
     *
     * @param dictType
     * @return
     */
    public Map<String, String> getDictLabelValueMap(String dictType) {
        R<Map<String, String>> sdf = remoteDictService.getDictDataMap(dictType, 1, SecurityConstants.INNER);
        return sdf.getData();
    }

    /**
     * 获取字典数据 key：value value: label
     *
     * @param dictType
     * @return
     */
    public Map<String, String> getDictValueLabelMap(String dictType) {
        R<Map<String, String>> sdf = remoteDictService.getDictDataMap(dictType, 2, SecurityConstants.INNER);
        return sdf.getData();
    }

    private XfptProcessCGStatisticsVO buildProcessStatistics(String name, Long deptId, List<XfptSchoolStatisticsVO> dataList) {
        return XfptProcessCGStatisticsVO.builder()
                .name(name)
                .deptId(deptId)
                .bzhyTotal(countReported(dataList, XfptSchoolStatisticsVO::getBzhyFlg))
                .jzgmyjTotal(countReported(dataList, XfptSchoolStatisticsVO::getJzgmyjFlg))
                .zzmdTotal(countReported(dataList, XfptSchoolStatisticsVO::getZzmdFlg))
                .zzxgmsTotal(countReported(dataList, XfptSchoolStatisticsVO::getXyzzxgmsFlg))
                .xgjggsTotal(countReported(dataList, XfptSchoolStatisticsVO::getXgjggsFlg))
                .cghtTotal(countReported(dataList, XfptSchoolStatisticsVO::getCghtFlg))
                .cgjgggTotal(countReported(dataList, XfptSchoolStatisticsVO::getCgjgggFlg))
                .cpzljcTotal(countReported(dataList, XfptSchoolStatisticsVO::getCpzljybgFlg))
                .jzmydzjbgTotal(countReported(dataList, XfptSchoolStatisticsVO::getJzmydzjbgFlg))
                .zjbbTotal(countReported(dataList, XfptSchoolStatisticsVO::getZjbbFlg))
                .cggzzjTotal(countReported(dataList, XfptSchoolStatisticsVO::getCggzzjFlag))
                .build();
    }

    private XfptProcessCGStatisticsVO buildProcessStatisticsSummary(String name, List<XfptProcessCGStatisticsVO> dataList) {
        return XfptProcessCGStatisticsVO.builder()
                .name(name)
                .bzhyTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getBzhyTotal()).orElse(0L)).sum())
                .jzgmyjTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getJzgmyjTotal()).orElse(0L)).sum())
                .zzmdTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getZzmdTotal()).orElse(0L)).sum())
                .zzxgmsTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getZzxgmsTotal()).orElse(0L)).sum())
                .xgjggsTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getXgjggsTotal()).orElse(0L)).sum())
                .cghtTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getCghtTotal()).orElse(0L)).sum())
                .cgjgggTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getCgjgggTotal()).orElse(0L)).sum())
                .cpzljcTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getCpzljcTotal()).orElse(0L)).sum())
                .jzmydzjbgTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getJzmydzjbgTotal()).orElse(0L)).sum())
                .zjbbTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getZjbbTotal()).orElse(0L)).sum())
                .cggzzjTotal(dataList.stream().mapToLong(item -> Optional.ofNullable(item.getCggzzjTotal()).orElse(0L)).sum())
                .build();
    }

    private Long countReported(List<XfptSchoolStatisticsVO> dataList, Function<XfptSchoolStatisticsVO, String> flagGetter) {
        return dataList.stream()
                .filter(item -> "已报备".equals(flagGetter.apply(item)))
                .count();
    }

    private String resolveSchoolTypeName(String schoolTypeValue, Map<String, String> schoolTypeDictMap) {
        return schoolTypeDictMap.getOrDefault(schoolTypeValue, "其他");
    }

    /**
     * 查询班子会议列表
     */
    @Operation(summary = "查询班子会议列表")
    //@RequiresPermissions("system:meeting:list")
    @GetMapping("/list")
    public TableDataInfo list(TeamMeeting teamMeeting) {
        if (teamMeeting.getDeptId() == null) {
            teamMeeting.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId());
        }
        startPage();
        List<TeamMeeting> list = teamMeetingService.selectTeamMeetingList(teamMeeting);
        for (TeamMeeting meetingCurr : list) {
            meetingCurr.setTeamMeetingSonList(new ArrayList<>());
        }
        return getDataTable(list);
    }

    /**
     * 导出班子会议列表
     */
    @Operation(summary = "导出班子会议列表")
    @RequiresPermissions("team:meeting:export")
    @Log(title = "班子会议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TeamMeeting teamMeeting) {
        List<TeamMeeting> list = teamMeetingService.selectTeamMeetingList(teamMeeting);
        ExcelUtil<TeamMeeting> util = new ExcelUtil<TeamMeeting>(TeamMeeting.class);
        util.exportExcel(response, list, "班子会议数据");
    }

    /**
     * 获取班子会议详细信息
     */
    @Operation(summary = "获取班子会议详细信息")
    //@RequiresPermissions("team:meeting:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        TeamMeeting dataEntity = new TeamMeeting();
//        if (id == null) {
//            return AjaxResult.error("获取标识失败！");
//        }
        List<TeamMeeting> teamMeetings = teamMeetingService.selectTeamMeetingList(TeamMeeting.builder().id(id).build());
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

    /**
     * 新增班子会议
     */
    @Operation(summary = "新增班子会议")
    //@RequiresPermissions("team:meeting:add")
    @Log(title = "班子会议", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody TeamMeeting teamMeeting) {
        try {
            String timeTb = 1 + "#" + ProceConstants.BZHY + "#" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            teamMeeting.setCurrOrder(1L);
            teamMeeting.setTimeTb(timeTb);
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
            teamMeeting.setDeptId(deptId);
            teamMeeting.setAncestors(ancestors);
            boolean save = teamMeetingService.save(teamMeeting);
            String sonJsonData = teamMeeting.getSonJsonData();
            if (StringUtils.isBlank(sonJsonData)) {
                return AjaxResult.error("选购需求不能为空！");
            }
            //System.out.println("sonJsonData:" + sonJsonData);
            List<TeamMeetingSon> teamMeetingSonDataList = com.alibaba.fastjson.JSON.parseObject(sonJsonData, new TypeReference<List<TeamMeetingSon>>() {
            });
            if (save && !CollectionUtils.isEmpty(teamMeetingSonDataList)) {
                //绑定主键id
                teamMeetingSonDataList.forEach(e -> {
                    e.setTeamMeetingId(teamMeeting.getId());
                });
                teamMeetingSonServiceImpl.saveBatch(teamMeetingSonDataList);
            }
            return AjaxResult.success(teamMeeting.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改班子会议
     */
    @Operation(summary = "修改班子会议")
    //@RequiresPermissions("team:meeting:edit")
    @Log(title = "班子会议", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TeamMeeting teamMeeting) {
        Long id = teamMeeting.getId();
        if (id == null) {
            return AjaxResult.error("获取标识失败！");
        }
        try {
            teamMeeting.setUpdateTime(new Date());
            teamMeeting.setUpdateBy(SecurityUtils.getUsername());
            int updateFlg = teamMeetingService.updateTeamMeeting(teamMeeting);
            String sonJsonData = teamMeeting.getSonJsonData();
            if (StringUtils.isNotBlank(sonJsonData) && !sonJsonData.equals("[]") && updateFlg > 0) {
                List<TeamMeetingSon> teamMeetingSonDataList = JSON.parseObject(sonJsonData, new TypeReference<List<TeamMeetingSon>>() {
                });
                if (!CollectionUtils.isEmpty(teamMeetingSonDataList)) {
                    teamMeetingSonDataList.forEach(e -> {
                        e.setTeamMeetingId(id);
                    });
                }
                teamMeetingSonServiceImpl.saveOrUpdateBatch(teamMeetingSonDataList);
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return toAjax(teamMeetingService.updateTeamMeeting(teamMeeting));
    }


    /**
     * 删除班子会议
     */
    @Operation(summary = "删除班子会议")
    //@RequiresPermissions("team:meeting:remove")
    @Log(title = "班子会议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids.length < 1) {
            return AjaxResult.error("请选择需要删除的数据！");
        }
        try {
            teamMeetingService.removeByIds(Arrays.asList(ids));
            List<Long> teamMeetingIdList = Arrays.stream(ids).collect(Collectors.toList());
            LambdaUpdateWrapper<TeamMeetingSon> sonLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            sonLambdaUpdateWrapper.in(TeamMeetingSon::getTeamMeetingId, teamMeetingIdList);
            teamMeetingSonServiceImpl.remove(sonLambdaUpdateWrapper);
            return toAjax(1);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除子表数据
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteSonDataById/{id}")
    public AjaxResult delSonData(@PathVariable("id") Long id) {
        if (id == null) {
            return AjaxResult.error("获取标识失败！");
        }
        return toAjax(teamMeetingSonServiceImpl.removeById(id));
    }

    /**
     * 修改教育局查阅状态为：未查阅状态
     *
     * @param teamMeeting
     * @return
     */
    @PostMapping("/modifyJyjStatus")
    public AjaxResult modifyJyjStatus(@RequestBody TeamMeeting teamMeeting) {
        Long id = teamMeeting.getId();
        if (id == null) {
            return AjaxResult.error("获取标识失败！");
        }
        Integer jyjStatus = teamMeeting.getJyjStatus();
        if (jyjStatus == null) {
            return AjaxResult.error("撤回状态不能为空！！");
        }
        int effectRow = teamMeetingService.updateTeamMeeting(TeamMeeting.builder().id(id).jyjStatus(jyjStatus).build());
        return toAjax(effectRow);
    }


    /**
     * 获取教育局直辖的区县：采购规范流程统计数据
     *
     * @param parentDeptId 要查询哪个部门时，传入哪个部门的id
     * @return
     */
    @GetMapping("/getStatisticsDataList")
    public AjaxResult getStatisticsDataList(@RequestParam("parentDeptId") Long parentDeptId,@RequestParam(value = "beginTime",required = false) String beginTime,
                                            @RequestParam(value = "endTime",required = false) String endTime) {
        BaseEntity entity = new BaseEntity();
        Map<String, Object> params = entity.getParams();
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
        }
        entity.setParams(params);
        List<XfptProcessCGStatisticsVO> finalDataList = getStep_one_list(parentDeptId, entity);
        return success(finalDataList);
    }

    /**
     * 导出区县统计数据
     */
    @Operation(summary = "导出采购规范流程统计数据区县统计数据")
    @Log(title = "班子会议", businessType = BusinessType.EXPORT)
    @PostMapping("/exportDistrictCountryStatistics")
    public void exportDistrictCountryStatistics(HttpServletResponse response,
                                                @RequestParam(value = "parentDeptId",required = false) Long parentDeptId,
                                                @RequestParam(value = "beginTime",required = false) String beginTime,
                                                @RequestParam(value = "endTime",required = false) String endTime) {
        BaseEntity entity = new BaseEntity();
        Map<String, Object> params = entity.getParams();
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
        }
        entity.setParams(params);
        List<XfptProcessCGStatisticsVO> finalDataList = getStep_one_list(parentDeptId, entity);
        ExcelUtil<XfptProcessCGStatisticsVO> util = new ExcelUtil<>(XfptProcessCGStatisticsVO.class);
        util.exportExcel(response, finalDataList, "采购规范流程统计数据区县统计数据");
    }


    /**
     * 获取市教育局直辖的区县部门数据
     *
     * @param parentDeptId 查询哪个部门，就是哪个部门的id
     * @return
     */
    @GetMapping("/getEducationDistrictAndCountryList/{parentDeptId}")
    public AjaxResult getEducationDistrictAndCountryList(@PathVariable("parentDeptId") Long parentDeptId) {
        List<SysDept> deptList = getDeptList(parentDeptId);
        return success(deptList);
    }


    public List<SysDept> getDeptList(Long parentDeptId) {
        //获取所有市教育局的部门列表
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        List<SysDept> deptList = xfptProcSummaryMapper.selectDeptBytypeList();
        if (!admin) {
            if (parentDeptId != null && parentDeptId != 100L) {
                //其他部门只查看自己部门列表
                deptList.removeIf(e -> !e.getDeptId().equals(parentDeptId));
            }
        }
        deptList = deptList.stream().sorted(Comparator.comparing(SysDept::getOrderNum)).collect(Collectors.toList());
        return deptList;
    }

    public List<SysDept> getSonDeptList(Long parentDeptId) {
        //获取指定部门的所有子部门
        return xfptProcSummaryMapper.selectDeptList(parentDeptId);
    }

    /**
     * 获取第一阶段的统计数据，仅查询市教育局直辖区县的 11 步流程报备学校数
     *
     * @param parentDeptId 查询哪个部门就是哪个部门的id
     * @param entity       这里主要使用params['beginTime'] 和 params['endTime']
     * @return
     */
    public List<XfptProcessCGStatisticsVO> getStep_one_list(Long parentDeptId, BaseEntity entity) {
        if (System.currentTimeMillis() >= 0) {
            boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
            if (!admin) {
                parentDeptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
            }
            List<SysDept> deptList = getDeptList(parentDeptId);
            List<XfptProcessCGStatisticsVO> finalDataList = deptList.stream()
                    .map(dept -> buildProcessStatistics(
                            dept.getDeptName(),
                            dept.getDeptId(),
                            getSchoolBaoBeiSituationDataList(dept.getDeptId(), entity, XfptSchoolStatisticsVO.builder().build(), false)
                    ))
                    .collect(Collectors.toList());
            finalDataList.add(0, buildProcessStatisticsSummary("总数", finalDataList));
            return finalDataList;
        }

        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (!admin) {
            parentDeptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        }
        final Long finalParentDeptId = parentDeptId;
        List<SysDept> deptList = getDeptList(parentDeptId);
        TeamMeeting teamMeetingCondition = new TeamMeeting();
        teamMeetingCondition.setParams(entity.getParams());
        //查询符合条件的班子会议数据
        List<TeamMeeting> teamMeetingList = teamMeetingService.selectTeamMeetingList(teamMeetingCondition);
        //只保留归属当前部门的数据
        teamMeetingList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
        //获取所有班子会议id集合
        List<Long> teamMeetingIdList = teamMeetingList.stream().map(TeamMeeting::getId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        boolean emptyTeamMeetingFlg = CollectionUtils.isEmpty(teamMeetingIdList);
        //注：以下为旧统计逻辑，保留兼容；运行时已切换到新的 11 步统计逻辑
        //查询家长购买意见征集报备
        List<XfptJzBuyOpinions> jzBuyOpinionList = new ArrayList<>();
        //选用组织名单报备学校数
        List<XfptOrgReportDisclose> orgReportDiscloseList = new ArrayList<>();
        //选用组织选购模式报备学校数
        List<XfptReportMode> reportModeList = new ArrayList<>();
        //选购结果公示报备学校数
        List<XfptAnnoResults> xuangResultList = new ArrayList<>();
        //采购合同报备
        List<XfptSuppliceContract> caiGouHtList = new ArrayList<>();
        //产品质量检测报告报备学校数
        List<XfptSuppliceInspectionReport> chanPinReportList = new ArrayList<>();
        //备选公告展示
        List<XfptRwggzs> rwggzsList = new ArrayList<>();
        //家长投票
        List<XfptPdxzSureSupplier> parentVoteList = new ArrayList<>();
        //家长满意度总结报告报备学校数
        List<XfptPatriarchSatisfactionSummarize> jzManYdReportList = new ArrayList<>();
        //家长满意度总结报告报备学校数
        List<XfptProcSummary> jXfptProcSummaryList = new ArrayList<>();
        //校服平台工作总结
        List<XfptCglcgzzj> xfptCglcgzzjsList = new ArrayList<>();

        if (!emptyTeamMeetingFlg) {
            jzBuyOpinionList = jzBuyOpinionsService.list(new LambdaQueryWrapper<XfptJzBuyOpinions>().in(XfptJzBuyOpinions::getTeamMeetingId, teamMeetingIdList));
            jzBuyOpinionList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            orgReportDiscloseList = orgReportDiscloseService.list(new LambdaQueryWrapper<XfptOrgReportDisclose>().in(XfptOrgReportDisclose::getTeamMeetingId, teamMeetingIdList));
            orgReportDiscloseList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            reportModeList = reportModeService.list(new LambdaQueryWrapper<XfptReportMode>().in(XfptReportMode::getTeamId, teamMeetingIdList));
            reportModeList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            xuangResultList = annoResultsService.list(new LambdaQueryWrapper<XfptAnnoResults>().in(XfptAnnoResults::getTeamId, teamMeetingIdList));
            xuangResultList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            caiGouHtList = suppliceContractService.list(new LambdaQueryWrapper<XfptSuppliceContract>().in(XfptSuppliceContract::getTeamMeetingId, teamMeetingIdList));
            caiGouHtList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            chanPinReportList = suppliceInspectionReportService.list(new LambdaQueryWrapper<XfptSuppliceInspectionReport>().in(XfptSuppliceInspectionReport::getTeamMeetingId, teamMeetingIdList));
            chanPinReportList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            rwggzsList = rwggzsService.list(new LambdaQueryWrapper<XfptRwggzs>().in(XfptRwggzs::getTeamId, teamMeetingIdList));
            rwggzsList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            parentVoteList = pdxzSureSupplierService.list(new LambdaQueryWrapper<XfptPdxzSureSupplier>().in(XfptPdxzSureSupplier::getTeamId, teamMeetingIdList));
            parentVoteList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            jzManYdReportList = patriarchSatisfactionSummarizeService.list(new LambdaQueryWrapper<XfptPatriarchSatisfactionSummarize>().in(XfptPatriarchSatisfactionSummarize::getTeamMeetingId, teamMeetingIdList));
            jzManYdReportList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            jXfptProcSummaryList=iXfptProcSummaryService.list(new LambdaQueryWrapper<XfptProcSummary>().in(XfptProcSummary::getTeamId, teamMeetingIdList));
            jXfptProcSummaryList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
            xfptCglcgzzjsList=iXfptCglcgzzjService.list(new LambdaQueryWrapper<XfptCglcgzzj>().in(XfptCglcgzzj::getTeamId, teamMeetingIdList));
            xfptCglcgzzjsList.removeIf(e -> !e.getAncestors().contains(finalParentDeptId.toString()));
        }
        List<XfptAnnoResults> voteResultNoticeList = xuangResultList.stream().filter(e -> "1".equals(e.getStageType())).collect(Collectors.toList());
        List<XfptAnnoResults> purchaseResultNoticeList = xuangResultList.stream().filter(e -> "2".equals(e.getStageType())).collect(Collectors.toList());
        List<XfptProcessCGStatisticsVO> finalDataList = new ArrayList<>();
        Long bzhyTotal = 0L;
        Long jzgmyjTotal = 0L;
        Long zzmdTotal = 0L;
        Long zzxgmsTotal = 0L;
        Long xgjggsTotal = 0L;
        Long cghtTotal = 0L;
        Long cgjgggTotal = 0L;
        Long cpzljcTotal = 0L;
        Long jzmydzjbgTotal = 0L;
        Long zjbbTotal = 0L;
        Long cggzzjTotal = 0L;
        for (SysDept sysDeptCurr : deptList) {
            Long deptId = sysDeptCurr.getDeptId();
            String deptIdStr = deptId.toString();
            //班子会议：本部门及部门以下的数据 以下逻辑相同
            Long bzhyCurr = teamMeetingList.stream().filter(e -> e.getDeptId().equals(deptId)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (bzhyCurr > 0) {
                bzhyTotal += bzhyCurr;
            }
            //查询家长购买意见征集报备
            Long jzgmyjCurr = reportModeList.stream().filter(e -> e.getDeptId().equals(deptId)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (jzgmyjCurr > 0) {
                jzgmyjTotal += jzgmyjCurr;
            }
            //选用组织名单报备学校数
            Long zzmdCurr = rwggzsList.stream().filter(e -> e.getDeptId().equals(deptId)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (zzmdCurr > 0) {
                zzmdTotal += zzmdCurr;
            }
            //选用组织选购模式报备学校数
            Long zzxgmsCurr = parentVoteList.stream().filter(e -> e.getDeptId().equals(deptId)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (zzxgmsCurr > 0) {
                zzxgmsTotal += zzxgmsCurr;
            }
            //选购结果公示报备学校数
            Long xgjggsCurr = voteResultNoticeList.stream().filter(e -> e.getDeptId().equals(deptId)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (xgjggsCurr > 0) {
                xgjggsTotal += xgjggsCurr;
            }
            //采购合同报备
            Long cghtCurr = caiGouHtList.stream().filter(e -> e.getDeptId().equals(deptIdStr)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (cghtCurr > 0) {
                cghtTotal += cghtCurr;
            }
            //采购结果公告
            Long cgjgggCurr = purchaseResultNoticeList.stream().filter(e -> e.getDeptId().equals(deptId)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (cgjgggCurr > 0) {
                cgjgggTotal += cgjgggCurr;
            }
            //产品质量检测报告报备学校数
            Long cpzljcCurr = chanPinReportList.stream().filter(e -> e.getDeptId().equals(deptIdStr)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (cpzljcCurr > 0) {
                cpzljcTotal += cpzljcCurr;
            }
            //家长满意度总结报告报备学校数
            for (XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize : jzManYdReportList) {
                if(StrUtil.isEmpty(xfptPatriarchSatisfactionSummarize.getDeptId())){
                    System.out.println(xfptPatriarchSatisfactionSummarize.getDeptId());
                }
                if(StrUtil.isEmpty(xfptPatriarchSatisfactionSummarize.getAncestors())){
                    System.out.println(xfptPatriarchSatisfactionSummarize.getDeptId());
                }
            }
            Long jzmydzjbgCurr = jzManYdReportList.stream().filter(e -> e.getDeptId().equals(deptIdStr)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (jzmydzjbgCurr > 0) {
                jzmydzjbgTotal += jzmydzjbgCurr;
            }
            //总结报备
            Long zjbbCurr = jXfptProcSummaryList.stream().filter(e -> e.getDeptId().equals(deptIdStr)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (zjbbCurr > 0) {
                zjbbTotal += zjbbCurr;
            }
            //总结报备
            Long cggzzjCurr = xfptCglcgzzjsList.stream().filter(e -> e.getDeptId().equals(deptIdStr)
                    || e.getAncestors().contains(deptIdStr)).count();
            if (cggzzjCurr > 0) {
                cggzzjTotal += cggzzjCurr;
            }
            XfptProcessCGStatisticsVO totalSummaryCurr = XfptProcessCGStatisticsVO.builder()
                    .bzhyTotal(bzhyCurr)
                    .jzgmyjTotal(jzgmyjCurr)
                    .zzmdTotal(zzmdCurr)
                    .zzxgmsTotal(zzxgmsCurr)
                    .xgjggsTotal(xgjggsCurr)
                    .cghtTotal(cghtCurr)
                    .cgjgggTotal(cgjgggCurr)
                    .cpzljcTotal(cpzljcCurr)
                    .jzmydzjbgTotal(jzmydzjbgCurr)
                    .zjbbTotal(zjbbCurr)
                    .cggzzjTotal(cggzzjCurr)
                    .name(sysDeptCurr.getDeptName())
                    .deptId(sysDeptCurr.getDeptId())
                    .build();
            finalDataList.add(totalSummaryCurr);
        }
        //填充总数据
        XfptProcessCGStatisticsVO totalSummaryVo = XfptProcessCGStatisticsVO.builder()
                .bzhyTotal(bzhyTotal)
                .jzgmyjTotal(jzgmyjTotal)
                .zzmdTotal(zzmdTotal)
                .zzxgmsTotal(zzxgmsTotal)
                .xgjggsTotal(xgjggsTotal)
                .cghtTotal(cghtTotal)
                .cgjgggTotal(cgjgggTotal)
                .cpzljcTotal(cpzljcTotal)
                .jzmydzjbgTotal(jzmydzjbgTotal)
                .jzgmyjTotal(jzgmyjTotal)
                .zjbbTotal(zjbbTotal)
                .cggzzjTotal(cggzzjTotal)
                .name("总数")
                .build();
        finalDataList.add(0, totalSummaryVo);

        return finalDataList;
    }


    /**
     * 获取区县直辖的学校数据：采购规范流程统计数据
     *
     * @param parentDeptId 要查询哪个部门时，传入哪个部门的id
     * @return
     */
    @GetMapping("/getStatisticsSchoolDataList")
    public AjaxResult getStatisticsSchoolDataList(@RequestParam("parentDeptId") Long parentDeptId, BaseEntity entity) {
        List<XfptProcessCGStatisticsVO> finalDataList = getStep_school_list(parentDeptId, entity);
        return success(finalDataList);
    }

    /**
     * 获取第二阶段的统计数据，仅查询特定区县下 11 步流程报备学校数
     *
     * @param parentDeptId 查询哪个部门就是哪个部门的id
     * @param entity       这里主要使用params['beginTime'] 和 params['endTime']
     * @return
     */
    public List<XfptProcessCGStatisticsVO> getStep_school_list(Long parentDeptId, BaseEntity entity) {
        if (System.currentTimeMillis() >= 0) {
            Map<String, String> schoolTypeDictMap = Optional.ofNullable(getDictValueLabelMap("sc_sc_type")).orElse(Collections.emptyMap());
            List<XfptSchoolStatisticsVO> dataList =
                    getSchoolBaoBeiSituationDataList(parentDeptId, entity, XfptSchoolStatisticsVO.builder().build(), false);
            List<String> categoryList = Stream.of("高级中学", "初级中学", "小学", "幼儿园", "其他").collect(Collectors.toList());
            List<XfptProcessCGStatisticsVO> finalDataList = categoryList.stream()
                    .map(category -> buildProcessStatistics(
                            category,
                            null,
                            dataList.stream()
                                    .filter(item -> category.equals(resolveSchoolTypeName(item.getSchoolType(), schoolTypeDictMap)))
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
            finalDataList.add(0, buildProcessStatistics("总数", null, dataList));
            return finalDataList;
        }

        List<SysDept> deptList = getSonDeptList(parentDeptId);
        List<Long> sonDeptIdList = deptList.stream().map(e -> e.getDeptId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sonDeptIdList)) {
            //如果没有子部门数据，则直接只查询自己的部门数据
            sonDeptIdList.add(parentDeptId);
        }
        //查询学校信息 XFPT_SCHOOL_MESSAGE
        List<XfptSchoolMessage> schoolMessageList = schoolMessageService.list(new LambdaQueryWrapper<XfptSchoolMessage>().in(XfptSchoolMessage::getDeptId, sonDeptIdList));
       /* if (CollectionUtils.isEmpty(schoolMessageList)) {
            throw new RuntimeException("没有获取到学校信息，请联系管理员！");
        }*/
        //学校部门id和学校类别字典映射
        Map<String, String> deptId_schoolType_map = schoolMessageList.stream().collect(Collectors.toMap(e -> e.getDeptId().toString(), e -> e.getSchoolType()));
        //学校类别字典
        Map<String, String> schoolTypeDictMap = getDictValueLabelMap("sc_sc_type");
        TeamMeeting teamMeetingCondition = new TeamMeeting();
        teamMeetingCondition.setParams(entity.getParams());
        //查询符合条件的班子会议数据
        List<TeamMeeting> teamMeetingList = teamMeetingService.selectTeamMeetingList(teamMeetingCondition);
        //只保留归属于当前参数部门的数据
        teamMeetingList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
        //获取所有班子会议id集合
        List<Long> teamMeetingIdList = teamMeetingList.stream().map(TeamMeeting::getId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        boolean emptyTeamMeetingFlg = CollectionUtils.isEmpty(teamMeetingIdList);
        //注：以下相关模块均做关联班子会议处理
        //查询家长购买意见征集报备
        List<XfptJzBuyOpinions> jzBuyOpinionList = new ArrayList<>();
        //选用组织名单报备学校数
        List<XfptOrgReportDisclose> orgReportDiscloseList = new ArrayList<>();
        //选用组织选购模式报备学校数
        List<XfptReportMode> reportModeList = new ArrayList<>();
        //选购结果公示报备学校数
        List<XfptAnnoResults> xuangResultList = new ArrayList<>();
        //采购合同报备
        List<XfptSuppliceContract> caiGouHtList = new ArrayList<>();
        //产品质量检测报告报备学校数
        List<XfptSuppliceInspectionReport> chanPinReportList = new ArrayList<>();
        //家长满意度总结报告报备学校数
        List<XfptPatriarchSatisfactionSummarize> jzManYdReportList = new ArrayList<>();
        if (!emptyTeamMeetingFlg) {
            jzBuyOpinionList = jzBuyOpinionsService.list(new LambdaQueryWrapper<XfptJzBuyOpinions>().in(XfptJzBuyOpinions::getTeamMeetingId, teamMeetingIdList));
            jzBuyOpinionList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
            orgReportDiscloseList = orgReportDiscloseService.list(new LambdaQueryWrapper<XfptOrgReportDisclose>().in(XfptOrgReportDisclose::getTeamMeetingId, teamMeetingIdList));
            orgReportDiscloseList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
            reportModeList = reportModeService.list(new LambdaQueryWrapper<XfptReportMode>().in(XfptReportMode::getTeamId, teamMeetingIdList));
            reportModeList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
            xuangResultList = annoResultsService.list(new LambdaQueryWrapper<XfptAnnoResults>().in(XfptAnnoResults::getTeamId, teamMeetingIdList));
            xuangResultList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
            caiGouHtList = suppliceContractService.list(new LambdaQueryWrapper<XfptSuppliceContract>().in(XfptSuppliceContract::getTeamMeetingId, teamMeetingIdList));
            caiGouHtList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
            chanPinReportList = suppliceInspectionReportService.list(new LambdaQueryWrapper<XfptSuppliceInspectionReport>().in(XfptSuppliceInspectionReport::getTeamMeetingId, teamMeetingIdList));
            chanPinReportList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
            jzManYdReportList = patriarchSatisfactionSummarizeService.list(new LambdaQueryWrapper<XfptPatriarchSatisfactionSummarize>().in(XfptPatriarchSatisfactionSummarize::getTeamMeetingId, teamMeetingIdList));
            jzManYdReportList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
        }
        //高级中学
        Long gj_bzhyTotal = 0L;
        Long gj_jzgmyjTotal = 0L;
        Long gj_zzmdTotal = 0L;
        Long gj_zzxgmsTotal = 0L;
        Long gj_xgjggsTotal = 0L;
        Long gj_cghtTotal = 0L;
        Long gj_cpzljcTotal = 0L;
        Long gj_jzmydzjbgTotal = 0L;
        //初级中学
        Long cj_bzhyTotal = 0L;
        Long cj_jzgmyjTotal = 0L;
        Long cj_zzmdTotal = 0L;
        Long cj_zzxgmsTotal = 0L;
        Long cj_xgjggsTotal = 0L;
        Long cj_cghtTotal = 0L;
        Long cj_cpzljcTotal = 0L;
        Long cj_jzmydzjbgTotal = 0L;
        //小学
        Long xx_bzhyTotal = 0L;
        Long xx_jzgmyjTotal = 0L;
        Long xx_zzmdTotal = 0L;
        Long xx_zzxgmsTotal = 0L;
        Long xx_xgjggsTotal = 0L;
        Long xx_cghtTotal = 0L;
        Long xx_cpzljcTotal = 0L;
        Long xx_jzmydzjbgTotal = 0L;
        //幼儿园
        Long yry_bzhyTotal = 0L;
        Long yry_jzgmyjTotal = 0L;
        Long yry_zzmdTotal = 0L;
        Long yry_zzxgmsTotal = 0L;
        Long yry_xgjggsTotal = 0L;
        Long yry_cghtTotal = 0L;
        Long yry_cpzljcTotal = 0L;
        Long yry_jzmydzjbgTotal = 0L;
        //其他
        Long qt_bzhyTotal = 0L;
        Long qt_jzgmyjTotal = 0L;
        Long qt_zzmdTotal = 0L;
        Long qt_zzxgmsTotal = 0L;
        Long qt_xgjggsTotal = 0L;
        Long qt_cghtTotal = 0L;
        Long qt_cpzljcTotal = 0L;
        Long qt_jzmydzjbgTotal = 0L;
        List<String> categoryList = Stream.of("高级中学", "初级中学", "小学", "幼儿园", "其他").collect(Collectors.toList());
        List<XfptProcessCGStatisticsVO> finalDataList = new ArrayList<>();
        for (String curr_category : categoryList) {
            //1、班子会议：本部门及部门以下的数据 以下逻辑相同
            Long bzhyCurr = teamMeetingList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId().toString(), "nil"), "nil").equals(curr_category)).count();
            if (bzhyCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_bzhyTotal += bzhyCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_bzhyTotal += bzhyCurr;
                } else if (curr_category.contains("小学")) {
                    xx_bzhyTotal += bzhyCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_bzhyTotal += bzhyCurr;
                } else if (curr_category.contains("其他")) {
                    qt_bzhyTotal += bzhyCurr;
                }
            }
            //2、查询家长购买意见征集报备
            Long jzgmyjCurr = jzBuyOpinionList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId().toString(), "nil"), "nil").contains(curr_category)).count();
            if (jzgmyjCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_jzgmyjTotal += jzgmyjCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_jzgmyjTotal += jzgmyjCurr;
                } else if (curr_category.contains("小学")) {
                    xx_jzgmyjTotal += jzgmyjCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_jzgmyjTotal += jzgmyjCurr;
                } else if (curr_category.contains("其他")) {
                    qt_jzgmyjTotal += jzgmyjCurr;
                }
            }
            //3、选用组织名单报备学校数
            Long zzmdCurr = orgReportDiscloseList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId().toString(), "nil"), "nil").contains(curr_category)).count();
            if (zzmdCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_zzmdTotal += zzmdCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_zzmdTotal += zzmdCurr;
                } else if (curr_category.contains("小学")) {
                    xx_zzmdTotal += zzmdCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_zzmdTotal += zzmdCurr;
                } else if (curr_category.contains("其他")) {
                    qt_zzmdTotal += zzmdCurr;
                }
            }
            //4、选用组织选购模式报备学校数
            Long zzxgmsCurr = reportModeList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId().toString(), "nil"), "nil").contains(curr_category)).count();
            if (zzxgmsCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_zzxgmsTotal += zzxgmsCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_zzxgmsTotal += zzxgmsCurr;
                } else if (curr_category.contains("小学")) {
                    xx_zzxgmsTotal += zzxgmsCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_zzxgmsTotal += zzxgmsCurr;
                } else if (curr_category.contains("其他")) {
                    qt_zzxgmsTotal += zzxgmsCurr;
                }
            }
            //5、选购结果公示报备学校数
            Long xgjggsCurr = xuangResultList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId().toString(), "nil"), "nil").contains(curr_category)).count();
            if (xgjggsCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_xgjggsTotal += xgjggsCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_xgjggsTotal += xgjggsCurr;
                } else if (curr_category.contains("小学")) {
                    xx_xgjggsTotal += xgjggsCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_xgjggsTotal += xgjggsCurr;
                } else if (curr_category.contains("其他")) {
                    qt_xgjggsTotal += xgjggsCurr;
                }
            }
            //6、采购合同报备
            Long cghtCurr = caiGouHtList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId(), "nil"), "nil").contains(curr_category)).count();
            if (cghtCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_cghtTotal += cghtCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_cghtTotal += cghtCurr;
                } else if (curr_category.contains("小学")) {
                    xx_cghtTotal += cghtCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_cghtTotal += cghtCurr;
                } else if (curr_category.contains("其他")) {
                    qt_cghtTotal += cghtCurr;
                }
            }
            //7、产品质量检测报告报备学校数
            Long cpzljcCurr = chanPinReportList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId(), "nil"), "nil").contains(curr_category)).count();
            if (cpzljcCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_cpzljcTotal += cpzljcCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_cpzljcTotal += cpzljcCurr;
                } else if (curr_category.contains("小学")) {
                    xx_cpzljcTotal += cpzljcCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_cpzljcTotal += cpzljcCurr;
                } else if (curr_category.contains("其他")) {
                    qt_cpzljcTotal += cpzljcCurr;
                }
            }
            //8、家长满意度总结报告报备学校数
            Long jzmydzjbgCurr = jzManYdReportList.stream().filter(e ->
                    schoolTypeDictMap.getOrDefault(deptId_schoolType_map.getOrDefault(e.getDeptId(), "nil"), "nil").contains(curr_category)).count();
            if (jzmydzjbgCurr > 0) {
                if (curr_category.contains("高级中学")) {
                    gj_jzmydzjbgTotal += jzmydzjbgCurr;
                } else if (curr_category.contains("初级中学")) {
                    cj_jzmydzjbgTotal += jzmydzjbgCurr;
                } else if (curr_category.contains("小学")) {
                    xx_jzmydzjbgTotal += jzmydzjbgCurr;
                } else if (curr_category.contains("幼儿园")) {
                    yry_jzmydzjbgTotal += jzmydzjbgCurr;
                } else if (curr_category.contains("其他")) {
                    qt_jzmydzjbgTotal += jzmydzjbgCurr;
                }
            }
            if (curr_category.contains("高级中学")) {
                finalDataList.add(XfptProcessCGStatisticsVO.builder()
                        .bzhyTotal(gj_bzhyTotal)
                        .jzgmyjTotal(gj_jzgmyjTotal)
                        .zzmdTotal(gj_zzmdTotal)
                        .zzxgmsTotal(gj_zzxgmsTotal)
                        .xgjggsTotal(gj_xgjggsTotal)
                        .cghtTotal(gj_cghtTotal)
                        .cpzljcTotal(gj_cpzljcTotal)
                        .jzmydzjbgTotal(gj_jzmydzjbgTotal)
                        .name(curr_category)
                        .build());
            } else if (curr_category.contains("初级中学")) {
                finalDataList.add(XfptProcessCGStatisticsVO.builder()
                        .bzhyTotal(cj_bzhyTotal)
                        .jzgmyjTotal(cj_jzgmyjTotal)
                        .zzmdTotal(cj_zzmdTotal)
                        .zzxgmsTotal(cj_zzxgmsTotal)
                        .xgjggsTotal(cj_xgjggsTotal)
                        .cghtTotal(cj_cghtTotal)
                        .cpzljcTotal(cj_cpzljcTotal)
                        .jzmydzjbgTotal(cj_jzmydzjbgTotal)
                        .name(curr_category)
                        .build());
            } else if (curr_category.contains("小学")) {
                finalDataList.add(XfptProcessCGStatisticsVO.builder()
                        .bzhyTotal(xx_bzhyTotal)
                        .jzgmyjTotal(xx_jzgmyjTotal)
                        .zzmdTotal(xx_zzmdTotal)
                        .zzxgmsTotal(xx_zzxgmsTotal)
                        .xgjggsTotal(xx_xgjggsTotal)
                        .cghtTotal(xx_cghtTotal)
                        .cpzljcTotal(xx_cpzljcTotal)
                        .jzmydzjbgTotal(xx_jzmydzjbgTotal)
                        .name(curr_category)
                        .build());
            } else if (curr_category.contains("幼儿园")) {
                finalDataList.add(XfptProcessCGStatisticsVO.builder()
                        .bzhyTotal(yry_bzhyTotal)
                        .jzgmyjTotal(yry_jzgmyjTotal)
                        .zzmdTotal(yry_zzmdTotal)
                        .zzxgmsTotal(yry_zzxgmsTotal)
                        .xgjggsTotal(yry_xgjggsTotal)
                        .cghtTotal(yry_cghtTotal)
                        .cpzljcTotal(yry_cpzljcTotal)
                        .jzmydzjbgTotal(yry_jzmydzjbgTotal)
                        .name(curr_category)
                        .build());
            } else if (curr_category.contains("其他")) {
                finalDataList.add(XfptProcessCGStatisticsVO.builder()
                        .bzhyTotal(qt_bzhyTotal)
                        .jzgmyjTotal(qt_jzgmyjTotal)
                        .zzmdTotal(qt_zzmdTotal)
                        .zzxgmsTotal(qt_zzxgmsTotal)
                        .xgjggsTotal(qt_xgjggsTotal)
                        .cghtTotal(qt_cghtTotal)
                        .cpzljcTotal(qt_cpzljcTotal)
                        .jzmydzjbgTotal(qt_jzmydzjbgTotal)
                        .name(curr_category)
                        .build());
            }
        }
        //填充总数据
        XfptProcessCGStatisticsVO totalSummaryVo = XfptProcessCGStatisticsVO.builder()
                .bzhyTotal(gj_bzhyTotal + cj_bzhyTotal + xx_bzhyTotal + yry_bzhyTotal + qt_bzhyTotal)
                .jzgmyjTotal(gj_jzgmyjTotal + cj_jzgmyjTotal + xx_jzgmyjTotal + yry_jzgmyjTotal + qt_jzgmyjTotal)
                .zzmdTotal(gj_zzmdTotal + cj_zzmdTotal + xx_zzmdTotal + yry_zzmdTotal + qt_zzmdTotal)
                .zzxgmsTotal(gj_zzxgmsTotal + cj_zzxgmsTotal + xx_zzxgmsTotal + yry_zzxgmsTotal + qt_zzxgmsTotal)
                .xgjggsTotal(gj_xgjggsTotal + cj_xgjggsTotal + xx_xgjggsTotal + yry_xgjggsTotal + qt_xgjggsTotal)
                .cghtTotal(gj_cghtTotal + cj_cghtTotal + xx_cghtTotal + yry_cghtTotal + qt_cghtTotal)
                .cpzljcTotal(gj_cpzljcTotal + cj_cpzljcTotal + xx_cpzljcTotal + yry_cpzljcTotal + qt_cpzljcTotal)
                .jzmydzjbgTotal(gj_jzmydzjbgTotal + cj_jzmydzjbgTotal + xx_jzmydzjbgTotal + yry_jzmydzjbgTotal + qt_jzmydzjbgTotal)
                .name("总数")
                .build();
        finalDataList.add(0, totalSummaryVo);
        return finalDataList;
    }


    /**
     * 获取区县下对应学校的报备情况
     * parentDeptId  查询哪个部门下子部门的数据，就传递对应部门id
     * @param entity                    主要获取查询的开始时间和结束时间
     * @param schoolStatisticsCondition 处理动态参数
     * @return
     */
    @GetMapping("/getSchoolBaoBeiSituation")
    public TableDataInfo getSchoolBaoBeiSituation(BaseEntity entity, XfptSchoolStatisticsVO schoolStatisticsCondition) {
        Long parentDeptId = schoolStatisticsCondition.getParentDeptId();
        if (parentDeptId == null) {
            throw new RuntimeException("部门不能为空！");
        }
        List<XfptSchoolStatisticsVO> finalDataList = getSchoolBaoBeiSituationDataList(parentDeptId, entity, schoolStatisticsCondition,true);
        return getDataTable(finalDataList);
    }

    /**
     * 区县下对应学校的报备情况导出
     *
     * @param response
     * @param entity
     * @param schoolStatisticsCondition
     */
    @Log(title = "区县下对应学校的报备情况导出", businessType = BusinessType.EXPORT)
    @PostMapping("/exportSchoolBaoBeiSituationStatistics")
    public void exportSchoolBaoBeiSituationStatistics(HttpServletResponse response,BaseEntity entity, XfptSchoolStatisticsVO schoolStatisticsCondition) {
        List<XfptSchoolStatisticsVO> list = getSchoolBaoBeiSituationDataList(schoolStatisticsCondition.getParentDeptId(), entity, schoolStatisticsCondition,false);
        ExcelUtil<XfptSchoolStatisticsVO> util = new ExcelUtil<>(XfptSchoolStatisticsVO.class);
        util.exportExcel(response, list, "学校报备情况统计");
    }

    public List<XfptSchoolStatisticsVO> getSchoolBaoBeiSituationDataList(Long parentDeptId, BaseEntity entity, XfptSchoolStatisticsVO schoolStatisticsCondition,boolean needPage) {
        List<SysDept> deptList = getSonDeptList(parentDeptId);
        List<Long> sonDeptIdList = deptList.stream().map(e -> e.getDeptId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sonDeptIdList)) {
            //如果没有子部门数据，则手动设置一个永远查询不到数据的值
            sonDeptIdList.add(-100L);
        }
        TeamMeeting teamMeetingCondition = new TeamMeeting();
        teamMeetingCondition.setParams(entity.getParams());
        //查询符合条件的班子会议数据
        List<TeamMeeting> teamMeetingList = teamMeetingService.selectTeamMeetingList(teamMeetingCondition);
        //只保留归属于当前参数部门的数据
        teamMeetingList.removeIf(e -> !e.getAncestors().contains(parentDeptId.toString()));
        //获取所有班子会议id集合
        List<Long> teamMeetingIdList = teamMeetingList.stream().map(TeamMeeting::getId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teamMeetingIdList)) {
            //没有班子会议则设置一个不存在的值
            teamMeetingIdList.add(-1L);
        }
        if (needPage) {
            startPage();
        }
        schoolStatisticsCondition.setParentDeptId(parentDeptId);
        schoolStatisticsCondition.setTeamMeetingIds(teamMeetingIdList);
        List<XfptSchoolStatisticsVO> dataList = teamMeetingService.listSchoolStatistics(schoolStatisticsCondition);
        return dataList;
    }



    @GetMapping("/getProcessMenu")
    public AjaxResult getProcessMenu(Long teamId){
        //根据id查询当前
        TeamMeeting teamMeeting = teamMeetingService.getById(teamId);
        List<Integer> result=new ArrayList<>();
        if(teamMeeting==null){
            list(teamId, teamMeeting, result);
        }else {
            list1(teamId, teamMeeting, result);
        }


        return AjaxResult.success(result);
    }

    private void list1(Long teamId, TeamMeeting teamMeeting, List<Integer> result) {
        String timeTb = teamMeeting.getTimeTb();

        result.add(timeTb.contains(ProceConstants.BZHY) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.CGGGFB) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.BXGGZS) || timeTb.contains(ProceConstants.RWZS) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.JZTP) || timeTb.contains(ProceConstants.PDXZ) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.TPJGGS) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.HTGK) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.CGJGGG) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.ZJBGGK) ? 1 : 0);
        result.add(timeTb.contains(ProceConstants.MDYBB) ? 1 : 0);
        Long count = iXfptProcSummaryService.lambdaQuery().eq(XfptProcSummary::getTeamId, teamId).count();
        result.add(count == 0 ? 0 : 1);
        result.add(timeTb.contains(ProceConstants.CGGZZJ) ? 1 : 0);
    }
    private void list(Long teamId, TeamMeeting teamMeeting, List<Integer> result) {
        for (int i = 0; i < 11; i++) {
            result.add(0);
        }
    }
    @PostMapping("/importUserName")
    public AjaxResult importUserName(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("导入失败：请选择 xls/xlsx 文件");
        }
        List<UserName> userList;
        try {
            userList = parseUserNameList(file);
        } catch (Exception e) {
            String message = StrUtil.blankToDefault(e.getMessage(), "导入失败：请上传有效的 xls/xlsx 文件");
            return AjaxResult.error(message);
        }
        if (CollUtil.isEmpty(userList)) {
            return AjaxResult.error("导入失败：未识别到人员名单，请使用模板或在第一列填写姓名、第二列填写电话");
        }
        String result = userList.stream()
                .map(this::buildUserNameText)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(","));
        if (StrUtil.isBlank(result)) {
            return AjaxResult.error("导入失败：名单内容为空，请检查 Excel");
        }
        return AjaxResult.success(result);
    }

    private List<UserName> parseUserNameList(MultipartFile file) throws IOException {
        ExcelUtil<UserName> util = new ExcelUtil<UserName>(UserName.class);
        List<UserName> userList = normalizeUserNames(util.importExcel(file.getInputStream()));
        if (CollUtil.isNotEmpty(userList)) {
            return userList;
        }
        return parseUserNamesWithoutHeader(file);
    }

    private List<UserName> normalizeUserNames(List<UserName> userList) {
        if (CollUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return userList.stream()
                .filter(Objects::nonNull)
                .peek(userName -> {
                    userName.setName(StrUtil.trim(userName.getName()));
                    userName.setPhone(StrUtil.trim(userName.getPhone()));
                })
                .filter(userName -> StrUtil.isNotBlank(userName.getName()) || StrUtil.isNotBlank(userName.getPhone()))
                .collect(Collectors.toList());
    }

    private List<UserName> parseUserNamesWithoutHeader(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (workbook.getNumberOfSheets() == 0) {
                return Collections.emptyList();
            }
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            List<UserName> userList = new ArrayList<>();
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (isUserNameRowEmpty(row, formatter)) {
                    continue;
                }
                String name = getCellText(row.getCell(0), formatter);
                String phone = getCellText(row.getCell(1), formatter);
                if (i == 0 && isUserNameHeaderRow(name, phone)) {
                    continue;
                }
                UserName userName = new UserName();
                userName.setName(StrUtil.trim(name));
                userName.setPhone(StrUtil.trim(phone));
                if (StrUtil.isBlank(userName.getName()) && StrUtil.isBlank(userName.getPhone())) {
                    continue;
                }
                userList.add(userName);
            }
            return userList;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("导入失败：请上传有效的 xls/xlsx 文件", e);
        }
    }

    private boolean isUserNameRowEmpty(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        return StrUtil.isBlank(getCellText(row.getCell(0), formatter))
                && StrUtil.isBlank(getCellText(row.getCell(1), formatter));
    }

    private boolean isUserNameHeaderRow(String firstCell, String secondCell) {
        String first = normalizeHeaderCell(firstCell);
        String second = normalizeHeaderCell(secondCell);
        return containsHeaderKeyword(first, "学生姓名", "姓名", "名字", "家长姓名")
                && (StrUtil.isBlank(second) || containsHeaderKeyword(second, "电话", "手机号", "手机号码", "联系电话"));
    }

    private String normalizeHeaderCell(String value) {
        if (value == null) {
            return StrUtil.EMPTY;
        }
        return value.replace(" ", "").trim();
    }

    private boolean containsHeaderKeyword(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String getCellText(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return StrUtil.EMPTY;
        }
        return formatter.formatCellValue(cell);
    }

    private String buildUserNameText(UserName userName) {
        StringBuilder builder = new StringBuilder();
        if (StrUtil.isNotBlank(userName.getName())) {
            builder.append(userName.getName());
        }
        if (StrUtil.isNotBlank(userName.getPhone())) {
            if (builder.length() > 0) {
                builder.append("-");
            }
            builder.append(userName.getPhone());
        }
        return builder.toString();
    }

    @GetMapping("/getStatus")
    public AjaxResult getStatus(Long teamId){
        if(teamId==null){
            return AjaxResult.success(null);
        }
        List<XfptReportMode> list = iXfptReportModeService.lambdaQuery().eq(XfptReportMode::getTeamId, teamId).orderByDesc(XfptReportMode::getCreateTime).list();
        if(list==null || list.size()==0){
            return AjaxResult.success(null);
        }

        return AjaxResult.success(list.get(0).getPurchaseMode());
    }
}
