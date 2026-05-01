package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptOrderItem;

/**
 * 订单明细Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptOrderItemMapper extends BaseMapper<XfptOrderItem> {
    /**
     * 查询订单明细列表
     *
     * @param xfptOrderItem 订单明细
     * @return 订单明细集合
     */
    public List<XfptOrderItem> selectXfptOrderItemList(XfptOrderItem xfptOrderItem);
}
