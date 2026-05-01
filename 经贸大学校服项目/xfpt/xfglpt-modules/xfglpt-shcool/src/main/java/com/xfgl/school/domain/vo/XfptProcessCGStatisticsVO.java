package com.xfgl.school.domain.vo;

import com.xfgl.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教育局-校服选用采购规范流程统计
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XfptProcessCGStatisticsVO {

    //部门名称
    @Excel(name = "区县名称")
    private String name;

    //班子会议纪要报备学校数
    @Excel(name = "班子会议纪要报备学校数（所）")
    private Long bzhyTotal;

    //家长购买意见征集报备学校数
    @Excel(name = "家长购买意见征集报备学校数(所)")
    private Long jzgmyjTotal;

    //选用组织名单报备学校数
    @Excel(name = "选用组织名单报备学校数(所)")
    private Long zzmdTotal;

    //选用组织选购模式报备学校数
    @Excel(name = "选用组织选购模式报备学校数(所)")
    private Long zzxgmsTotal;

    //选购结果公示报备学校数
    @Excel(name = "选购结果公示报备学校数(所)")
    private Long xgjggsTotal;

    //采购合同报备学校数
    @Excel(name = "采购合同报备学校数(所)")
    private Long cghtTotal;

    //产品质量检测报告报备学校数
    @Excel(name = "产品质量检验报告报备学校数(所)")
    private Long cpzljcTotal;

    //家长满意度总结报告报备学校数
    @Excel(name = "家长满意度总结报告报备学校数(所)")
    private Long jzmydzjbgTotal;
    @Excel(name = "总结报备数量学校数(所)")
    private Long zjbbTotal;

    @Excel(name = "采购工作总结")
    private Long cggzzjTotal;

    //当前区县的部门id
    private Long deptId;
}
