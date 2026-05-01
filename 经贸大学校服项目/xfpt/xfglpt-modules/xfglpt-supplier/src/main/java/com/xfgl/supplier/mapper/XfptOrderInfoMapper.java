package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptOrderInfo;

/**
 * 订单主Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptOrderInfoMapper extends BaseMapper<XfptOrderInfo> {
    /**
     * 查询订单主列表
     *
     * @param xfptOrderInfo 订单主
     * @return 订单主集合
     */
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo);
}
