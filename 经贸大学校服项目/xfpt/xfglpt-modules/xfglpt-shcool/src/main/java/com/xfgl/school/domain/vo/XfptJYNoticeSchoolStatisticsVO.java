package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xfgl.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教育局-校服管理-质量与监管统计：学校
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XfptJYNoticeSchoolStatisticsVO {

    //存储父级部门id
    private Long parentDeptId;

    //当前区县的部门id
    private Long deptId;

    //学校名称
    @Excel(name = "学校名称")
    private String schoolName;

    //公告数量
    @Excel(name = "公告数量")
    private String ggCount;

    /**
     * 有质检公告数量及占比： 处理逻辑：有质检公告数量 + 有质检公告占比组成
     */
    //有质检公告数量
    private String hasZjCount;

    //有质检公告占比
    private String hasZjRate;

    /**
     * 无质检公告数量及占比：处理逻辑：无质检公告数量  无质检公告占比
     */

    //无质检公告数量
    private String noZjCount;

    //无质检公告占比
    private String noZjRate;

    /**
     * 首次质检公告数量及占比：处理逻辑：首次质检公告数量 + 首次质检公告占比
     */
    //首次质检公告数量
    private String hasFirstZjCount;

    //首次质检公告占比
    private String hasFirstZjRate;

    /**
     * 二次质检公告数量及占比：处理逻辑：二次质检公告数量 + 二次质检公告占比
     */
    //二次质检公告数量
    private String hasSecondZjCount;

    //二次质检公告占比
    private String hasSecondZjRate;

    @JsonIgnore
    private List<Long> teamMeetingIdList;

    @JsonIgnore
    private List<Long> inspectIdList;

    /**
     * 以下是四个查询条件：传递值是去掉百分制后的值
     * 如果需要查询 比率为 50%，则传递 0.5，100% 就传1
     */
    //首次质检占比 one
    private Float conditionFirstOne;

    //首次质检占比 two
    private Float conditionFirstTwo;

    //二次质检占比 one
    private Float conditionSecondOne;

    //二次质检占比 two
    private Float conditionSecondTwo;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
