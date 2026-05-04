package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptProductionOrder;

/**
 * 生产单Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptProductionOrderService extends IService<XfptProductionOrder> {

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
        public XfptProductionOrder selectXfptProductionOrderById(Long id);

        /**
         * 新增生产单
         *
         * @param xfptProductionOrder 生产单
         * @return 结果
         */
        public int insertXfptProductionOrder(XfptProductionOrder xfptProductionOrder);

        /**
         * 修改生产单
         *
         * @param xfptProductionOrder 生产单
         * @return 结果
         */
        public int updateXfptProductionOrder(XfptProductionOrder xfptProductionOrder);

        /**
         * 批量删除生产单
         *
         * @param ids 需要删除的生产单主键集合
         * @return 结果
         */
        public int deleteXfptProductionOrderByIds(Long[] ids);

        /**
         * 删除生产单信息
         *
         * @param id 生产单主键
         * @return 结果
         */
        public int deleteXfptProductionOrderById(Long id);
}
