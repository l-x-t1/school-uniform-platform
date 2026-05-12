package com.xfgl.school.controller;

import cn.hutool.core.date.DateUtil;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.utils.DateUtils;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptParentsFeedback;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.vo.KeyValueVo;
import com.xfgl.school.mapper.IndexStatisticsMapper;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.system.api.domain.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/index/statistics")
public class IndexStatisticsController {
    private static final Long DEFAULT_TEST_DEPT_ID = 100L;

    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private IndexStatisticsMapper indexStatisticsMapper;
    @Autowired
    private IXfptSchoolMessageService supplierMessageService;


    @GetMapping("/peopleCount")
    public AjaxResult peopleCount() {
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        //Long deptId =200l;


        //获取当前年月日
        String format = DateUtil.format(new Date(), "yyyy");
        //获取上一年年份
        int lastYear = LocalDate.now().minusYears(1).getYear();
        //查询今年订购人数数据
        Long thisOrderYearCount = indexStatisticsMapper.orderCount(format, deptId);
        //去年订购人数数据
        Long lastOrderYearCount = indexStatisticsMapper.orderCount(String.valueOf(lastYear), deptId);

        //查询今年金额
        Long thisMoneyYearCount = indexStatisticsMapper.orderMoneyCount(format, deptId);
        //查询去年金额
        Long lastMoneyYearCount = indexStatisticsMapper.orderMoneyCount(String.valueOf(lastYear), deptId);
        HashMap<String, Long> result = new HashMap<>();
        result.put("thisOrderYearCount", thisOrderYearCount);
        result.put("lastOrderYearCount", lastOrderYearCount);
        result.put("thisMoneyYearCount", thisMoneyYearCount);
        result.put("lastMoneyYearCount", lastMoneyYearCount);

        return AjaxResult.success(result);
    }

    /**
     * 查询区县成交金额
     *
     * @return
     */
    @GetMapping("/getMoney")
    public AjaxResult getMoney() {

        //判断角色是否是教育局
        //查询角色，提取角色的key
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        List<KeyValueVo> map = new ArrayList<>();
        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            map = indexStatisticsMapper.getQxMoney();
        } else {
            map = indexStatisticsMapper.getSchoolMoney();
        }
        //map取前10个
        map = map.stream().limit(10).collect(Collectors.toList());
        return AjaxResult.success(map);
    }

    /**
     * 查询区县成交金额
     *
     * @return
     */
    @GetMapping("/getCount")
    public AjaxResult getCount() {

        //判断角色是否是教育局
        //查询角色，提取角色的key
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        List<KeyValueVo> map = new ArrayList<>();
        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            map = indexStatisticsMapper.getQxCount();
        } else {
            map = indexStatisticsMapper.getSchoolCount();
        }
        map = map.stream().limit(10).collect(Collectors.toList());
        return AjaxResult.success(map);
    }

    /**
     * 学校查询近5年的数据
     */
    @GetMapping("/getSchool5YearCountData")
    public AjaxResult getSchool5YearData(@RequestParam(value = "deptId", required = false) Long deptId) {
        Long currentDeptId = resolveDeptId(deptId);
        List<KeyValueVo> map = new ArrayList<>();
        map=indexStatisticsMapper.getSchool5YearCountData(currentDeptId);
        return AjaxResult.success(map);
    }

    /**
     * 学校查询近5年的数据
     */
    @GetMapping("/getSchool5YearMoneyData")
    public AjaxResult getSchool5YearMoneyData(@RequestParam(value = "deptId", required = false) Long deptId) {
        Long currentDeptId = resolveDeptId(deptId);
        List<KeyValueVo> map = new ArrayList<>();
        map=indexStatisticsMapper.getSchool5YearMoneyData(currentDeptId);
        return AjaxResult.success(map);
    }

    @GetMapping("/schoolUniformType")
    public AjaxResult schoolUniformType(){
        //判断角色是否是教育局
        //查询角色，提取角色的key
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        List<KeyValueVo> map = new ArrayList<>();
        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            map = indexStatisticsMapper.schoolUniformType(null);
        } else if(collect.contains(RoleType.JYJ.getCode()) || collect.contains(RoleType.XX.getCode())){
            Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
            map = indexStatisticsMapper.schoolUniformType(deptId);
        }else {
            return AjaxResult.warn("未查询到角色信息,联系管理员");
        }
        return AjaxResult.success(map);
    }

    /**
     * 平台使用情况
     */
    @GetMapping("/platformUsage")
    public AjaxResult platformUsage(@RequestParam(value = "deptId", required = false) Long deptId) {
        Long currentDeptId = resolveDeptId(deptId);
        HashMap<String, Long> total = new HashMap<String, Long>();
        total.put("districtPlatformTotal", indexStatisticsMapper.getDistrictPlatformUserCount(currentDeptId));
        total.put("schoolPlatformTotal", indexStatisticsMapper.getSchoolPlatformCount(currentDeptId));
        total.put("studentPlatformTotal", indexStatisticsMapper.getStudentPlatformCount(currentDeptId));
        return AjaxResult.success(total);
    }

    /**
     * 价格趋势分析
     */
    @GetMapping("/priceTrendData")
    public AjaxResult priceTrendData(@RequestParam(value = "deptId", required = false) Long deptId) {
        Long currentDeptId = resolveDeptId(deptId);
        List<KeyValueVo> map = indexStatisticsMapper.getPriceTrendData(currentDeptId);
        return AjaxResult.success(map);
    }

    private Long resolveDeptId(Long requestDeptId) {
        if (requestDeptId != null) {
            return requestDeptId;
        }
        if (SecurityUtils.getLoginUser() != null
                && SecurityUtils.getLoginUser().getSysUser() != null
                && SecurityUtils.getLoginUser().getSysUser().getDeptId() != null) {
            return SecurityUtils.getLoginUser().getSysUser().getDeptId();
        }
        return DEFAULT_TEST_DEPT_ID;
    }
}
