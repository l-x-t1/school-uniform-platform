package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptOrderItem;

/**
 * 订单明细Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptOrderItemService extends IService<XfptOrderItem> {

    /**
     * 查询订单明细列表
     *
     * @param xfptOrderItem 订单明细
     * @return 订单明细集合
     */
    public List<XfptOrderItem> selectXfptOrderItemList(XfptOrderItem xfptOrderItem);

}
