package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptOrderInfo;

/**
 * 订单信息Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptOrderInfoService extends IService<XfptOrderInfo> {

    /**
     * 查询订单信息列表
     *
     * @param xfptOrderInfo 订单信息
     * @return 订单信息集合
     */
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo);

}
