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
import com.xfgl.system.api.domain.SysDept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 教育局统计
 */
@Slf4j
@RestController
@RequestMapping("/index/statistics")
public class JyjAndSchoolStatisticsController {

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
     * 教育局首页头部统计
     * @return
     */
    @GetMapping("/jyj/topList")
    public AjaxResult jyjTopList(){
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        // 查询所有班级数据并求和
        List<XfptSchoolGradeClass> classList = xfptSchoolMessageMapper.classListByDept(dept.getDeptId());
        //学生总人数
        Long studentTotal = classList.stream()
                .map(XfptSchoolGradeClass::getNumber)  // 获取字符串类型的数字
                .filter(num -> num != null && !num.isEmpty())  // 避免空值转换错误
                .mapToLong(Long::parseLong)  // 转换为int类型
                .sum();  // 求和
        //供应商数量
        Long supplierTotal = supplierMessageService.lambdaQuery().count();

        //学校数量
        Long schoolTotal =xfptSchoolMessageMapper.getCount(dept.getDeptId());
        //成交金额
        List<XfptProcSummary> list = iXfptProcSummaryService.lambdaQuery().apply(StrFormatter.format("find_in_set({},ancestors)", dept.getDeptId())).list();
        Long moneyTotal = list.stream().map(XfptProcSummary::getMoney).mapToLong(Long::longValue).sum();

        HashMap<String,Long> total = new HashMap<String,Long>();
        total.put("studentTotal",studentTotal);
        total.put("supplierTotal",supplierTotal);
        total.put("schoolTotal",schoolTotal);
        total.put("moneyTotal",moneyTotal);
        return AjaxResult.success(total);
    }

    /**
     * 学校首页头部统计
     * 学生人数	订购金额	采购公告数量	订购套数
     * @return
     */
    @GetMapping("/school/topList")
    public AjaxResult schoolTopList(){
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        // 查询所有班级数据并求和
        List<XfptSchoolGradeClass> classList = xfptSchoolMessageMapper.classListByDept(dept.getDeptId());
        //学生总人数
        Long studentTotal = classList.stream()
                .map(XfptSchoolGradeClass::getNumber)  // 获取字符串类型的数字
                .filter(num -> num != null && !num.isEmpty())  // 避免空值转换错误
                .mapToLong(Long::parseLong)  // 转换为int类型
                .sum();  // 求和

        //成交金额
        List<XfptProcSummary> list = iXfptProcSummaryService.lambdaQuery().apply(StrFormatter.format("find_in_set({},ancestors)", dept.getDeptId())).list();
        Long moneyTotal = list.stream().map(XfptProcSummary::getMoney).mapToLong(Long::longValue).sum();
        //订购件数
        Long countTotal = list.stream().map(XfptProcSummary::getCount).mapToLong(Long::longValue).sum();
        //采购公告数量
        Long teamCount = iTeamMeetingService.lambdaQuery().eq(TeamMeeting::getDeptId, dept.getDeptId()).count();
        HashMap<String,Long> total = new HashMap<String,Long>();
        total.put("studentTotal",studentTotal);
        total.put("moneyTotal",moneyTotal);
        total.put("countTotal",countTotal);
        total.put("teamCount",teamCount);
        return AjaxResult.success(total);
    }
}
