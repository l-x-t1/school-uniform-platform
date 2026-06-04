package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptProductionOrder;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptProductionOrderItem;

/**
 * 生产单Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptProductionOrderMapper extends BaseMapper<XfptProductionOrder> {
    /**
     * 查询生产单列表
     *
     * @param xfptProductionOrder 生产单
     * @return 生产单集合
     */
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder);

        /**
         * 查询生产单
         *
         * @param id 生产单主键
         * @return 生产单
         */
        XfptProductionOrder selectXfptProductionOrderById(@Param("id") Long id);

        /**
         * 查询生产单明细
         *
         * @param id 生产单主键
         * @return 明细列表
         */
        List<XfptProductionOrderItem> selectXfptProductionOrderItemById(@Param("id")Long id);

        /**
         * 删除生产单信息
         *
         * @param id 生产单主键
         * @return 结果
         */
        int deleteXfptProductionOrderItemByProductionId(@Param("id")Long id);

        /**
         * 批量新增生产单明细
         *
         * @param xfptProductionOrderItemList 生产单明细列表
         * @return 结果
         */
        int batchInsertXfptProductionOrderItem(List<XfptProductionOrderItem> xfptProductionOrderItemList);
}
