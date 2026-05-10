package com.xfgl.school.controller;

import cn.hutool.core.text.StrFormatter;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.TeamMeeting;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.XfptSchoolGradeClass;
import com.xfgl.school.mapper.XfptSchoolMessageMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.ITeamMeetingService;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.school.service.IXfptSchoolGradeClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 教育局/学校首页统计
 */
@Slf4j
@RestController
@RequestMapping("/index/statistics")
public class JyjAndSchoolStatisticsController {

    private static final Long DEFAULT_TEST_DEPT_ID = 100L;

    @Autowired
    private IXfptSchoolGradeClassService iXfptSchoolGradeClassService;
    @Autowired
    private ISupplierMessageService supplierMessageService;
    @Autowired
    private XfptSchoolMessageMapper xfptSchoolMessageMapper;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;

    /**
     * 教育局首页顶部统计
     */
    @GetMapping("/jyj/topList")
    public AjaxResult jyjTopList(@RequestParam(value = "deptId", required = false) Long deptId) {
        Long currentDeptId = resolveDeptId(deptId);
        List<XfptSchoolGradeClass> classList = xfptSchoolMessageMapper.classListByDept(currentDeptId);
        if (classList == null) {
            classList = Collections.emptyList();
        }
        Long studentTotal = classList.stream()
                .map(XfptSchoolGradeClass::getNumber)
                .filter(num -> num != null && !num.isEmpty())
                .mapToLong(Long::parseLong)
                .sum();
        Long supplierTotal = supplierMessageService.lambdaQuery().count();
        Long schoolTotal = xfptSchoolMessageMapper.getCount(currentDeptId);

        List<XfptProcSummary> list = iXfptProcSummaryService.lambdaQuery()
                .apply(StrFormatter.format("find_in_set({},ancestors)", currentDeptId))
                .list();
        if (list == null) {
            list = Collections.emptyList();
        }
        Long moneyTotal = list.stream()
                .map(XfptProcSummary::getMoney)
                .filter(money -> money != null)
                .mapToLong(Long::longValue)
                .sum();

        HashMap<String, Long> total = new HashMap<String, Long>();
        total.put("studentTotal", studentTotal);
        total.put("supplierTotal", supplierTotal);
        total.put("schoolTotal", schoolTotal);
        total.put("moneyTotal", moneyTotal);
        return AjaxResult.success(total);
    }

    /**
     * 学校首页顶部统计
     */
    @GetMapping("/school/topList")
    public AjaxResult schoolTopList(@RequestParam(value = "deptId", required = false) Long deptId) {
        Long currentDeptId = resolveDeptId(deptId);
        List<XfptSchoolGradeClass> classList = xfptSchoolMessageMapper.classListByDept(currentDeptId);
        if (classList == null) {
            classList = Collections.emptyList();
        }
        Long studentTotal = classList.stream()
                .map(XfptSchoolGradeClass::getNumber)
                .filter(num -> num != null && !num.isEmpty())
                .mapToLong(Long::parseLong)
                .sum();

        List<XfptProcSummary> list = iXfptProcSummaryService.lambdaQuery()
                .apply(StrFormatter.format("find_in_set({},ancestors)", currentDeptId))
                .list();
        if (list == null) {
            list = Collections.emptyList();
        }
        Long moneyTotal = list.stream()
                .map(XfptProcSummary::getMoney)
                .filter(money -> money != null)
                .mapToLong(Long::longValue)
                .sum();
        Long countTotal = list.stream()
                .map(XfptProcSummary::getCount)
                .filter(count -> count != null)
                .mapToLong(Long::longValue)
                .sum();
        Long teamCount = iTeamMeetingService.lambdaQuery()
                .eq(TeamMeeting::getDeptId, currentDeptId)
                .count();

        HashMap<String, Long> total = new HashMap<String, Long>();
        total.put("studentTotal", studentTotal);
        total.put("moneyTotal", moneyTotal);
        total.put("countTotal", countTotal);
        total.put("teamCount", teamCount);
        return AjaxResult.success(total);
    }

    private Long resolveDeptId(Long requestDeptId) {
        if (requestDeptId != null) {
            return requestDeptId;
        }
        if (SecurityUtils.getLoginUser() != null
                && SecurityUtils.getLoginUser().getSysUser() != null
                && SecurityUtils.getLoginUser().getSysUser().getDept() != null
                && SecurityUtils.getLoginUser().getSysUser().getDept().getDeptId() != null) {
            return SecurityUtils.getLoginUser().getSysUser().getDept().getDeptId();
        }
        log.warn("statistics endpoint called without login user, fallback deptId={}", DEFAULT_TEST_DEPT_ID);
        return DEFAULT_TEST_DEPT_ID;
    }
}
