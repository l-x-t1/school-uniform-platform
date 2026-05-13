package com.xfgl.school.mapper;

import com.xfgl.school.domain.vo.XfptDeliveryStatisticsVo;

import java.util.List;

/**
 * 发货单统计Mapper接口
 */
public interface XfptDeliveryStatisticsMapper {
    /**
     * 查询发货单统计列表
     *
     * @param query 查询条件
     * @return 发货单统计集合
     */
    List<XfptDeliveryStatisticsVo> selectXfptDeliveryStatisticsList(XfptDeliveryStatisticsVo query);
}
