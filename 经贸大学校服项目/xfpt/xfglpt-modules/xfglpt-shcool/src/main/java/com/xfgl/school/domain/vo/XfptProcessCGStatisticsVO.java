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

    //发布采购公告学校数
    @Excel(name = "发布采购公告学校数(所)")
    private Long jzgmyjTotal;

    //备选公告展示学校数
    @Excel(name = "备选公告展示学校数(所)")
    private Long zzmdTotal;

    //家长投票学校数
    @Excel(name = "家长投票学校数(所)")
    private Long zzxgmsTotal;

    //投票结果公示学校数
    @Excel(name = "投票结果公示学校数(所)")
    private Long xgjggsTotal;

    //签订合同学校数
    @Excel(name = "签订合同学校数(所)")
    private Long cghtTotal;

    //采购结果公告学校数
    @Excel(name = "采购结果公告学校数(所)")
    private Long cgjgggTotal;

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
