package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptOrderInfo;

/**
 * 订单信息Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptOrderInfoMapper extends BaseMapper<XfptOrderInfo> {
    /**
     * 查询订单信息列表
     *
     * @param xfptOrderInfo 订单信息
     * @return 订单信息集合
     */
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo);
}
