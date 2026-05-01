package com.xfgl.school.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptAnnoResults;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;
import com.xfgl.school.domain.vo.XfptJYNoticeSchoolStatisticsVO;
import com.xfgl.school.domain.vo.XfptJYNoticeStatisticsVO;
import com.xfgl.school.mapper.XfptProcSummaryMapper;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.domain.SysDept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 只查询公告和学校，没有厂商的数据统计
 */
@Slf4j
@RestController
@RequestMapping("/edu/manager/statistics")
public class SchoolStatisticsController extends BaseController {

    @Autowired
    private IXfptSchoolStatisticsService schoolStatisticsService;
    @Autowired
    private RemoteDeptService remoteDeptService;
    @Autowired
    private IXfptAnnoResultsService annoResultsService;
    @Autowired
    private IXfptSuppliceInspectionReportService suppliceInspectionReportService;
    @Autowired
    private IXfptSuppliceInspectionReportSonService suppliceInspectionReportSonService;
    @Autowired
    private XfptProcSummaryMapper xfptProcSummaryMapper;

    /**
     * 获取指定部门下的所属子部门集合
     *
     * @param parentDeptId 查询哪个部门下的子部门数据，就传递对应的部门id
     * @return
     */
    public List<SysDept> getDeptList(Long parentDeptId) {
        return xfptProcSummaryMapper.selectDeptList(parentDeptId);
    }


    /**
     * 获取子部门
     * @return
     */
    @GetMapping("/getDeptListByParentDeptId")
    public AjaxResult getDeptListByParentDeptId() {
        //获取市教育局部门
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        List<SysDept> deptList = xfptProcSummaryMapper.selectDeptBytypeList();
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        if (!admin) {
            if (deptId != null && deptId != 100L) {
                //其他部门只查看自己部门列表
                deptList.removeIf(e -> !e.getDeptId().equals(deptId));
            }
        }
        deptList = deptList.stream().sorted(Comparator.comparing(SysDept::getOrderNum)).collect(Collectors.toList());
        return success(deptList);
    }



    /**
     * 获取子部门
     * @param parentDeptId
     * @return
     */
    @GetMapping("/getDeptListByParentDeptId2")
    public AjaxResult getDeptListByParentDeptId2(@RequestParam(value = "parentDeptId",required = false) Long parentDeptId) {
        if (parentDeptId == null) {
            //默认教育局顶层部门id
            parentDeptId = 100L;
        }
        List<SysDept> deptList = getDeptList(parentDeptId);
        return success(deptList);
    }

