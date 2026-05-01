package com.xfgl.school.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xfgl.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教育局-校服选用采购规范流程统计
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XfptJYNoticeStatisticsVO {

    //存储父级部门id
    private Long parentDeptId;

    //当前区县的部门id
    private Long deptId;

    //公告名称
    @Excel(name = "公告名称")
    private String ggName;

    //发布日期
    @Excel(name = "发布日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fbDate;

    //学校名称
    @Excel(name = "学校名称")
    private String schoolName;

    //厂商名称|成交企业名称
    @Excel(name = "厂商名称")
    private String csName;

    //首次质检
    @Excel(name = "首次质检")
    private String firstzj;

    //二次质检
    @Excel(name = "二次质检")
    private String secondzj;

    //班子会议id
    private Long teamMeetingId;

    @JsonIgnore
    private List<Long> teamMeetingIdList;

    @JsonIgnore
    private List<Long> inspectIdList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}
