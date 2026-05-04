package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptOrderInfoMapper;
import com.xfgl.supplier.domain.XfptOrderInfo;
import com.xfgl.supplier.service.IXfptOrderInfoService;

/**
 * 订单主Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptOrderInfoServiceImpl extends ServiceImpl<XfptOrderInfoMapper, XfptOrderInfo> implements IXfptOrderInfoService {

    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;

    /**
     * 查询订单主列表
     *
     * @param xfptOrderInfo 订单主
     * @return 订单主
     */
    @Override
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo)
    {
        return xfptOrderInfoMapper.selectXfptOrderInfoList(xfptOrderInfo);
    }


}
