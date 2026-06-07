package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.vo.DeliveryOrderStatisticsQuery;
import com.xfgl.school.domain.vo.DeliveryOrderStatisticsVo;
import com.xfgl.school.domain.vo.SupplierOptionVo;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptDeliveryOrderItem;

/**
 * 发货单Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptDeliveryOrderMapper extends BaseMapper<XfptDeliveryOrder> {
    /**
     * 查询发货单列表
     *
     * @param xfptDeliveryOrder 发货单
     * @return 发货单集合
     */
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder);

    /**
     * 查询发货单详情
     *
     * @param id 发货单主键
     * @return 发货单
     */
    XfptDeliveryOrder selectXfptDeliveryOrderById(@Param("id") Long id);

    /**
     * 查询发货单明细
     *
     * @param id 发货单主键
     * @return 发货单明细
     */
    List<XfptDeliveryOrderItem> selectXfptDeliveryOrderItemById(@Param("id") Long id);

    /**
     * 删除发货单信息
     *
     * @param id 发货单主键
     * @return 结果
     */
    int deleteXfptDeliveryOrderItemByDeliveryId(@Param("id") Long id);

    /**
     * 批量新增发货单明细
     *
     * @param xfptDeliveryOrderItemList 发货单明细列表
     * @return 结果
     */
    int batchInsertXfptDeliveryOrderItem(List<XfptDeliveryOrderItem> xfptDeliveryOrderItemList);

    List<DeliveryOrderStatisticsVo> selectDeliveryOrderStatisticsList(DeliveryOrderStatisticsQuery query);

    List<SupplierOptionVo> selectDeliveryOrderSupplierOptions(@Param("schoolId") Long schoolId,
                                                              @Param("deptId") Long deptId);
}
