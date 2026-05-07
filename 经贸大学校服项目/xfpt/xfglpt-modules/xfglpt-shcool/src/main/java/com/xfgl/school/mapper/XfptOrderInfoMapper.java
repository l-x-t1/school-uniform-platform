package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptOrderInfo;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptOrderItem;

/**
 * 订单信息Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptOrderInfoMapper extends BaseMapper<XfptOrderInfo> {
    /**
     * 查询订单信息列表
     *
     * @param xfptOrderInfo 订单信息
     * @return 订单信息集合
     */
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo);
        /**
         * 查询订单信息
         *
         * @param id 订单信息主键
         * @return 订单信息
         */
        List<XfptOrderItem> selectXfptOrderItemList(@Param("order_id") Long orderId);

        /**
         * 删除订单信息信息
         *
         * @param id 订单信息主键
         * @return 结果
         */
        int deleteXfptOrderItemByOrderId(@Param("id")Long id);

        /**
         * 批量新增订单明细
         *
         * @param xfptOrderItemList 订单明细列表
         * @return 结果
         */
        int batchInsertXfptOrderItem(List<XfptOrderItem> xfptOrderItemList);
}
