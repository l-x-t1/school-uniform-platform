package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptOrderInfo;

/**
 * 订单主Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptOrderInfoService extends IService<XfptOrderInfo> {

    /**
     * 查询订单主列表
     *
     * @param xfptOrderInfo 订单主
     * @return 订单主集合
     */
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo);

}