    /**
     * 校服管理-质量与监管：公告
     */
    @GetMapping("/getNoticeStatisticsDataList")
    public TableDataInfo getNoticeStatisticsDataList(XfptJYNoticeStatisticsVO jYNoticeStatisticsVOCondition) {

        List<XfptJYNoticeStatisticsVO> finalDataList = new ArrayList<>();
        //这里查询的是指定部门下的子部门
        Long parentDeptId = jYNoticeStatisticsVOCondition.getParentDeptId();
        if (parentDeptId == null) {
            throw new RuntimeException("部门不能为空！");
        }
        final Long parentDeptIdFinal = parentDeptId;
        //查询子部门数据
        List<SysDept> sonDeptList = getDeptList(parentDeptId);
        List<String> sonDeptIdList = sonDeptList.stream().map(e -> e.getDeptId().toString()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sonDeptIdList)) {
            //说明这里是叶子节点部门登录，则只保留自己的数据
            sonDeptIdList.add(parentDeptId.toString());
        }
        //查询符合条件的交易公告
        XfptAnnoResults annoResultEntity = XfptAnnoResults.builder()
                .noticeTitle(jYNoticeStatisticsVOCondition.getGgName())
                .deptId(parentDeptId)
                .transactionCompanyName(jYNoticeStatisticsVOCondition.getCsName())
                .build();
        annoResultEntity.setParams(jYNoticeStatisticsVOCondition.getParams());
        List<XfptAnnoResults> xfptAnnoResultsList = annoResultsService.selectXfptAnnoResultsList(annoResultEntity);
        //删除不是本部门且不是本部门下的子部门的数据
        xfptAnnoResultsList.removeIf(e -> !sonDeptIdList.contains(e.getDeptId().toString())
                && !e.getDeptId().equals(parentDeptIdFinal)
                && !e.getAncestors().contains(parentDeptIdFinal.toString()));
        if (CollectionUtils.isEmpty(xfptAnnoResultsList)) {
            return getDataTable(finalDataList);
        }
        //获取符合查询条件的班子会议id集合
        List<Long> teamMeetingIdList = xfptAnnoResultsList.stream().map(XfptAnnoResults::getTeamId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teamMeetingIdList)) {
            log.info("统计公告获取班子会议集合为空！");
            return getDataTable(finalDataList);
        }
        //查询质检的主键id用于查询子表数据，来获取首次质检和二次质检的依据
        List<XfptSuppliceInspectionReport> suppliceInspectionReportDataList = suppliceInspectionReportService.list(new LambdaQueryWrapper<XfptSuppliceInspectionReport>()
                .in(XfptSuppliceInspectionReport::getTeamMeetingId, teamMeetingIdList));
        //删除部门id不在子部门集合中，并且不是当前父级部门的子孙节点
        suppliceInspectionReportDataList.removeIf(e -> !sonDeptIdList.contains(e.getDeptId())
                && !e.getAncestors().contains(parentDeptIdFinal.toString())
                && (StringUtils.isNotBlank(e.getAncestors()) && !e.getAncestors().contains(parentDeptIdFinal.toString())));
        //获取检查主键id
        List<Long> inspectIdList = suppliceInspectionReportDataList.stream().map(e -> e.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(inspectIdList)) {
            //传递一个查询不到数据的值
            inspectIdList.add(-1L);
        }
        //查询统计的数据
        jYNoticeStatisticsVOCondition.setTeamMeetingIdList(teamMeetingIdList);
        startPage();
        finalDataList = schoolStatisticsService.getJYNoticeStatisticsList(jYNoticeStatisticsVOCondition);
        return getDataTable(finalDataList);
    }

    /**
     * 获取指定班子会议对应公告所属质检的附件
     *
     * @param teamMeetingId
     * @return
     */
    @GetMapping("/getNoticeInspectSonFiles")
    public AjaxResult getNoticeInspectSonFile(Long teamMeetingId) {
        if (teamMeetingId == null) {
            return error("会议id不能为空！");
        }
        Map<String, Object> finalDataMap = new LinkedHashMap<>();
        //这里只获取查询到数据列表的第一条记录，默认公告和班子会议当作一对一的关系处理
        LambdaQueryWrapper<XfptSuppliceInspectionReport> inspectionReportWrapper = new LambdaQueryWrapper<XfptSuppliceInspectionReport>().eq(XfptSuppliceInspectionReport::getTeamMeetingId, teamMeetingId.toString());
        List<XfptSuppliceInspectionReport> inspectionReportDataList = suppliceInspectionReportService.list(inspectionReportWrapper);
        if (CollectionUtils.isEmpty(inspectionReportDataList)) {
            return success(finalDataMap);
        }
        List<Long> inspectionReportIdList = inspectionReportDataList.stream().map(XfptSuppliceInspectionReport::getId).collect(Collectors.toList());
        LambdaQueryWrapper<XfptSuppliceInspectionReportSon> suppliceInspectionReportSonWrapper = new LambdaQueryWrapper<XfptSuppliceInspectionReportSon>().in(XfptSuppliceInspectionReportSon::getReportId, inspectionReportIdList);
        List<XfptSuppliceInspectionReportSon> suppliceInspectionReportSonList = suppliceInspectionReportSonService.list(suppliceInspectionReportSonWrapper);
        suppliceInspectionReportSonList.removeIf(e -> StringUtils.isBlank(e.getType()) && StringUtils.isBlank(e.getUrl()));
        if (CollectionUtils.isEmpty(suppliceInspectionReportSonList)) {
            return success(finalDataMap);
        }
        Map<String, List<XfptSuppliceInspectionReportSon>> typeDataMap = suppliceInspectionReportSonList.stream().collect(Collectors.groupingBy(XfptSuppliceInspectionReportSon::getType));
        //获取首次的质检报告附件
        List<XfptSuppliceInspectionReportSon> oneDataList = typeDataMap.get("1");
        if (!CollectionUtils.isEmpty(oneDataList)) {
            String urlStr = oneDataList.get(0).getUrl();
            List<String> oneFileUrlList = Arrays.stream(urlStr.split("&&&")).collect(Collectors.toList());
            finalDataMap.put("one", oneFileUrlList);
        }
        //获取二次的质检报告附件
        List<XfptSuppliceInspectionReportSon> twoDataList = typeDataMap.get("2");
        if (!CollectionUtils.isEmpty(twoDataList)) {
            String urlStr = twoDataList.get(0).getUrl();
            List<String> twoDataListFileUrlList = Arrays.stream(urlStr.split("&&&")).collect(Collectors.toList());
            finalDataMap.put("two", twoDataListFileUrlList);
        }
        return success(finalDataMap);
    }


    /**
     * 获取校服交易管理-质量与监管-学校统计
     * 必须参数
     *
     * @param jYNoticeSchoolStatisticsVO
     * @return
     */
    @GetMapping("/getJYNoticeSchoolStatisticsList")
    public TableDataInfo getJYNoticeSchoolStatisticsList(XfptJYNoticeSchoolStatisticsVO jYNoticeSchoolStatisticsVO) {

        if (jYNoticeSchoolStatisticsVO.getConditionFirstOne() != null && jYNoticeSchoolStatisticsVO.getConditionFirstOne() - 1 > 0) {
            jYNoticeSchoolStatisticsVO.setConditionFirstOne(jYNoticeSchoolStatisticsVO.getConditionFirstOne() / 100);
        }
        if (jYNoticeSchoolStatisticsVO.getConditionFirstTwo() != null && jYNoticeSchoolStatisticsVO.getConditionFirstTwo() - 1 > 0) {
            jYNoticeSchoolStatisticsVO.setConditionFirstTwo(jYNoticeSchoolStatisticsVO.getConditionFirstTwo() / 100);
        }
        if (jYNoticeSchoolStatisticsVO.getConditionSecondOne() != null && jYNoticeSchoolStatisticsVO.getConditionSecondOne() - 1 > 0) {
            jYNoticeSchoolStatisticsVO.setConditionSecondOne(jYNoticeSchoolStatisticsVO.getConditionSecondOne() / 100);
        }
        if (jYNoticeSchoolStatisticsVO.getConditionSecondTwo() != null && jYNoticeSchoolStatisticsVO.getConditionSecondTwo() - 1 > 0) {
            jYNoticeSchoolStatisticsVO.setConditionSecondTwo(jYNoticeSchoolStatisticsVO.getConditionSecondTwo() / 100);
        }
        List<XfptJYNoticeSchoolStatisticsVO> finalDataList = new ArrayList<>();
        //这里查询的是指定部门下的子部门
        /*boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (admin) {
            //管理员默认顶部部门id
            parentDeptId = 100L;
        }*/
        Long parentDeptId = jYNoticeSchoolStatisticsVO.getParentDeptId();
        if (parentDeptId == null) {
            throw new RuntimeException("部门不能为空！");
        }
        final Long parentDeptIdFinal = parentDeptId;
        //查询子部门数据
        List<SysDept> sonDeptList = getDeptList(parentDeptId);
        //市以下直辖的
        List<String> sonDeptIdList = sonDeptList.stream().map(e -> e.getDeptId().toString()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sonDeptIdList)) {
            //如果没有子部门，可能是最后一层叶子节点部门，所以直接添加自己的部门
            sonDeptIdList.add(parentDeptId.toString());
        }
        //查询符合条件的交易公告
        XfptAnnoResults annoResultEntity = XfptAnnoResults.builder()
                .deptId(parentDeptId)
                .build();
        annoResultEntity.setParams(jYNoticeSchoolStatisticsVO.getParams());
        List<XfptAnnoResults> xfptAnnoResultsList = annoResultsService.selectXfptAnnoResultsList(annoResultEntity);
        //删除不是本部门且不是本部门下的子部门的数据
        xfptAnnoResultsList.removeIf(e -> !sonDeptIdList.contains(e.getDeptId().toString()) && !e.getDeptId().equals(parentDeptIdFinal)
                && !e.getAncestors().contains(parentDeptIdFinal.toString()));
        if (CollectionUtils.isEmpty(xfptAnnoResultsList)) {
            return getDataTable(finalDataList);
        }
        //获取符合查询条件的班子会议id集合
        List<Long> teamMeetingIdList = xfptAnnoResultsList.stream().map(XfptAnnoResults::getTeamId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teamMeetingIdList)) {
            log.info("统计公告获取班子会议集合为空！");
            return getDataTable(finalDataList);
        }
        //查询质检数据
        List<XfptSuppliceInspectionReport> suppliceInspectionReportDataList = suppliceInspectionReportService.list(new LambdaQueryWrapper<XfptSuppliceInspectionReport>()
                .in(XfptSuppliceInspectionReport::getTeamMeetingId, teamMeetingIdList));
        //删除部门id不在子部门集合中，并且不是当前父级部门的子孙节点
        suppliceInspectionReportDataList.removeIf(e -> !sonDeptIdList.contains(e.getDeptId())
                && !e.getAncestors().contains(parentDeptIdFinal.toString())
                && (StringUtils.isNotBlank(e.getAncestors()) && !e.getAncestors().contains(parentDeptIdFinal.toString())));
        //获取检查主键id
        List<Long> inspectIdList = suppliceInspectionReportDataList.stream().map(e -> e.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(inspectIdList)) {
            //传递一个查询不到数据的值
            inspectIdList.add(-1L);
        }
        jYNoticeSchoolStatisticsVO.setTeamMeetingIdList(teamMeetingIdList);
        jYNoticeSchoolStatisticsVO.setInspectIdList(inspectIdList);
        startPage();
        finalDataList = schoolStatisticsService.getJYNoticeSchoolStatisticsList(jYNoticeSchoolStatisticsVO);
        return getDataTable(finalDataList);
    }

}
