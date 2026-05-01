package com.xfgl.school.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;
import com.xfgl.school.domain.vo.DeptMesVo;
import com.xfgl.school.domain.vo.OrderManagementStatisticsVo;
import com.xfgl.school.mapper.XfptProcSummaryMapper;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/OrderManagement")
public class OrderManagementController {
    @Autowired
    private XfptProcSummaryMapper xfptProcSummaryMapper;
    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;


    @PostMapping("/statistics")
    public AjaxResult statistics(@RequestBody OrderManagementStatisticsVo orderManagementStatisticsVo) {
        //查询角色，提取角色的key
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        //查询市教育局下面的部门
        List<SysDept> deptList = xfptProcSummaryMapper.selectDeptBytypeList();
        Map<Long, List<SysDept>> deptIdMap = new HashMap<>();
        //根据部门id分组
        if (CollUtil.isNotEmpty(deptList)) {
            deptIdMap = deptList.stream().collect(Collectors.groupingBy(SysDept::getDeptId));
        }
        List<OrderManagementStatisticsVo> result = new ArrayList<>();
        OrderManagementStatisticsVo allData = null;
        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            List<XfptProcSummary> list = xfptProcSummaryMapper.statistics(orderManagementStatisticsVo);
            //封装总额
            allData = setSum(list, result);
            result.add(allData);
            Set<Long> deptIds = deptIdMap.keySet();
            for (Long deptId : deptIds) {
                OrderManagementStatisticsVo orderManagementStatisticsVo1 = new OrderManagementStatisticsVo();
                List<XfptProcSummary> collect1 = list.stream().filter(item -> item.getAncestors().contains(deptId.toString())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(collect1)) {
                    orderManagementStatisticsVo1.setDistrict(deptIdMap.get(deptId).get(0).getDeptName());
                    orderManagementStatisticsVo1.setBuyCount(collect1.stream().mapToLong(XfptProcSummary::getMoney).sum());
                    orderManagementStatisticsVo1.setSchoolUniformCount(collect1.stream().mapToLong(XfptProcSummary::getCount).sum());
                } else {
                    orderManagementStatisticsVo1.setDistrict(deptIdMap.get(deptId).get(0).getDeptName());
                    orderManagementStatisticsVo1.setBuyCount(0l);
                    orderManagementStatisticsVo1.setSchoolUniformCount(0l);
                }
                orderManagementStatisticsVo1.setDeptId(deptId);
                result.add(orderManagementStatisticsVo1);
            }

        } else {
            SysUser sysUser = SecurityUtils.getLoginUser().getSysUser();
            orderManagementStatisticsVo.setDeptId(sysUser.getDeptId());
            OrderManagementStatisticsVo orderManagementStatisticsVo1 = new OrderManagementStatisticsVo();
            List<XfptProcSummary> list = xfptProcSummaryMapper.statistics(orderManagementStatisticsVo);
            if (CollUtil.isNotEmpty(list)) {
                orderManagementStatisticsVo1.setDistrict(deptIdMap.get(sysUser.getDeptId()).get(0).getDeptName());
                orderManagementStatisticsVo1.setBuyCount(list.stream().mapToLong(XfptProcSummary::getMoney).sum());
                orderManagementStatisticsVo1.setSchoolUniformCount(list.stream().mapToLong(XfptProcSummary::getCount).sum());
            } else {
                orderManagementStatisticsVo1.setDistrict(deptIdMap.get(sysUser.getDeptId()).get(0).getDeptName());
                orderManagementStatisticsVo1.setBuyCount(0l);
                orderManagementStatisticsVo1.setSchoolUniformCount(0l);
            }
            orderManagementStatisticsVo1.setDeptId(sysUser.getDeptId());
            result.add(orderManagementStatisticsVo1);
        }
        //填充购买学校数量
        //所有的购买记录
        List<XfptProcSummary> list = iXfptProcSummaryService.lambdaQuery()
                .apply(StrFormatter.format("date_format(buy_time,'%Y%m%d') >= date_format('{}','%Y%m%d') AND date_format(buy_time,'%Y%m%d') <= date_format('{}','%Y%m%d')", orderManagementStatisticsVo.getParams().get("beginTime"), orderManagementStatisticsVo.getParams().get("endTime"))).list();
        //去重重复购买学校
        List<XfptProcSummary> distinctList = list.stream()
                .collect(Collectors.toMap(
                        XfptProcSummary::getDeptId, // Key: deptId
                        p -> p,                      // Value: 对象本身
                        (existing, replacement) -> existing // 冲突时保留第一个出现的对象
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
        Map<String, List<XfptProcSummary>> collect1 = distinctList.stream().collect(Collectors.groupingBy(XfptProcSummary::getAncestors));
        Set<String> strings = collect1.keySet();
        for (OrderManagementStatisticsVo orderManagementStatisticsVo2 : result) {
            int count = 0;
            for (String string : strings) {
                if (string.contains(orderManagementStatisticsVo2.getDeptId() + "") && CollUtil.isNotEmpty(collect1.get(string))) {
                    count = count + collect1.get(string).size();
                }
            }
            orderManagementStatisticsVo2.setBuySchoolCount(Long.valueOf(count));
        }
        //封装全部数据的购买学校
        if (allData != null) {
            allData.setBuySchoolCount(Long.valueOf(distinctList.size()));
        }

        //result根据金额从大到小排序
        result.sort(new Comparator<OrderManagementStatisticsVo>() {
            @Override
            public int compare(OrderManagementStatisticsVo o1, OrderManagementStatisticsVo o2) {
                return o2.getBuyCount().compareTo(o1.getBuyCount());
            }
        });

        return AjaxResult.success(result);
    }

    @PostMapping("/statisticsTypeInfo")
    public AjaxResult statisticsTypeInfo(@RequestBody OrderManagementStatisticsVo orderManagementStatistics) {
        List<OrderManagementStatisticsVo> result = xfptProcSummaryMapper.statisticsTypeInfo(orderManagementStatistics);
        for (OrderManagementStatisticsVo orderManagementStatisticsVo : result) {
            //查询购买学校的数量
            List<XfptProcSummary> list = xfptProcSummaryMapper.selectProcSummByDept(orderManagementStatistics.getDeptId());

            Map<String, List<XfptProcSummary>> collect = list.stream().collect(Collectors.groupingBy(XfptProcSummary::getSchoolType));
            List<XfptProcSummary> list1 = collect.get(orderManagementStatisticsVo.getSchoolType());
            if (CollUtil.isNotEmpty(list1)) {
                //list1 stream流deptId根据去重
                // 方式2：使用toMap（可选择保留最后一条或根据条件选择）
                List<XfptProcSummary> distinctById2 = list1.stream()
                        .collect(Collectors.toMap(
                                XfptProcSummary::getDeptId,          // Key: ID
                                p -> p,                 // Value: 对象本身
                                (existing, replacement) -> existing, // 冲突时保留现有（第一条）
                                LinkedHashMap::new      // 保持插入顺序
                        ))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
                orderManagementStatisticsVo.setBuySchoolCount(Long.valueOf(distinctById2.size()));
            } else {
                orderManagementStatisticsVo.setBuySchoolCount(0L);
            }

            //查询学校数量
            List<DeptMesVo> list2 = xfptProcSummaryMapper.selectDeptByDept(orderManagementStatistics.getDeptId());
            Map<String, List<DeptMesVo>> collect1 = list2.stream().collect(Collectors.groupingBy(DeptMesVo::getSchoolType));
            List<DeptMesVo> deptMesVos = collect1.get(orderManagementStatisticsVo.getSchoolType());
            if (CollUtil.isNotEmpty(deptMesVos)) {
                orderManagementStatisticsVo.setSchoolCount(Long.valueOf(deptMesVos.size()));
            } else {
                orderManagementStatisticsVo.setSchoolCount(0L);
            }
        }

        //result根据金额从大到小排序
        result.sort(new Comparator<OrderManagementStatisticsVo>() {
            @Override
            public int compare(OrderManagementStatisticsVo o1, OrderManagementStatisticsVo o2) {
                return o2.getBuyCount().compareTo(o1.getBuyCount());
            }
        });
        //封装全部数据
        OrderManagementStatisticsVo orderManagementStatisticsVo = new OrderManagementStatisticsVo();
        orderManagementStatisticsVo.setDistrict("全部");
        orderManagementStatisticsVo.setBuySchoolCount(result.stream().mapToLong(OrderManagementStatisticsVo::getBuySchoolCount).sum());
        orderManagementStatisticsVo.setSchoolCount(result.stream().mapToLong(OrderManagementStatisticsVo::getSchoolCount).sum());
        orderManagementStatisticsVo.setBuyCount(result.stream().mapToLong(OrderManagementStatisticsVo::getBuyCount).sum());
        orderManagementStatisticsVo.setSchoolUniformCount(result.stream().mapToLong(OrderManagementStatisticsVo::getSchoolUniformCount).sum());
        //result往0索引插入数据
        result.add(0, orderManagementStatisticsVo);
        return AjaxResult.success(result);
    }

    @PostMapping("/statisticsInfo")
    public AjaxResult statisticsInfo(@RequestBody OrderManagementStatisticsVo orderManagementStatistics) {
        List<OrderManagementStatisticsVo> result = xfptProcSummaryMapper.statisticsInfo(orderManagementStatistics);
        return AjaxResult.success(result);
    }

    /**
     * 查询部门列表
     *
     * @return
     */
    @GetMapping("/selectDeptList")
    public AjaxResult selectDeptList() {
        Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
        //查询角色，提取角色的key
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        List<String> collect = roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
        List<SysDept> list = null;
        if (collect.contains(RoleType.SJYJ.getCode()) || SecurityUtils.getLoginUser().getSysUser().getUserName().equals("admin")) {
            list = xfptProcSummaryMapper.selectDeptList(deptId);
        } else {
            list = xfptProcSummaryMapper.selectDeptList2(deptId);
        }
        List<SysDept> collect1 = list.stream().sorted(Comparator.comparing(SysDept::getOrderNum)).collect(Collectors.toList());
        return AjaxResult.success(collect1);
    }


    private static OrderManagementStatisticsVo setSum(List<XfptProcSummary> list, List<OrderManagementStatisticsVo> result) {
        OrderManagementStatisticsVo orderManagementStatisticsVoSum = new OrderManagementStatisticsVo();
        orderManagementStatisticsVoSum.setDistrict("全部");
        orderManagementStatisticsVoSum.setBuyCount(list.stream().mapToLong(XfptProcSummary::getMoney).sum());
        orderManagementStatisticsVoSum.setSchoolUniformCount(list.stream().mapToLong(XfptProcSummary::getCount).sum());
        return orderManagementStatisticsVoSum;
    }
}
