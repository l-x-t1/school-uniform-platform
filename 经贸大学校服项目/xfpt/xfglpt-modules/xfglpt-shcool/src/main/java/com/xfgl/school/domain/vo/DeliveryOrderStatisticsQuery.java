package com.xfgl.school.domain.vo;

import com.xfgl.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发货单统计查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryOrderStatisticsQuery extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 厂商ID
     */
    private Long manufacturerId;

    /**
     * 导出类型
     * 1: 待发货
     * 2: 已发货/已签收
     */
    private String exportType;
}
