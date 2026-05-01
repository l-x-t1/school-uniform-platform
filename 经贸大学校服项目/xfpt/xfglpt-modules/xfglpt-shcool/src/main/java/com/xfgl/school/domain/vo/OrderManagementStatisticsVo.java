package com.xfgl.school.domain.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
public class OrderManagementStatisticsVo {

    /**
     * 区县名称
     */
    private String district;

    /**
     * 购买校服数
     */
    private Long schoolUniformCount;
    /**
     * 学校数量
     */
    private Long schoolCount;
    /**
     * 购买学校数量
     */
    private Long buySchoolCount;
    /**
     * 购买数量
     */
    private Long buyCount;

    /**
     * 部门id
     */
    private Long deptId;

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private Map<String, Object> params;

    /**
     * 学校类型
     */
    private String schoolType;

    /**
     * 公告数量
     */
    private String ggCount;

    /**
     * 是否购买 0未购买 1已购买
     */
    private String isBuy;

}
