package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xfgl.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 教育局-校服选用采购规范流程统计: 区县下的学校数据库统计vo
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XfptSchoolStatisticsVO {

    @Excel(name = "学校名称")
    private String schoolName;

    @Excel(name = "班子会议纪要是否报备")
    private String bzhyFlg;

    @Excel(name = "发布采购公告")
    private String jzgmyjFlg;

    @Excel(name = "备选公告展示")
    private String zzmdFlg;

    @Excel(name = "家长投票")
    private String xyzzxgmsFlg;

    @Excel(name = "投票结果公示")
    private String xgjggsFlg;

    @Excel(name = "签订合同")
    private String cghtFlg;

    @Excel(name = "采购结果公告")
    private String cgjgggFlg;

    @Excel(name = "产品质量检验报告是否报备")
    private String cpzljybgFlg;

    @Excel(name = "家长满意度总结报告是否报备")
    private String jzmydzjbgFlg;

    @Excel(name = "总结报表")
    private String cggzzjFlag;

    @Excel(name = "采购总结")
    private String zjbbFlg;

    private Long deptId;

    //报备类别：字典dic_bblb
    private Integer selectType;

    private String bblbValue;

    //学校类别 字典 sc_sc_type 取值
    private String schoolType;

    @JsonIgnore
    private Long parentDeptId;

    @JsonIgnore
    private List<Long> teamMeetingIds;

}
