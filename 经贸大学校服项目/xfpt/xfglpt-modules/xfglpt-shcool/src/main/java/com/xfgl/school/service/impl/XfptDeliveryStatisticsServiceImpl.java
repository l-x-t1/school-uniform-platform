package com.xfgl.school.service.impl;

import com.xfgl.school.domain.vo.XfptDeliveryStatisticsVo;
import com.xfgl.school.mapper.XfptDeliveryStatisticsMapper;
import com.xfgl.school.service.IXfptDeliveryStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发货单统计Service业务层处理
 */
@Service
public class XfptDeliveryStatisticsServiceImpl implements IXfptDeliveryStatisticsService {

    @Autowired
    private XfptDeliveryStatisticsMapper xfptDeliveryStatisticsMapper;

    @Override
    public List<XfptDeliveryStatisticsVo> selectXfptDeliveryStatisticsList(XfptDeliveryStatisticsVo query) {
        return xfptDeliveryStatisticsMapper.selectXfptDeliveryStatisticsList(query);
    }
}
