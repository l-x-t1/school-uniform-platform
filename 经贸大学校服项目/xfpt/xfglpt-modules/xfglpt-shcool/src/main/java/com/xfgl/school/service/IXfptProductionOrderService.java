package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptGroupNotice;
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
         * 标记生产单生产完成
         *
         * @param id 生产单主键
         * @return 结果
         */
        public int finishProductionOrder(Long id);

        /**
         * 根据生产单构建发货单预填数据
         *
         * @param id 生产单主键
         * @return 发货单预填数据
         */
        public XfptDeliveryOrder generateDeliveryOrder(Long id);

        /**
         * 查询当前用户可选择的统购公告
         *
         * @return 统购公告集合
         */
        public List<XfptGroupNotice> selectProductionNoticeOptions();

        /**
         * 查询统购公告详情，并校验当前用户是否可用于创建生产单
         *
         * @param noticeId 公告主键
         * @return 统购公告
         */
        public XfptGroupNotice selectProductionNoticeDetail(Long noticeId);

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
