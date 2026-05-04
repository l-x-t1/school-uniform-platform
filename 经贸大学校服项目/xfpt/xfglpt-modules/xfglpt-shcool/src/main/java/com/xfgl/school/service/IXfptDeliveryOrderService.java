package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptDeliveryOrder;

/**
 * 发货单Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptDeliveryOrderService extends IService<XfptDeliveryOrder> {

    /**
     * 查询发货单列表
     *
     * @param xfptDeliveryOrder 发货单
     * @return 发货单集合
     */
    public List<XfptDeliveryOrder> selectXfptDeliveryOrderList(XfptDeliveryOrder xfptDeliveryOrder);

        /**
         * 查询发货单
         *
         * @param id 发货单主键
         * @return 发货单
         */
        public XfptDeliveryOrder selectXfptDeliveryOrderById(Long id);

        /**
         * 新增发货单
         *
         * @param xfptDeliveryOrder 发货单
         * @return 结果
         */
        public int insertXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder);

        /**
         * 修改发货单
         *
         * @param xfptDeliveryOrder 发货单
         * @return 结果
         */
        public int updateXfptDeliveryOrder(XfptDeliveryOrder xfptDeliveryOrder);

        /**
         * 批量删除发货单
         *
         * @param ids 需要删除的发货单主键集合
         * @return 结果
         */
        public int deleteXfptDeliveryOrderByIds(Long[] ids);

        /**
         * 删除发货单信息
         *
         * @param id 发货单主键
         * @return 结果
         */
        public int deleteXfptDeliveryOrderById(Long id);
}
